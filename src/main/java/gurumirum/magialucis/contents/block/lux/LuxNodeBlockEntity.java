package gurumirum.magialucis.contents.block.lux;

import gurumirum.magialucis.capability.LuxNetLinkDestination;
import gurumirum.magialucis.contents.block.DebugTextProvider;
import gurumirum.magialucis.contents.block.SyncedBlockEntity;
import gurumirum.magialucis.impl.luxnet.InWorldLinkInfo;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxNode;
import gurumirum.magialucis.impl.luxnet.LuxNodeInterface;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector3d;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class LuxNodeBlockEntity extends SyncedBlockEntity
		implements LuxNodeInterface, LuxNetLinkDestination, LuxNodeSyncPropertyAccess, DebugTextProvider {
	private int nodeId;
	private final Vector3d luxFlow = new Vector3d();
	private final Int2ObjectMap<@Nullable InWorldLinkInfo> outboundLinks = new Int2ObjectOpenHashMap<>();
	private final Int2ObjectMap<@Nullable InWorldLinkInfo> inboundLinks = new Int2ObjectOpenHashMap<>();

	private byte color;
	private double minLuxThreshold;
	private double rMaxTransfer;
	private double gMaxTransfer;
	private double bMaxTransfer;

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
	public @NotNull @UnmodifiableView Int2ObjectMap<@Nullable InWorldLinkInfo> outboundLinks() {
		return Int2ObjectMaps.unmodifiable(this.outboundLinks);
	}

	@Override
	public @NotNull @UnmodifiableView Int2ObjectMap<@Nullable InWorldLinkInfo> inboundLinks() {
		return Int2ObjectMaps.unmodifiable(this.inboundLinks);
	}

	@Override
	public byte color() {
		return color;
	}
	@Override
	public double minLuxThreshold() {
		return minLuxThreshold;
	}
	@Override
	public double rMaxTransfer() {
		return rMaxTransfer;
	}
	@Override
	public double gMaxTransfer() {
		return gMaxTransfer;
	}
	@Override
	public double bMaxTransfer() {
		return bMaxTransfer;
	}

	protected final @Nullable LuxNet getLuxNet() {
		return LuxNet.tryGet(this.level);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		registerLuxNode();
	}

	@Override
	public void onChunkUnloaded() {
		super.onChunkUnloaded();
		unregisterLuxNode(false);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		unregisterLuxNode(true);
	}

	@Override
	public void clearRemoved() {
		super.clearRemoved();
		registerLuxNode();
	}

	protected void registerLuxNode() {
		LuxNet luxNet = LuxNet.tryGet(this.level);
		if (luxNet != null) this.nodeId = luxNet.register(this, this.nodeId);
	}

	protected void unregisterLuxNode(boolean destroyed) {
		LuxNet luxNet = LuxNet.tryGet(this.level);
		if (luxNet != null) luxNet.unregister(this.nodeId, destroyed);
		this.nodeId = NO_ID;
	}

	@Override
	public int getLinkDestinationId(int sourceId, @Nullable BlockHitResult hitResult) {
		return this.nodeId;
	}

	@Override
	public void syncLuxFlow(Vector3d amount) {
		if (this.luxFlow.equals(amount)) return;
		this.luxFlow.set(amount);
		syncToClient();
	}

	@Override
	public void syncNodeStats(byte color, double minLuxThreshold,
	                          double rMaxTransfer, double gMaxTransfer, double bMaxTransfer) {
		if (this.color == color &&
				this.minLuxThreshold == minLuxThreshold &&
				this.rMaxTransfer == rMaxTransfer &&
				this.gMaxTransfer == gMaxTransfer &&
				this.bMaxTransfer == bMaxTransfer) return;
		this.color = color;
		this.minLuxThreshold = minLuxThreshold;
		this.rMaxTransfer = rMaxTransfer;
		this.gMaxTransfer = gMaxTransfer;
		this.bMaxTransfer = bMaxTransfer;
		syncToClient();
	}

	@Override
	public void syncConnection(@NotNull @UnmodifiableView Map<LuxNode, @Nullable InWorldLinkInfo> outboundLinks,
	                           @NotNull @UnmodifiableView Map<LuxNode, @Nullable InWorldLinkInfo> inboundLinks) {
		boolean changed = false;

		if (!equals(this.outboundLinks, outboundLinks)) {
			changed = true;
			this.outboundLinks.clear();
			for (var e : outboundLinks.entrySet()) {
				this.outboundLinks.put(e.getKey().id, e.getValue());
			}
		}
		if (!equals(this.inboundLinks, inboundLinks)) {
			changed = true;
			this.inboundLinks.clear();
			for (var e : inboundLinks.entrySet()) {
				this.inboundLinks.put(e.getKey().id, e.getValue());
			}
		}

		if (changed) syncToClient();
	}

	@Override
	public void addDebugText(@NotNull List<String> list) {
		list.add("Node: #" + luxNodeId() + " [" + getBlockPos().toShortString() + "]");

		addLinkDebugText(list, outboundLinks(), false);
		addLinkDebugText(list, inboundLinks(), true);

		list.add("");
		list.add("LUX Flow: " + this.luxFlow.toString(NumberFormats.DECIMAL));
		list.add("color = " + color());
		list.add("minLuxThreshold = " + NumberFormats.DECIMAL.format(minLuxThreshold()));
		list.add("rMaxTransfer = " + NumberFormats.DECIMAL.format(rMaxTransfer()));
		list.add("gMaxTransfer = " + NumberFormats.DECIMAL.format(gMaxTransfer()));
		list.add("bMaxTransfer = " + NumberFormats.DECIMAL.format(bMaxTransfer()));
	}

	private void addLinkDebugText(List<String> list, Int2ObjectMap<@Nullable InWorldLinkInfo> links, boolean inbound) {
		boolean first = true;
		int writtenEntries = 0;
		int skippedEntries = 0;
		final int limit = 5;

		for (var e : links.int2ObjectEntrySet()) {
			if (e.getValue() == null) continue;
			if (first) {
				first = false;
				list.add("");
				list.add(inbound ? "Inbound Links:" : "Outbound Links:");
			}
			if (writtenEntries < limit) {
				BlockPos pos = inbound ? e.getValue().origin() : BlockPos.containing(e.getValue().linkLocation());
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

			this.color = tag.getByte("color");
			this.minLuxThreshold = tag.getDouble("minLuxThreshold");
			this.rMaxTransfer = tag.getDouble("rMaxTransfer");
			this.gMaxTransfer = tag.getDouble("gMaxTransfer");
			this.bMaxTransfer = tag.getDouble("bMaxTransfer");
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

			tag.putByte("color", this.color);
			tag.putDouble("minLuxThreshold", this.minLuxThreshold);
			tag.putDouble("rMaxTransfer", this.rMaxTransfer);
			tag.putDouble("gMaxTransfer", this.gMaxTransfer);
			tag.putDouble("bMaxTransfer", this.bMaxTransfer);
		}
	}

	@MustBeInvokedByOverriders
	@SuppressWarnings("deprecation")
	@Override
	public void removeComponentsFromTag(CompoundTag tag) {
		tag.remove("nodeId"); // do NOT copy node id lmao
	}

	private static boolean equals(Int2ObjectMap<@Nullable InWorldLinkInfo> m1, Map<LuxNode, @Nullable InWorldLinkInfo> m2) {
		if (m1.size() != m2.size()) return false;
		for (var e : m2.entrySet()) {
			InWorldLinkInfo linkInfo = m1.get(e.getKey().id);
			if (!Objects.equals(linkInfo, e.getValue())) return false;
		}
		return true;
	}

	private static ListTag saveLinkMap(Int2ObjectMap<@Nullable InWorldLinkInfo> map) {
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

	private static void loadLinkMap(ListTag list, Int2ObjectMap<@Nullable InWorldLinkInfo> map) {
		map.clear();
		for (int i = 0; i < list.size(); i++) {
			CompoundTag tag = list.getCompound(i);
			map.put(tag.getInt("id"), tag.getBoolean("hasInfo") ? new InWorldLinkInfo(tag) : null);
		}
	}
}
