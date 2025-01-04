package gurumirum.gemthing.impl;

import gurumirum.gemthing.GemthingMod;
import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class LuxNet extends SavedData implements LuxNetEvent.LuxNetEventDispatcher {
	public static final int NO_ID = 0;

	private static final SavedData.Factory<LuxNet> FACTORY = new Factory<>(LuxNet::new, LuxNet::new);
	private static final String NAME = GemthingMod.MODID + "_lux_net";

	private static final int UPDATE_CONNECTION_CYCLE = 20;

	public static @Nullable LuxNet tryGet(@Nullable Level level) {
		return level instanceof ServerLevel serverLevel ? get(serverLevel) : null;
	}

	public static @NotNull LuxNet get(@NotNull ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(FACTORY, NAME);
	}

	private final Int2ObjectMap<LuxNode> nodes = new Int2ObjectOpenHashMap<>();
	private final List<LuxNetEvent> queuedEvents = new ArrayList<>();
	private final IntSet queuedLinkUpdates = new IntOpenHashSet();

	private final IntSet updateCacheSet = new IntOpenHashSet();
	private final IntSet updateCacheSet2 = new IntOpenHashSet();

	private int idIncrement;

	public LuxNet() {}
	public LuxNet(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		this.idIncrement = tag.getInt("id");
	}

	void update(@NotNull ServerLevel level) {
		this.updateCacheSet.addAll(this.queuedLinkUpdates);
		this.queuedLinkUpdates.clear();

		this.updateCacheSet.forEach(id -> {
			LuxNode node = this.nodes.get(id);
			if (node == null) return;
			LuxNodeInterface iface = node.iface();
			if (iface != null) iface.updateLink();
		});

		if (level.getGameTime() % UPDATE_CONNECTION_CYCLE == 0) {
			this.updateCacheSet2.addAll(this.nodes.keySet());
			this.updateCacheSet2.removeAll(this.updateCacheSet);

			this.updateCacheSet2.forEach(id -> {
				LuxNode node = this.nodes.get(id);
				if (node == null) return;
				LuxNodeInterface iface = node.iface();
				if (iface == null) return;
				if (node.outboundNodeId != NO_ID) {
					LuxNode dest = this.nodes.get(node.outboundNodeId);
					if (dest != null) {
						if (dest.iface() == null) return;
					}
				}
				iface.updateLink(); // do not check against unloaded node, assume it stays valid
			});

			this.updateCacheSet2.clear();
		}

		this.updateCacheSet.clear();

		// TODO calculate lux load? thingy

		// TODO dispatch queued events
		for (LuxNetEvent event : this.queuedEvents) {
			GemthingMod.LOGGER.info("Lux net event: {}", event);
			event.dispatch(this);
		}
		this.queuedEvents.clear();
	}

	@Override
	public void dispatchEvent(int nodeId, @NotNull LuxNetEvent event) {
		if (nodeId == NO_ID) return;
		LuxNode node = this.nodes.get(nodeId);
		if (node != null) {
			LuxNodeInterface iface = node.iface();
			if (iface != null) event.call(iface);
		}
	}

	public int register(@NotNull LuxNodeInterface iface, int id) {
		while (id == NO_ID) id = ++this.idIncrement;
		registerInternal(iface, id);
		return id;
	}

	private void registerInternal(@NotNull LuxNodeInterface iface, int id) {
		if (id == NO_ID) throw new IllegalArgumentException("Cannot register node of ID 0");
		this.nodes.computeIfAbsent(id, LuxNode::new).bindInterface(iface);
	}

	public void unregister(int nodeId, boolean destroyed) {
		// TODO maybe retaining connections is possible? maybe?
		if (destroyed) {
			LuxNode node = this.nodes.remove(nodeId);
			if (node != null) {
				if (node.outboundNodeId != NO_ID) link(node, NO_ID);
				IntIterator it = node.adjacentInboundNodeIds.intIterator();
				while (it.hasNext()) unlink(it.nextInt());
			}
		} else {
			LuxNode node = this.nodes.get(nodeId);
			if (node != null) node.unbindInterface();
		}
	}

	public @Nullable LuxNode get(int id) {
		return this.nodes.get(id);
	}

	public void unlink(int source) {
		link(source, NO_ID);
	}

	public void link(int source, int dest) {
		if (source == NO_ID || source == dest) return; // disallow null source and self connection
		LuxNode sourceNode = this.nodes.get(source);
		if (sourceNode != null) link(sourceNode, dest);
	}

	private void link(LuxNode sourceNode, int dest) {
		if (sourceNode.id == dest) return; // disallow self connection
		int prev = sourceNode.outboundNodeId;
		if (prev == dest) return;
		LuxNode prevNode = this.nodes.get(prev);
		LuxNode toNode = this.nodes.get(dest);

		sourceNode.outboundNodeId = dest;
		if (prevNode != null) prevNode.adjacentInboundNodeIds.remove(sourceNode.id);
		if (toNode != null) toNode.adjacentInboundNodeIds.add(sourceNode.id);

		this.queuedEvents.add(new LuxNetEvent.ConnectionUpdated(sourceNode.id, prev, dest));
	}

	public void queueLinkUpdate(int nodeId) {
		if (nodeId == NO_ID) return;
		this.queuedLinkUpdates.add(nodeId);
	}

	@Override
	public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
		// if (this.idIncrement != 0) tag.putInt("id", this.idIncrement);
		return tag;
	}
}
