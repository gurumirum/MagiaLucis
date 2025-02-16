package gurumirum.magialucis.contents.block.lux;

import gurumirum.magialucis.api.capability.LinkDestination;
import gurumirum.magialucis.api.luxnet.*;
import gurumirum.magialucis.api.luxnet.behavior.LuxNodeBehavior;
import gurumirum.magialucis.contents.block.DebugTextProvider;
import gurumirum.magialucis.contents.block.RegisteredBlockEntity;
import gurumirum.magialucis.impl.luxnet.ServerLuxNet;
import gurumirum.magialucis.utils.NumberFormats;
import gurumirum.magialucis.utils.TagUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector3d;

import java.util.*;

public abstract class LuxNodeBlockEntity<B extends LuxNodeBehavior> extends RegisteredBlockEntity
		implements LuxNodeInterface, LinkDestination, LuxNodeSyncPropertyAccess, DebugTextProvider {
	private final Vector3d luxFlow = new Vector3d();
	private final Int2ObjectMap<LinkInfo> outboundLinks = new Int2ObjectOpenHashMap<>();
	private final Int2ObjectMap<LinkInfo> inboundLinks = new Int2ObjectOpenHashMap<>();
	private final Int2ObjectMap<InWorldLinkState> linkIndexToState = new Int2ObjectOpenHashMap<>();

	private int nodeId;
	private @Nullable B nodeBehavior;
	private int totalLinkWeight;

	public LuxNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
	}

	@Override
	public int luxNodeId() {
		return this.nodeId;
	}

	@Override
	public Vector3d luxFlow(Vector3d dest) {
		return dest.set(this.luxFlow);
	}

	@Override
	public @NotNull @UnmodifiableView Int2ObjectMap<LinkInfo> outboundLinks() {
		return Int2ObjectMaps.unmodifiable(this.outboundLinks);
	}

	@Override
	public @NotNull @UnmodifiableView Int2ObjectMap<LinkInfo> inboundLinks() {
		return Int2ObjectMaps.unmodifiable(this.inboundLinks);
	}

	@Override
	public @NotNull @UnmodifiableView Collection<InWorldLinkState> linkStates() {
		return Collections.unmodifiableCollection(this.linkIndexToState.values());
	}

	@Override
	public int totalLinkWeight() {
		return this.totalLinkWeight;
	}

	protected final @Nullable ServerLuxNet getLuxNet() {
		return ServerLuxNet.tryGet(this.level);
	}

	protected @Nullable InWorldLinkState getLinkState(int index) {
		return this.linkIndexToState.get(index);
	}

	protected abstract @NotNull B createNodeBehavior();

	protected @Nullable B getUpdatedNodeBehavior(@NotNull LuxNodeBehavior previous) {
		return null;
	}

	@Override
	protected void onRegister(@NotNull ServerLevel serverLevel) {
		int prevId = this.nodeId;
		this.nodeId = ServerLuxNet.register(serverLevel, this, prevId);
		if (prevId != this.nodeId) syncToClient();
	}

	@Override
	protected void onUnregister(@NotNull ServerLevel serverLevel, @NotNull UnregisterContext context) {
		if (context.isRemoved()) {
			ServerLuxNet.unregister(serverLevel, this.nodeId);
			this.nodeId = LuxNet.NO_ID;
		} else {
			ServerLuxNet.unbindInterface(serverLevel, this.nodeId);
		}
	}

	public final @NotNull B nodeBehavior() {
		if (this.nodeBehavior == null) this.nodeBehavior = createNodeBehavior();
		return this.nodeBehavior;
	}

	@Override
	public @NotNull LuxNodeBehavior updateNodeBehavior(@NotNull LuxNodeBehavior previous, boolean initial) {
		if (initial) {
			B updated = getUpdatedNodeBehavior(previous);
			if (updated != null) this.nodeBehavior = updated;
		}
		return nodeBehavior();
	}

	@Override
	public @NotNull LinkDestination.LinkTestResult linkWithSource(@NotNull LinkContext context) {
		return LinkTestResult.linkable(this.nodeId);
	}

	@Override
	public void syncLuxFlow(Vector3d amount) {
		if (this.luxFlow.equals(amount)) return;
		this.luxFlow.set(amount);
		syncToClient();
	}

	@Override
	public void syncConnection(OutboundLink outboundLinks, InboundLink inboundLinks) {
		boolean changed = false;

		if (!equals(this.outboundLinks, outboundLinks.links())) {
			changed = true;
			this.outboundLinks.clear();
			for (var e : outboundLinks.links().entrySet()) {
				this.outboundLinks.put(e.getKey().id(), e.getValue());
			}
		}
		if (!equals(this.inboundLinks, inboundLinks.links())) {
			changed = true;
			this.inboundLinks.clear();
			for (var e : inboundLinks.links().entrySet()) {
				this.inboundLinks.put(e.getKey().id(), e.getValue());
			}
		}
		if (this.totalLinkWeight != outboundLinks.totalLinkWeight()) {
			changed = true;
			this.totalLinkWeight = outboundLinks.totalLinkWeight();
		}

		if (changed) syncToClient();
	}

	@Override
	public void syncLinkStatus(@NotNull @UnmodifiableView Int2ObjectMap<InWorldLinkState> linkIndexToState) {
		if (!this.linkIndexToState.equals(linkIndexToState)) {
			this.linkIndexToState.clear();
			this.linkIndexToState.putAll(linkIndexToState);
			syncToClient();
		}
	}

	@Override
	public @Nullable BlockPos nodeBlockPos() {
		return getBlockPos();
	}

	@Override
	public void addDebugText(@NotNull List<String> list) {
		list.add("Node: #" + luxNodeId() + " [" + getBlockPos().toShortString() + "]");

		addLinkDebugText(list, outboundLinks(), false);
		addLinkDebugText(list, inboundLinks(), true);

		list.add("");
		list.add("LUX Flow: " + this.luxFlow.toString(NumberFormats.DECIMAL));
	}

	private void addLinkDebugText(List<String> list, Int2ObjectMap<LinkInfo> links, boolean inbound) {
		boolean first = true;
		int writtenEntries = 0;
		int skippedEntries = 0;
		final int limit = 5;

		for (var e : links.int2ObjectEntrySet()) {
			LinkInfo info = e.getValue();
			if (info.inWorld() == null) continue;
			if (first) {
				first = false;
				list.add("");
				list.add(inbound ? "Inbound Links:" : "Outbound Links:");
			}
			if (writtenEntries < limit) {
				BlockPos pos = inbound ? info.inWorld().origin() : BlockPos.containing(info.inWorld().linkLocation());
				list.add("#" + e.getIntKey() + " [" + pos.toShortString() + "]");
				writtenEntries++;
			} else skippedEntries++;
		}
		if (skippedEntries > 0)
			list.add("... And " + skippedEntries + " more");
	}

	@Override
	protected void load(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.load(tag, lookupProvider, context);
		this.nodeId = tag.getInt("nodeId");

		if (context.isSync()) {
			TagUtils.readVector3d(tag, "luxFlow", this.luxFlow);

			loadLinkMap(tag.getList("outboundLinks", Tag.TAG_COMPOUND), this.outboundLinks);
			loadLinkMap(tag.getList("inboundLinks", Tag.TAG_COMPOUND), this.inboundLinks);

			ListTag list = tag.getList("linkIndexToState", Tag.TAG_COMPOUND);
			this.linkIndexToState.clear();
			for (int i = 0; i < list.size(); i++) {
				CompoundTag tag2 = list.getCompound(i);
				this.linkIndexToState.put(tag2.getInt("index"), new InWorldLinkState(tag2));
			}

			this.totalLinkWeight = tag.getInt("totalLinkWeight");
		}
	}

	@Override
	protected void save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.save(tag, lookupProvider, context);
		tag.putInt("nodeId", this.nodeId);

		if (context.isSync()) {
			TagUtils.writeVector3d(tag, "luxFlow", this.luxFlow);

			tag.put("outboundLinks", saveLinkMap(this.outboundLinks));
			tag.put("inboundLinks", saveLinkMap(this.inboundLinks));

			ListTag list = new ListTag();
			for (var e : this.linkIndexToState.int2ObjectEntrySet()) {
				CompoundTag tag2 = e.getValue().save();
				tag2.putInt("index", e.getIntKey());
				list.add(tag2);
			}
			tag.put("linkIndexToState", list);

			tag.putInt("totalLinkWeight", this.totalLinkWeight);
		}
	}

	@MustBeInvokedByOverriders
	@SuppressWarnings("deprecation")
	@Override
	public void removeComponentsFromTag(CompoundTag tag) {
		tag.remove("nodeId"); // do NOT copy node id lmao
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean equals(Int2ObjectMap<LinkInfo> m1, Map<? extends LuxNode, LinkInfo> m2) {
		if (m1.size() != m2.size()) return false;
		for (var e : m2.entrySet()) {
			LinkInfo linkInfo = m1.get(e.getKey().id());
			if (!Objects.equals(linkInfo, e.getValue())) return false;
		}
		return true;
	}

	private static ListTag saveLinkMap(Int2ObjectMap<LinkInfo> map) {
		ListTag list = new ListTag();
		for (var e : map.int2ObjectEntrySet()) {
			CompoundTag tag = new CompoundTag();
			tag.putInt("id", e.getIntKey());
			if (e.getValue() != null) {
				e.getValue().save(tag);
				tag.putBoolean("hasInfo", true);
			}
			list.add(tag);
		}
		return list;
	}

	private static void loadLinkMap(ListTag list, Int2ObjectMap<LinkInfo> map) {
		map.clear();
		for (int i = 0; i < list.size(); i++) {
			CompoundTag tag = list.getCompound(i);
			map.put(tag.getInt("id"), new LinkInfo(tag));
		}
	}
}
