package gurumirum.gemthing.impl;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.utils.LuxUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class LuxNet extends SavedData implements LuxNetEvent.LuxNetEventDispatcher {
	public static final int NO_ID = 0;

	private static final SavedData.Factory<LuxNet> FACTORY = new Factory<>(LuxNet::new, LuxNet::new);
	private static final String NAME = GemthingMod.MODID + "_lux_net";

	private static final int UPDATE_CONNECTION_CYCLE = 20;
	private static final int TRANSFER_PER_TICK = 3;

	public static @Nullable LuxNet tryGet(@Nullable Level level) {
		return level instanceof ServerLevel serverLevel ? get(serverLevel) : null;
	}

	public static @NotNull LuxNet get(@NotNull ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(FACTORY, NAME);
	}

	private final Int2ObjectMap<LuxNode> nodes = new Int2ObjectOpenHashMap<>();
	private final Set<LuxNode> sourceNodes = new ObjectOpenHashSet<>();
	private final Set<LuxNode> consumerNodes = new ObjectOpenHashSet<>();

	private final IntSet queuedLinkUpdates = new IntOpenHashSet();
	private final List<LuxNetEvent> queuedEvents = new ArrayList<>();

	private final IntSet updateCacheSet = new IntOpenHashSet();
	private final IntSet updateCacheSet2 = new IntOpenHashSet();
	private final IntSet updateCacheSet3 = new IntOpenHashSet();

	private final LinkCollector linkCollector = new LinkCollector();

	private int idIncrement;

	public LuxNet() {}
	public LuxNet(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		this.idIncrement = tag.getInt("id");
	}

	public int register(@NotNull LuxNodeInterface iface, int id) {
		while (id == NO_ID) {
			id = ++this.idIncrement;
			setDirty();
		}
		registerInternal(iface, id);
		return id;
	}

	private void registerInternal(@NotNull LuxNodeInterface iface, int id) {
		if (id == NO_ID) throw new IllegalArgumentException("Cannot register node of ID 0");
		LuxNode node = this.nodes.computeIfAbsent(id, LuxNode::new);
		node.bindInterface(iface);
		if (iface instanceof LuxSourceNodeInterface) this.sourceNodes.add(node);
		else this.sourceNodes.remove(node);
		if (iface instanceof LuxConsumerNodeInterface) this.consumerNodes.add(node);
		else this.consumerNodes.remove(node);
	}

	public void unregister(int id, boolean destroyed) {
		// TODO maybe retaining connections is possible? maybe?
		LuxNode node = get(id);
		if (node == null) return;
		if (destroyed) {
			this.nodes.remove(id);
			node.inboundNodes.forEach(i -> {
				LuxNode node2 = get(i);
				if (node2 != null) removeLink(node2, node.id);
			});
			node.outboundNodes.forEach(i -> removeLink(node, i));
		} else {
			node.unbindInterface();
		}
		this.sourceNodes.remove(node);
		this.consumerNodes.remove(node);
	}

	public @Nullable LuxNode get(int id) {
		return id == NO_ID ? null : this.nodes.get(id);
	}

	private void removeLink(LuxNode node, int dest) {
		LuxNode destNode = get(dest);
		if (destNode != null && node.outboundNodes.remove(dest)) {
			destNode.inboundNodes.remove(node.id);
			this.queuedEvents.add(new LuxNetEvent.ConnectionUpdated(node.id, dest, true));
		}
	}

	private void addLink(LuxNode node, int dest) {
		if (node.id == dest) return; // disallow self connection
		LuxNode destNode = get(dest);
		if (destNode != null && node.outboundNodes.add(dest)) {
			destNode.inboundNodes.add(node.id);
			this.queuedEvents.add(new LuxNetEvent.ConnectionUpdated(node.id, dest, true));
		}
	}

	public void queueLinkUpdate(int nodeId) {
		if (nodeId == NO_ID) return;
		this.queuedLinkUpdates.add(nodeId);
	}

	void update(@NotNull ServerLevel level) {
		this.updateCacheSet.addAll(this.queuedLinkUpdates);
		this.queuedLinkUpdates.clear();

		this.updateCacheSet.forEach(id -> {
			LuxNode node = get(id);
			if (node == null) return;
			LuxNodeInterface iface = node.iface();
			if (iface != null) updateLink(node, iface);
		});

		if (level.getGameTime() % UPDATE_CONNECTION_CYCLE == 0) {
			this.updateCacheSet2.addAll(this.nodes.keySet());
			this.updateCacheSet2.removeAll(this.updateCacheSet);

			this.updateCacheSet2.forEach(id -> {
				LuxNode node = get(id);
				if (node == null) return;
				LuxNodeInterface iface = node.iface();
				if (iface == null) return;

				// do not check against unloaded node, assume it stays valid
				boolean outboundNodeUnloaded = node.outboundNodes.intStream().anyMatch(i -> {
					LuxNode dest = get(i);
					return dest != null && dest.iface() == null;
				});

				if (!outboundNodeUnloaded) updateLink(node, iface);
			});

			this.updateCacheSet2.clear();
		}
		this.updateCacheSet.clear();

		generateLuxSource();
		for (int i = 0; i < TRANSFER_PER_TICK; i++) {
			transferLux();
		}
		consumeLuxSource();

		for (LuxNetEvent event : this.queuedEvents) {
			GemthingMod.LOGGER.info("Lux net event: {}", event);
			event.dispatch(this);
		}
		this.queuedEvents.clear();
	}

	private void updateLink(LuxNode node, LuxNodeInterface iface) {
		this.linkCollector.init(node);
		iface.updateLink(this, this.linkCollector);
		this.updateCacheSet3.addAll(node.outboundNodes);
		this.updateCacheSet3.forEach(id -> {
			if (this.linkCollector.links.contains(id)) return;
			removeLink(node, id);
		});
		this.linkCollector.links.forEach(id -> {
			if (this.updateCacheSet3.contains(id)) return;
			addLink(node, id);
		});
		this.updateCacheSet3.clear();
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
					node.storedColorCharge.add(this.luxTransferCache).max(node.maxColorCharge);
					node.trimColorCharge();
				} else {
					GemthingMod.LOGGER.warn("Lux source node {} (iface: {}) returned an invalid value!", node.id, iface);
				}
				this.luxTransferCache.zero();
			}
		}
	}

	private void transferLux() {
		for (LuxNode node : this.nodes.values()) {
			int size = node.outboundNodes.size();
			node.outboundNodes.forEach(id2 -> {
				LuxNode node2 = get(id2);
				if (node2 == null) return;
				node2.incomingColorChargeCache.add(node.storedColorCharge.div(size));
			});
		}

		for (LuxNode node : this.nodes.values()) {
			LuxUtils.snapComponents(node.incomingColorChargeCache, node.minLuxThreshold);
			node.storedColorCharge.add(node.incomingColorChargeCache).max(node.maxColorCharge);
			node.incomingColorChargeCache.zero();
			node.trimColorCharge();
		}
	}

	private void consumeLuxSource() {
		for (LuxNode node : this.consumerNodes) {
			LuxNodeInterface iface = node.iface();
			if (iface instanceof LuxConsumerNodeInterface consumer) {
				consumer.consumeLux(this.luxTransferCache.set(node.storedColorCharge));
				if (this.luxTransferCache.isFinite()) {
					LuxUtils.snapComponents(node.storedColorCharge.min(this.luxTransferCache), 0);
				} else {
					GemthingMod.LOGGER.warn("Lux consumer node {} (iface: {}) returned an invalid value!", node.id, iface);
				}
			}
		}
	}

	@Override
	public void dispatchEvent(int nodeId, @NotNull LuxNetEvent event) {
		if (nodeId == NO_ID) return;
		LuxNode node = get(nodeId);
		if (node != null) {
			LuxNodeInterface iface = node.iface();
			if (iface != null) event.call(iface);
		}
	}

	@Override
	public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
		if (this.idIncrement != 0) tag.putInt("id", this.idIncrement);
		return tag;
	}

	public class LinkCollector {
		public final Vector3d mutableVec3d = new Vector3d();

		private final IntSet links = new IntOpenHashSet();
		private @Nullable LuxNode node;

		private void init(@NotNull LuxNode node) {
			this.node = node;
		}

		private void reset() {
			this.links.clear();
			this.node = null;
		}

		public void link(int dest) {
			if (this.node == null) throw new IllegalStateException();
			if (dest == NO_ID || this.node.id == dest ||
					!LuxNet.this.nodes.containsKey(dest))
				return; // disallow null source and self connection
			this.links.add(dest);
		}
	}
}
