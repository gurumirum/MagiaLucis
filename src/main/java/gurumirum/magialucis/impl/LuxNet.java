package gurumirum.magialucis.impl;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.utils.LuxUtils;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector3d;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class LuxNet extends SavedData {
	public static final int NO_ID = 0;

	private static final int SYNC_DELAY = 20;

	private static final SavedData.Factory<LuxNet> FACTORY = new Factory<>(LuxNet::new, LuxNet::new);
	private static final String NAME = MagiaLucisMod.MODID + "_lux_net";

	private static final int UPDATE_CONNECTION_CYCLE = 20;

	public static @Nullable LuxNet tryGet(@Nullable Level level) {
		return level instanceof ServerLevel serverLevel ? get(serverLevel) : null;
	}

	public static @NotNull LuxNet get(@NotNull ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(FACTORY, NAME);
	}

	private final Int2ObjectMap<LuxNode> nodes = new Int2ObjectOpenHashMap<>();
	private final Map<LuxNode, NodeFlowRecord> loadedNodes = new Object2ObjectOpenHashMap<>();
	private final Set<LuxNode> sourceNodes = new ObjectOpenHashSet<>();
	private final Map<LuxNode, NodeFlowRecord> consumerNodes = new Object2ObjectOpenHashMap<>();

	private final Map<LuxNode, Map<LuxNode, @Nullable InWorldLinkInfo>> srcToDst = new Object2ObjectOpenHashMap<>();
	private final Map<LuxNode, Map<LuxNode, @Nullable InWorldLinkInfo>> dstToSrc = new Object2ObjectOpenHashMap<>();

	private final IntSet queuedLinkUpdates = new IntOpenHashSet();
	private final IntSet queuedStatUpdates = new IntOpenHashSet();
	private final IntSet queuedConnectionSyncs = new IntOpenHashSet();

	private final IntSet updateCacheSet = new IntOpenHashSet();

	private final LinkCollector linkCollector = new LinkCollector();

	private int idIncrement;

	private LuxNet() {}

	public int register(@NotNull LuxNodeInterface iface, int existingId) {
		if (existingId != NO_ID) {
			if (registerInternal(iface, existingId, true)) return existingId;
		}

		int id;

		do {
			do {
				id = ++this.idIncrement;
			} while (id == NO_ID);
		} while (!registerInternal(iface, id, false));

		if (existingId != NO_ID) {
			MagiaLucisMod.LOGGER.warn("""
					Failed to bind an interface to preexisting luxnet node, assigning a new node ID as a fallback.
					  Provided ID: {}
					  Re-mapped ID: {}""", existingId, id);
		}
		return id;
	}

	private boolean registerInternal(@NotNull LuxNodeInterface iface, int id, boolean existing) {
		if (id == NO_ID) throw new IllegalArgumentException("Cannot register node of ID 0");

		LuxNode node;

		if (existing) {
			node = this.nodes.get(id);
			if (node == null) return false;
		} else {
			node = this.nodes.computeIfAbsent(id, LuxNode::new);
		}

		return switch (node.bindInterface(iface)) {
			case SUCCESS -> {
				queueStatUpdate(id);
				queueConnectionSync(id);

				NodeFlowRecord record = this.loadedNodes.computeIfAbsent(node, n -> new NodeFlowRecord());
				record.reset();
				record.syncNow = true;

				if (iface instanceof LuxSourceNodeInterface) this.sourceNodes.add(node);
				else this.sourceNodes.remove(node);
				if (iface instanceof LuxConsumerNodeInterface) this.consumerNodes.put(node, record);
				else this.consumerNodes.remove(node);

				setDirty();

				yield true;
			}
			case FAIL -> false;
			case NO_CHANGE -> true;
		};
	}

	public void unregister(int id, boolean destroyed) {
		// TODO maybe retaining connections is possible? maybe?
		LuxNode node = get(id);
		if (node == null) return;
		if (destroyed) {
			node.bindInterface(null);
			this.nodes.remove(id);
			unlinkAll(node);
		} else {
			if (node.bindInterface(null) != LuxNode.BindInterfaceResult.SUCCESS) return;
		}

		this.loadedNodes.remove(node);
		this.sourceNodes.remove(node);
		this.consumerNodes.remove(node);
	}

	public @NotNull @UnmodifiableView Int2ObjectMap<LuxNode> nodes() {
		return Int2ObjectMaps.unmodifiable(this.nodes);
	}

	public @Nullable LuxNode get(int id) {
		return id == NO_ID ? null : this.nodes.get(id);
	}

	public boolean hasOutboundLink(@NotNull LuxNode node) {
		var m = this.srcToDst.get(node);
		return m != null && !m.isEmpty();
	}

	public boolean hasInboundLink(@NotNull LuxNode node) {
		var m = this.dstToSrc.get(node);
		return m != null && !m.isEmpty();
	}

	public @NotNull @UnmodifiableView Map<LuxNode, @Nullable InWorldLinkInfo> outboundLinks(@NotNull LuxNode node) {
		return Collections.unmodifiableMap(outboundLinks0(node));
	}

	public @NotNull @UnmodifiableView Map<LuxNode, @Nullable InWorldLinkInfo> inboundLinks(@NotNull LuxNode node) {
		return Collections.unmodifiableMap(inboundLinks0(node));
	}

	private @NotNull Map<LuxNode, @Nullable InWorldLinkInfo> outboundLinks0(@NotNull LuxNode node) {
		return this.srcToDst.computeIfAbsent(Objects.requireNonNull(node), n -> new Object2ObjectOpenHashMap<>());
	}

	private @NotNull Map<LuxNode, @Nullable InWorldLinkInfo> inboundLinks0(@NotNull LuxNode node) {
		return this.dstToSrc.computeIfAbsent(Objects.requireNonNull(node), n -> new Object2ObjectOpenHashMap<>());
	}

	public @NotNull @UnmodifiableView Set<LuxNode> nodesWithOutboundLink() {
		return Collections.unmodifiableSet(this.srcToDst.keySet());
	}

	public @NotNull @UnmodifiableView Set<LuxNode> nodesWithInboundLink() {
		return Collections.unmodifiableSet(this.dstToSrc.keySet());
	}

	private void unlinkAll(@NotNull LuxNode node) {
		if (hasOutboundLink(node)) {
			for (var n : outboundLinks0(node).keySet().toArray(new LuxNode[0])) removeLink(node, n);
		}
		if (hasInboundLink(node)) {
			for (var n : inboundLinks0(node).keySet().toArray(new LuxNode[0])) removeLink(n, node);
		}
	}

	private void removeLink(@NotNull LuxNode src, @NotNull LuxNode dst) {
		var map = this.srcToDst.get(src);
		if (map == null || !map.containsKey(dst)) return;

		map.remove(dst);
		map = this.dstToSrc.get(dst);
		if (map != null) map.remove(src);
		if (src.iface() != null) queueConnectionSync(src.id);
		if (dst.iface() != null) queueConnectionSync(dst.id);
	}

	private void addLink(@NotNull LuxNode src, @NotNull LuxNode dst, @Nullable InWorldLinkInfo linkInfo) {
		if (src == dst) return; // disallow self connection

		var outboundLinks = outboundLinks0(src);
		if (outboundLinks.containsKey(dst) && Objects.equals(outboundLinks.get(dst), linkInfo)) return;

		outboundLinks.put(Objects.requireNonNull(dst), linkInfo);
		inboundLinks0(dst).put(src, linkInfo);
		if (src.iface() != null) queueConnectionSync(src.id);
		if (dst.iface() != null) queueConnectionSync(dst.id);
	}

	public void queueLinkUpdate(int nodeId) {
		if (nodeId == NO_ID) return;
		this.queuedLinkUpdates.add(nodeId);
	}

	public void queueStatUpdate(int nodeId) {
		if (nodeId == NO_ID) return;
		this.queuedStatUpdates.add(nodeId);
	}

	public void queueConnectionSync(int nodeId) {
		if (nodeId == NO_ID) return;
		this.queuedConnectionSyncs.add(nodeId);
	}

	void update(@NotNull ServerLevel level) {
		if (level.getGameTime() % UPDATE_CONNECTION_CYCLE == 0) {
			if (!this.queuedLinkUpdates.isEmpty()) {
				updateWithQueue(this.queuedLinkUpdates, id -> {
					LuxNode node = get(id);
					if (node == null) return;
					LuxNodeInterface iface = node.iface();
					if (iface != null) updateLink(node, iface);
				});
			}
		} else {
			this.queuedLinkUpdates.clear();
			for (var e : this.nodes.int2ObjectEntrySet()) {
				LuxNode node = e.getValue();
				LuxNodeInterface iface = node.iface();
				if (iface != null) updateLink(node, iface);
			}
		}

		if (!this.queuedStatUpdates.isEmpty()) {
			updateWithQueue(this.queuedStatUpdates, id -> {
				LuxNode node = get(id);
				if (node == null) return;
				LuxNodeInterface iface = node.iface();
				if (iface != null) {
					LuxStat stat = iface.calculateNodeStat(this);
					node.setStats(stat != null ? stat : LuxStat.NULL);
					node.invokeSyncNodeStats();
					NodeFlowRecord record = this.loadedNodes.get(node);
					if (record != null) {
						record.reset();
						record.syncNow = true;
					}
				}
			});
		}

		if (!this.queuedConnectionSyncs.isEmpty()) {
			updateWithQueue(this.queuedConnectionSyncs, id -> {
				LuxNode node = get(id);
				if (node == null) return;
				LuxNodeInterface iface = node.iface();
				if (iface != null) {
					iface.syncConnection(outboundLinks(node), inboundLinks(node));
				}
			});
		}

		generateLuxSource();
		transferLux();
		consumeLuxSource();
		checkForSync();

		setDirty();
	}

	private void updateWithQueue(IntSet queue, IntConsumer function) {
		this.updateCacheSet.addAll(queue);
		queue.clear();
		this.updateCacheSet.forEach(function);
		this.updateCacheSet.clear();
	}

	@SuppressWarnings("unchecked") // bruh
	private void updateLink(LuxNode node, LuxNodeInterface iface) {
		this.linkCollector.init(node);
		iface.updateLink(this, this.linkCollector);

		var map = this.srcToDst.get(node);
		if (map != null && !map.isEmpty()) {
			for (Map.Entry<LuxNode, @Nullable InWorldLinkInfo> e : map.entrySet().toArray(new Map.Entry[0])) {
				LuxNode n = e.getKey();
				if (n.iface() == null || this.linkCollector.links.containsKey(n.id)) continue;
				removeLink(node, n);
			}
		}

		for (Int2IntMap.Entry e : this.linkCollector.links.int2IntEntrySet()) {
			int nodeId = e.getIntKey();
			int linkIndex = e.getIntValue();
			LuxNode n = get(nodeId);

			if (n == null) {
				if (linkIndex != -1) this.linkCollector.inWorldLinks.remove(linkIndex);
				continue;
			}

			InWorldLinkState linkState = this.linkCollector.inWorldLinks.get(linkIndex);
			addLink(node, n, linkState != null ? linkState.info() : null);
		}

		iface.syncLinkStatus(Int2ObjectMaps.unmodifiable(this.linkCollector.inWorldLinks));

		this.linkCollector.reset();
	}

	private final Vector3d luxTransferCache = new Vector3d();

	private void generateLuxSource() {
		for (LuxNode node : this.sourceNodes) {
			if (node == null) continue;
			LuxNodeInterface iface = node.iface();
			if (iface instanceof LuxSourceNodeInterface src) {
				src.generateLux(this.luxTransferCache);
				if (this.luxTransferCache.isFinite()) {
					LuxUtils.snapComponents(this.luxTransferCache, 0);
					node.storedColorCharge.add(this.luxTransferCache);
					node.trimColorCharge();
				} else {
					MagiaLucisMod.LOGGER.warn("Lux source node {} (iface: {}) returned an invalid value!", node.id, iface);
				}
				this.luxTransferCache.zero();
			}
		}
	}

	private void transferLux() {
		for (LuxNode node : this.nodes.values()) {
			var map = this.srcToDst.get(node);
			if (map != null) {
				NodeFlowRecord record = this.loadedNodes.get(node);
				if (record != null) record.luxFlowSum.add(node.storedColorCharge);
				node.storedColorCharge.div(map.size());
				for (var e : map.entrySet()) {
					e.getKey().incomingColorChargeCache.add(node.storedColorCharge);
				}
				node.storedColorCharge.zero();
			}
		}

		for (LuxNode node : this.nodes.values()) {
			LuxUtils.snapComponents(node.incomingColorChargeCache, node.minLuxThreshold);
			node.storedColorCharge.add(node.incomingColorChargeCache);
			node.incomingColorChargeCache.zero();
			node.trimColorCharge();
		}
	}

	private void consumeLuxSource() {
		for (var e : this.consumerNodes.entrySet()) {
			var node = e.getKey();
			NodeFlowRecord record = e.getValue();
			LuxNodeInterface iface = node.iface();
			if (iface instanceof LuxConsumerNodeInterface consumer) {
				consumer.consumeLux(this.luxTransferCache.set(node.storedColorCharge));
				if (this.luxTransferCache.isFinite()) {
					LuxUtils.snapComponents(this.luxTransferCache.max(node.storedColorCharge), 0);
					record.luxFlowSum.add(
							node.storedColorCharge.x - this.luxTransferCache.x,
							node.storedColorCharge.y - this.luxTransferCache.y,
							node.storedColorCharge.z - this.luxTransferCache.z);
					node.storedColorCharge.set(this.luxTransferCache);
					node.trimColorCharge();
				} else {
					MagiaLucisMod.LOGGER.warn("Lux consumer node {} (iface: {}) returned an invalid value!", node.id, iface);
				}
			}
		}
	}

	private void checkForSync() {
		for (var e : this.loadedNodes.entrySet()) {
			LuxNode node = e.getKey();
			NodeFlowRecord record = e.getValue();

			record.recordedTicks++;

			LuxNodeInterface iface = node.iface();
			if (iface == null) {
				MagiaLucisMod.LOGGER.warn("Lux node {} is unloaded but still in loaded node list!", node.id);
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
			ListTag list = new ListTag();
			for (var e : this.nodes.int2ObjectEntrySet()) {
				CompoundTag tag2 = e.getValue().save();
				tag2.putInt("id", e.getIntKey());
				list.add(tag2);
			}
			tag.put("nodes", list);

			list = new ListTag();
			for (var e : this.srcToDst.entrySet()) {
				LuxNode src = e.getKey();
				for (var e2 : e.getValue().entrySet()) {
					LuxNode dst = e2.getKey();
					InWorldLinkInfo linkInfo = e2.getValue();

					CompoundTag tag2 = new CompoundTag();
					tag2.putInt("src", src.id);
					tag2.putInt("dst", dst.id);
					if (linkInfo != null) tag2.put("info", linkInfo.save());
					list.add(tag2);
				}
			}
			tag.put("links", list);
		}

		return tag;
	}

	private LuxNet(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		this.idIncrement = tag.getInt("id");

		if (tag.contains("nodes", Tag.TAG_LIST)) {
			ListTag list = tag.getList("nodes", Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++) {
				CompoundTag tag2 = list.getCompound(i);
				int id = tag2.getInt("id");
				if (id != NO_ID && !this.nodes.containsKey(id)) this.nodes.put(id, new LuxNode(id, tag2));
			}
		}

		if (tag.contains("links", Tag.TAG_LIST)) {
			ListTag list = tag.getList("links", Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++) {
				CompoundTag tag2 = list.getCompound(i);
				LuxNode src = get(tag2.getInt("src"));
				LuxNode dst = get(tag2.getInt("dst"));

				if (src == null || dst == null) continue;

				InWorldLinkInfo linkInfo = tag2.contains("info", Tag.TAG_COMPOUND) ?
						new InWorldLinkInfo(tag2.getCompound("info")) : null;

				addLink(src, dst, linkInfo);
			}
		}
	}

	public void clear(ClearMode clearMode) {
		switch (clearMode) {
			case ALL -> {
				this.nodes.clear();
				this.loadedNodes.clear();
				this.sourceNodes.clear();
				this.consumerNodes.clear();

				this.srcToDst.clear();
				this.dstToSrc.clear();

				this.queuedLinkUpdates.clear();
				this.queuedStatUpdates.clear();
				this.queuedConnectionSyncs.clear();

				this.updateCacheSet.clear();

				this.linkCollector.reset();

				this.idIncrement = 0;
			}
			case UNLOADED -> this.nodes.int2ObjectEntrySet().removeIf(e -> {
				LuxNode node = e.getValue();
				if (node.iface() != null) return false;
				unlinkAll(node);
				this.loadedNodes.remove(node);
				this.sourceNodes.remove(node);
				this.consumerNodes.remove(node);
				return true;
			});
		}

		setDirty();
	}

	public final class LinkCollector {
		public final Vector3d mutableVec3d = new Vector3d();

		private final Int2IntMap links = new Int2IntOpenHashMap();
		private final Int2ObjectMap<InWorldLinkState> inWorldLinks = new Int2ObjectOpenHashMap<>();
		private @Nullable LuxNode node;

		private void init(@NotNull LuxNode node) {
			this.node = node;
		}

		private void reset() {
			this.links.clear();
			this.inWorldLinks.clear();
			this.node = null;
		}

		public void implicitLink(int nodeId) {
			if (this.node == null) throw new IllegalStateException();

			if (nodeId == NO_ID || this.node.id == nodeId ||
					!LuxNet.this.nodes.containsKey(nodeId))
				return; // disallow null source and self connection

			this.links.put(nodeId, -1);
		}

		public void inWorldLink(int linkIndex, int nodeId, @NotNull BlockPos origin, @NotNull Vec3 linkLocation) {
			if (this.node == null) throw new IllegalStateException();
			if (linkIndex < 0) throw new IllegalArgumentException("linkIndex < 0");

			// disallow null source and self connection
			boolean connected = nodeId != NO_ID && this.node.id != nodeId && LuxNet.this.nodes.containsKey(nodeId);

			this.links.put(nodeId, linkIndex);
			this.inWorldLinks.put(linkIndex, new InWorldLinkState(connected,
					Objects.requireNonNull(origin),
					Objects.requireNonNull(linkLocation)));
		}

		public void inWorldLinkFail(int linkIndex, @NotNull BlockPos origin, @NotNull Vec3 linkLocation) {
			if (this.node == null) throw new IllegalStateException();
			if (linkIndex < 0) throw new IllegalArgumentException("linkIndex < 0");

			this.inWorldLinks.put(linkIndex, new InWorldLinkState(false, Objects.requireNonNull(origin),
					Objects.requireNonNull(linkLocation)));
		}
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

	public enum ClearMode {
		ALL,
		UNLOADED;
	}
}
