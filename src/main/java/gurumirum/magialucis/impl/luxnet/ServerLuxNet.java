package gurumirum.magialucis.impl.luxnet;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.api.luxnet.InWorldLinkState;
import gurumirum.magialucis.api.luxnet.LinkInfo;
import gurumirum.magialucis.api.luxnet.LuxNet;
import gurumirum.magialucis.api.luxnet.LuxNodeInterface;
import gurumirum.magialucis.api.luxnet.behavior.LuxConsumerNodeBehavior;
import gurumirum.magialucis.api.luxnet.behavior.LuxGeneratorNodeBehavior;
import gurumirum.magialucis.api.luxnet.behavior.LuxSpecialNodeBehavior;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ServerLuxNet extends SavedData implements LuxNet {
	private static final int SYNC_DELAY = 20;
	private static final int UPDATE_CONNECTION_CYCLE = 20;
	private static final String NAME = MagiaLucisApi.MODID + "_lux_net";

	public static @Nullable ServerLuxNet tryGet(@Nullable Level level) {
		return level instanceof ServerLevel serverLevel ? get(serverLevel) : null;
	}

	public static @NotNull ServerLuxNet get(@NotNull ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(new Factory<>(
				() -> new ServerLuxNet(level),
				(tag, provider) -> new ServerLuxNet(level, tag, provider)), NAME);
	}

	public static int tryRegister(@Nullable Level level, @NotNull LuxNodeInterface iface, int existingId) {
		return level instanceof ServerLevel serverLevel ? register(serverLevel, iface, existingId) : NO_ID;
	}

	public static int register(@NotNull ServerLevel level, @NotNull LuxNodeInterface iface, int existingId) {
		return get(level).register(iface, existingId);
	}

	public static void tryUnregister(@Nullable Level level, int id) {
		if (level instanceof ServerLevel serverLevel) unregister(serverLevel, id);
	}

	public static void unregister(@NotNull ServerLevel level, int id) {
		get(level).unregister(id);
	}

	public static void tryUnbindInterface(@Nullable Level level, int id) {
		if (level instanceof ServerLevel serverLevel) unbindInterface(serverLevel, id);
	}

	public static void unbindInterface(@NotNull ServerLevel level, int id) {
		get(level).unbindInterface(id);
	}

	private final ServerLevel level;
	private final Int2ObjectMap<ServerLuxNode> nodes = new Int2ObjectOpenHashMap<>();

	private final Int2ObjectMap<ServerOutboundLink> srcToDst = new Int2ObjectOpenHashMap<>();
	private final Int2ObjectMap<ServerInboundLink> dstToSrc = new Int2ObjectOpenHashMap<>();

	private final Map<ServerLuxNode, NodeFlowRecord> loadedNodes = new Object2ObjectOpenHashMap<>();

	private final Map<ServerLuxNode, LuxGeneratorNodeBehavior> generatorBehaviors = new Object2ObjectOpenHashMap<>();
	private final Map<ServerLuxNode, LuxConsumerNodeBehavior> consumerBehaviors = new Object2ObjectOpenHashMap<>();

	private final IntSet queuedLinkUpdates = new IntOpenHashSet();
	private final IntSet queuedLuxFlowSyncs = new IntOpenHashSet();
	private final IntSet queuedConnectionSyncs = new IntOpenHashSet();

	private final IntSet updateCacheSet = new IntOpenHashSet();
	private final List<ServerLuxNode> nodeCache = new ArrayList<>();
	private final Vector3d luxCache = new Vector3d();

	private final ServerLuxNetLinkCollector linkCollector = new ServerLuxNetLinkCollector(this);

	private int idIncrement;

	private ServerLuxNet(@NotNull ServerLevel level) {
		this.level = level;
	}

	@Override
	public int register(@NotNull LuxNodeInterface iface, int existingId) {
		if (existingId != NO_ID) {
			if (doRegister(iface, existingId, true)) return existingId;
		}

		int id;

		do {
			do {
				id = ++this.idIncrement;
			} while (id == NO_ID);
		} while (!doRegister(iface, id, false));

		if (existingId != NO_ID) {
			MagiaLucisMod.LOGGER.warn("""
					Failed to bind an interface to preexisting luxnet node, assigning a new node ID as a fallback.
					  Provided ID: {}
					  Re-mapped ID: {}""", existingId, id);
		}
		return id;
	}

	private boolean doRegister(@NotNull LuxNodeInterface iface, int id, boolean existing) {
		if (id == NO_ID) throw new IllegalArgumentException("Cannot register node of ID 0");

		ServerLuxNode node;

		if (existing) {
			node = this.nodes.get(id);
			if (node == null) return false;
		} else {
			node = this.nodes.computeIfAbsent(id, ServerLuxNode::new);
		}

		return switch (node.bindInterface(this.level, this, iface)) {
			case SUCCESS -> {
				queueConnectionSync(id);

				NodeFlowRecord record = this.loadedNodes.computeIfAbsent(node, n -> new NodeFlowRecord());
				record.reset();
				record.syncNow = true;

				updateBehaviorCache(node);
				setDirty();
				yield true;
			}
			case FAIL -> false;
			case NO_CHANGE -> true;
		};
	}

	private void updateBehaviorCache(ServerLuxNode node) {
		if (node.behavior() instanceof LuxGeneratorNodeBehavior generatorBehavior) {
			this.generatorBehaviors.put(node, generatorBehavior);
		} else {
			this.generatorBehaviors.remove(node);
		}

		if (node.behavior() instanceof LuxConsumerNodeBehavior consumerBehavior) {
			this.consumerBehaviors.put(node, consumerBehavior);
		} else {
			this.consumerBehaviors.remove(node);
		}
	}

	@Override
	public void unregister(int id) {
		ServerLuxNode node = get(id);
		if (node == null) return;
		node.bindInterface(this.level, this, null);
		this.nodes.remove(id);
		unlinkAll(node);

		this.loadedNodes.remove(node);
		this.generatorBehaviors.remove(node);
		this.consumerBehaviors.remove(node);
	}

	@Override
	public void unbindInterface(int id) {
		ServerLuxNode node = get(id);
		if (node == null) return;
		if (node.bindInterface(this.level, this, null) != ServerLuxNode.BindInterfaceResult.SUCCESS) return;

		this.loadedNodes.remove(node);
	}

	@Override
	public @NotNull @UnmodifiableView Int2ObjectMap<ServerLuxNode> nodes() {
		return Int2ObjectMaps.unmodifiable(this.nodes);
	}

	@Override
	public @Nullable ServerLuxNode get(int nodeId) {
		return nodeId == NO_ID ? null : this.nodes.get(nodeId);
	}

	@Override
	public boolean hasOutboundLink(int nodeId) {
		var m = this.srcToDst.get(nodeId);
		return m != null && !m.links.isEmpty();
	}

	@Override
	public boolean hasInboundLink(int nodeId) {
		var m = this.dstToSrc.get(nodeId);
		return m != null && !m.links.isEmpty();
	}

	@Override
	public @Nullable ServerOutboundLink outboundLinks(int nodeId) {
		ServerOutboundLink outboundLink = this.srcToDst.get(nodeId);
		if (outboundLink != null) return outboundLink;

		ServerLuxNode node = this.nodes.get(nodeId);
		if (node == null) return null;

		this.srcToDst.put(nodeId, outboundLink = new ServerOutboundLink(node));
		return outboundLink;
	}

	@Override
	public @Nullable ServerInboundLink inboundLinks(int nodeId) {
		ServerInboundLink inboundLink = this.dstToSrc.get(nodeId);
		if (inboundLink != null) return inboundLink;

		ServerLuxNode node = this.nodes.get(nodeId);
		if (node == null) return null;

		this.dstToSrc.put(nodeId, inboundLink = new ServerInboundLink(node));
		return inboundLink;
	}

	private @NotNull ServerOutboundLink outboundLinks(ServerLuxNode node) {
		return this.srcToDst.computeIfAbsent(node.id(), id -> new ServerOutboundLink(node));
	}

	private @NotNull ServerInboundLink inboundLinks(ServerLuxNode node) {
		return this.dstToSrc.computeIfAbsent(node.id(), id -> new ServerInboundLink(node));
	}

	@Override
	public @NotNull @UnmodifiableView IntSet nodesWithOutboundLink() {
		return IntSets.unmodifiable(this.srcToDst.keySet());
	}

	@Override
	public @NotNull @UnmodifiableView IntSet nodesWithInboundLink() {
		return IntSets.unmodifiable(this.dstToSrc.keySet());
	}

	private void unlinkAll(@NotNull ServerLuxNode node) {
		if (hasOutboundLink(node.id())) {
			this.nodeCache.addAll(Objects.requireNonNull(outboundLinks(node.id())).links.keySet());
			for (var n : this.nodeCache) removeLink(node, n);
			this.nodeCache.clear();
		}
		if (hasInboundLink(node.id())) {
			this.nodeCache.addAll(Objects.requireNonNull(inboundLinks(node.id())).links.keySet());
			for (var n : this.nodeCache) removeLink(n, node);
			this.nodeCache.clear();
		}
	}

	private boolean removeLink(@NotNull ServerLuxNode src, @NotNull ServerLuxNode dst) {
		Objects.requireNonNull(src);
		Objects.requireNonNull(dst);

		ServerOutboundLink outboundLinks = this.srcToDst.get(src.id());
		if (outboundLinks == null) return false;

		LinkInfo removed = outboundLinks.links.remove(dst);
		if (removed == null) return false;

		outboundLinks.setTotalLinkWeight(outboundLinks.totalLinkWeight() - removed.weight());

		ServerInboundLink inboundLinks = this.dstToSrc.get(dst.id());
		if (inboundLinks != null) inboundLinks.links.remove(src);

		if (src.iface() != null) queueConnectionSync(src.id());
		if (dst.iface() != null) queueConnectionSync(dst.id());

		return true;
	}

	private boolean addLink(@NotNull ServerLuxNode src, @NotNull ServerLuxNode dst, @NotNull LinkInfo linkInfo) {
		Objects.requireNonNull(src);
		Objects.requireNonNull(dst);
		Objects.requireNonNull(linkInfo);

		if (src == dst) return false; // disallow self connection

		ServerOutboundLink outboundLinks = outboundLinks(src);

		@Nullable LinkInfo prev = null;
		if (outboundLinks.links.containsKey(dst)) {
			prev = outboundLinks.links.get(dst);
			if (Objects.equals(prev, linkInfo)) {
				return false;
			}
		}

		outboundLinks.links.put(dst, linkInfo);

		outboundLinks.setTotalLinkWeight(outboundLinks.totalLinkWeight() +
				(prev != null ? linkInfo.weight() - prev.weight() : linkInfo.weight()));

		inboundLinks(dst).links.put(src, linkInfo);

		if (src.iface() != null) queueConnectionSync(src.id());
		if (dst.iface() != null) queueConnectionSync(dst.id());

		return true;
	}

	@Override
	public void queueLinkUpdate(int nodeId) {
		if (nodeId == NO_ID) return;
		this.queuedLinkUpdates.add(nodeId);
	}

	@Override
	public void queueLuxFlowSync(int nodeId) {
		if (nodeId == NO_ID) return;
		this.queuedLuxFlowSyncs.add(nodeId);
	}

	@Override
	public void queueConnectionSync(int nodeId) {
		if (nodeId == NO_ID) return;
		this.queuedConnectionSyncs.add(nodeId);
	}

	void update(@NotNull ServerLevel level) {
		if (level.getGameTime() % UPDATE_CONNECTION_CYCLE == 0) {
			this.queuedLinkUpdates.clear();
			for (var e : this.nodes.int2ObjectEntrySet()) {
				updateLink(e.getValue());
			}
		} else {
			if (!this.queuedLinkUpdates.isEmpty()) {
				updateWithQueue(this.queuedLinkUpdates, id -> {
					ServerLuxNode node = get(id);
					if (node != null) updateLink(node);
				});
			}
		}

		if (!this.queuedLuxFlowSyncs.isEmpty()) {
			updateWithQueue(this.queuedLuxFlowSyncs, id -> {
				ServerLuxNode node = get(id);
				if (node == null) return;
				NodeFlowRecord record = this.loadedNodes.get(node);
				if (record != null) {
					record.reset();
					record.syncNow = true;
				}
			});
		}

		if (!this.queuedConnectionSyncs.isEmpty()) {
			updateWithQueue(this.queuedConnectionSyncs, id -> {
				ServerLuxNode node = get(id);
				if (node == null) return;
				LuxNodeInterface iface = node.iface();
				if (iface != null) {
					iface.syncConnection(outboundLinks(node), inboundLinks(node));
				}
			});
		}

		for (var e : this.loadedNodes.entrySet()) {
			if (e.getKey().updateBehavior(level, this)) {
				updateBehaviorCache(e.getKey());
				e.getValue().reset();
				e.getValue().syncNow = true;
			}
		}

		generateLux(level);
		transferLux(level);
		consumeLux(level);
		checkForSync();

		setDirty();
	}

	private void updateWithQueue(IntSet queue, IntConsumer function) {
		this.updateCacheSet.addAll(queue);
		queue.clear();
		this.updateCacheSet.forEach(function);
		this.updateCacheSet.clear();
	}

	private void updateLink(ServerLuxNode node) {
		LuxNodeInterface iface = node.iface();
		if (iface == null) return;

		this.linkCollector.init(node);
		iface.updateLink(this, this.linkCollector);

		ServerOutboundLink outboundLink = outboundLinks(node);
		this.linkCollector.nodeCache.addAll(outboundLink.links.keySet());

		int voidLinkWeight = this.linkCollector.voidLinkWeight;

		for (ServerLuxNetLinkCollector.Link l : this.linkCollector.links) {
			if (l.linkIndex() != -1 && l.info() != null) {
				this.linkCollector.linkIndexToState.put(l.linkIndex(), new InWorldLinkState(l.linked(), l.weight(), l.info()));
			}

			if (!l.linked()) {
				voidLinkWeight += l.weight();
				continue;
			}

			ServerLuxNode dest = get(l.destId());
			if (dest != null) {
				addLink(node, dest, new LinkInfo(l.weight(), l.info()));
				this.linkCollector.nodeCache.remove(dest);
			}
		}

		outboundLink.setVoidLinkWeight(voidLinkWeight);

		for (ServerLuxNode n : this.linkCollector.nodeCache) {
			if (n.iface() != null) removeLink(node, n);
		}

		iface.syncLinkStatus(Int2ObjectMaps.unmodifiable(this.linkCollector.linkIndexToState));

		this.linkCollector.reset();
	}

	private final Vector3d luxTransferCache = new Vector3d();

	private void generateLux(ServerLevel level) {
		for (var e : this.generatorBehaviors.entrySet()) {
			ServerLuxNode node = e.getKey();
			LuxGeneratorNodeBehavior generatorBehavior = e.getValue();

			generatorBehavior.generateLux(level, this, node, this.luxTransferCache);
			if (this.luxTransferCache.isFinite()) {
				LuxUtils.snapComponents(this.luxTransferCache, 0);
				node.charge.add(this.luxTransferCache);
				node.trimColorCharge();
			} else {
				MagiaLucisMod.LOGGER.warn("Lux generator node {} (behavior type: {}) returned an invalid value!",
						node.id(), node.behavior().type());
			}
			this.luxTransferCache.zero();
		}
	}

	private void transferLux(ServerLevel level) {
		for (ServerLuxNode node : this.nodes.values()) {
			ServerOutboundLink outboundLink = this.srcToDst.get(node.id());
			if (outboundLink != null) {
				NodeFlowRecord record = this.loadedNodes.get(node);
				if (record != null) record.luxFlowSum.add(node.charge);
				for (var e : outboundLink.links.entrySet()) {
					double m = (double)e.getValue().weight() / outboundLink.totalLinkWeight();
					e.getKey().incomingChargeCache.add(this.luxCache
							.set(node.charge).mul(m));
				}
				node.charge.zero();
			}
		}

		for (ServerLuxNode node : this.nodes.values()) {
			if (node.behavior() instanceof LuxSpecialNodeBehavior specialNodeBehavior) {
				specialNodeBehavior.alterIncomingLux(level, this, node, node.incomingChargeCache);
			}

			LuxUtils.snapComponents(node.incomingChargeCache, node.behavior().stat().minLuxThreshold());
			node.charge.add(node.incomingChargeCache);
			node.incomingChargeCache.zero();
			node.trimColorCharge();
		}
	}

	private void consumeLux(ServerLevel level) {
		for (var e : this.consumerBehaviors.entrySet()) {
			ServerLuxNode node = e.getKey();
			LuxConsumerNodeBehavior consumerBehavior = e.getValue();

			consumerBehavior.consumeLux(level, this, node, this.luxTransferCache.set(node.charge));

			if (this.luxTransferCache.isFinite()) {
				LuxUtils.snapComponents(this.luxTransferCache.max(node.charge), 0);

				NodeFlowRecord record = this.loadedNodes.get(node);
				if (record != null) {
					record.luxFlowSum.add(
							node.charge.x - this.luxTransferCache.x,
							node.charge.y - this.luxTransferCache.y,
							node.charge.z - this.luxTransferCache.z);
				}
				node.charge.set(this.luxTransferCache);
				node.trimColorCharge();
			} else {
				MagiaLucisMod.LOGGER.warn("Lux consumer node {} (behavior type: {}) returned an invalid value!",
						node.id(), node.behavior().type());
			}
		}
	}

	private void checkForSync() {
		for (var e : this.loadedNodes.entrySet()) {
			ServerLuxNode node = e.getKey();
			NodeFlowRecord record = e.getValue();

			record.recordedTicks++;

			LuxNodeInterface iface = node.iface();
			if (iface == null) {
				MagiaLucisMod.LOGGER.warn("Lux node {} is unloaded but still in loaded node list!", node.id());
				continue;
			}

			if (!record.syncNow && record.recordedTicks < SYNC_DELAY) continue;

			iface.syncLuxFlow(record.luxFlowSum.div(record.recordedTicks));
			record.reset();
		}
	}

	@Override
	public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
		if (this.idIncrement != 0) tag.putInt("id", this.idIncrement);

		if (!this.nodes.isEmpty()) {
			int nodes = 0, links = 0;

			ListTag list = new ListTag();
			for (var e : this.nodes.int2ObjectEntrySet()) {
				CompoundTag tag2 = e.getValue().save(lookupProvider);
				tag2.putInt("id", e.getIntKey());
				list.add(tag2);
				nodes++;
			}
			tag.put("nodes", list);

			list = new ListTag();
			for (ServerOutboundLink outboundLink : this.srcToDst.values()) {
				for (var e : outboundLink.links.entrySet()) {
					ServerLuxNode dst = e.getKey();
					LinkInfo linkInfo = e.getValue();

					CompoundTag tag2 = new CompoundTag();
					linkInfo.save(tag2);
					tag2.putInt("src", outboundLink.src().id());
					tag2.putInt("dst", dst.id());
					list.add(tag2);
					links++;
				}
				if (outboundLink.voidLinkWeight() > 0) {
					CompoundTag tag2 = new CompoundTag();
					tag2.putInt("src", outboundLink.src().id());
					tag2.putInt("weight", outboundLink.voidLinkWeight());
					list.add(tag2);
				}
			}
			tag.put("links", list);

			MagiaLucisMod.LOGGER.debug("LuxNet: Saved {} nodes, {} links", nodes, links);
		}

		return tag;
	}

	private ServerLuxNet(ServerLevel level, CompoundTag tag, HolderLookup.Provider lookupProvider) {
		this.level = level;
		this.idIncrement = tag.getInt("id");

		int nodes = 0, links = 0;

		if (tag.contains("nodes", Tag.TAG_LIST)) {
			ListTag list = tag.getList("nodes", Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++) {
				CompoundTag tag2 = list.getCompound(i);
				int id = tag2.getInt("id");
				if (id != NO_ID && !this.nodes.containsKey(id)) {
					ServerLuxNode node = new ServerLuxNode(id, tag2, lookupProvider);
					this.nodes.put(id, node);
					updateBehaviorCache(node);
					nodes++;
				}
			}
		}

		if (tag.contains("links", Tag.TAG_LIST)) {
			ListTag list = tag.getList("links", Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++) {
				CompoundTag tag2 = list.getCompound(i);

				int srcId = tag2.getInt("src");
				ServerLuxNode src = get(srcId);
				if (src == null) {
					MagiaLucisMod.LOGGER.warn("Cannot restore link of {}: invalid source node", srcId);
					continue;
				}

				if (!tag2.contains("dst", Tag.TAG_INT)) {
					outboundLinks(src).setVoidLinkWeight(tag2.getInt("weight"));
					continue;
				}

				int dstId = tag2.getInt("dst");
				ServerLuxNode dst = get(dstId);

				if (dst == null) {
					MagiaLucisMod.LOGGER.warn("Cannot restore link of {} -> {}: invalid destination node", srcId, dstId);
					continue;
				}

				if (addLink(src, dst, new LinkInfo(tag2))) {
					links++;
				} else {
					MagiaLucisMod.LOGGER.warn("Cannot restore link of {} -> {}: link failed", srcId, dstId);
				}
			}
		}

		if (nodes > 0) {
			for (ServerLuxNode n : this.nodes.values()) {
				n.initBehavior(level, this);
			}
			MagiaLucisMod.LOGGER.debug("LuxNet: Restored {} nodes, {} links", nodes, links);
		}
	}

	@Override
	public void clear(@NotNull ClearMode clearMode) {
		switch (clearMode) {
			case ALL -> {
				this.nodes.clear();
				this.loadedNodes.clear();
				this.generatorBehaviors.clear();
				this.consumerBehaviors.clear();

				this.srcToDst.clear();
				this.dstToSrc.clear();

				this.queuedLinkUpdates.clear();
				this.queuedConnectionSyncs.clear();

				this.updateCacheSet.clear();

				this.linkCollector.reset();

				this.idIncrement = 0;
			}
			case UNLOADED -> this.nodes.int2ObjectEntrySet().removeIf(e -> {
				ServerLuxNode node = e.getValue();
				if (node.iface() != null) return false;
				unlinkAll(node);
				this.loadedNodes.remove(node);
				this.generatorBehaviors.remove(node);
				this.consumerBehaviors.remove(node);
				return true;
			});
		}

		setDirty();
	}

	public static final class NodeFlowRecord {
		public final Vector3d luxFlowSum = new Vector3d();
		public int recordedTicks;
		public boolean syncNow;

		public void reset() {
			this.luxFlowSum.zero();
			this.recordedTicks = 0;
			this.syncNow = false;
		}
	}
}
