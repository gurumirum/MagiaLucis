package gurumirum.magialucis.impl.luxnet;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.impl.luxnet.behavior.LuxNodeBehavior;
import gurumirum.magialucis.impl.luxnet.behavior.LuxNodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.Objects;

public final class LuxNode {
	public final int id;

	private @Nullable LuxNodeInterface iface;
	private LuxNodeBehavior behavior = LuxNodeBehavior.none();

	private @Nullable BlockPos lastBlockPos;

	final Vector3d charge = new Vector3d();
	final Vector3d incomingChargeCache = new Vector3d();

	public LuxNode(int id) {
		this.id = id;
	}

	public @Nullable LuxNodeInterface iface() {
		return this.iface;
	}

	public @NotNull LuxNodeBehavior behavior() {
		return this.behavior;
	}

	public @Nullable BlockPos lastBlockPos() {
		return this.iface != null ? this.iface.nodeBlockPos() : this.lastBlockPos;
	}

	public boolean isLoaded() {
		return this.iface != null;
	}

	public boolean isUnloaded() {
		return this.iface == null;
	}

	BindInterfaceResult bindInterface(@NotNull ServerLevel level, @NotNull LuxNet luxNet, @Nullable LuxNodeInterface iface) {
		if (this.iface == iface) return BindInterfaceResult.NO_CHANGE;
		else if (iface != null && this.iface != null) return BindInterfaceResult.FAIL;

		LuxNodeInterface prev = this.iface;
		this.iface = iface;

		setBehavior(level, luxNet, iface != null ?
				iface.updateNodeBehavior(this.behavior, true) : LuxNodeBehavior.none());

		if (iface != null) {
			this.lastBlockPos = null;
		} else {
			BlockPos lastBlockPos = prev.nodeBlockPos();
			this.lastBlockPos = lastBlockPos != null ? lastBlockPos.immutable() : null;
		}

		return BindInterfaceResult.SUCCESS;
	}

	boolean updateBehavior(@NotNull ServerLevel level, @NotNull LuxNet luxNet) {
		if (this.iface == null) return false;
		return setBehavior(level, luxNet, this.iface.updateNodeBehavior(this.behavior, false));
	}

	private boolean setBehavior(@NotNull ServerLevel level, @NotNull LuxNet luxNet, @Nullable LuxNodeBehavior behavior) {
		if (behavior == null) behavior = LuxNodeBehavior.none();
		if (behavior == this.behavior) return false;

		LuxNodeBehavior prevBehavior = this.behavior;
		this.behavior = behavior;

		prevBehavior.onUnbind(level, luxNet, this);
		behavior.onBind(level, luxNet, this);

		return true;
	}

	void trimColorCharge() {
		LuxStat stat = behavior().stat();
		double rMax = stat.rMaxTransfer();
		double gMax = stat.gMaxTransfer();
		double bMax = stat.bMaxTransfer();

		if (Double.isNaN(this.charge.x) || this.charge.x > rMax) this.charge.x = rMax;
		if (Double.isNaN(this.charge.y) || this.charge.y > gMax) this.charge.y = gMax;
		if (Double.isNaN(this.charge.z) || this.charge.z > bMax) this.charge.z = bMax;
	}

	CompoundTag save(HolderLookup.Provider lookupProvider) {
		CompoundTag tag = new CompoundTag();

		if (this.charge.x > 0) tag.putDouble("chargeR", this.charge.x);
		if (this.charge.y > 0) tag.putDouble("chargeG", this.charge.y);
		if (this.charge.z > 0) tag.putDouble("chargeB", this.charge.z);

		BlockPos lastBlockPos = lastBlockPos();
		if (lastBlockPos != null) tag.put("lastBlockPos", NbtUtils.writeBlockPos(lastBlockPos));

		tag.putString("behaviorType", behavior().type().id().toString());
		CompoundTag tag2 = behavior().type().writeCast(behavior(), lookupProvider);
		if (tag2 != null) tag.put("behavior", tag2);

		return tag;
	}

	LuxNode(int id, CompoundTag tag, HolderLookup.Provider lookupProvider) {
		this(id);

		this.charge.x = tag.getDouble("chargeR");
		this.charge.y = tag.getDouble("chargeG");
		this.charge.z = tag.getDouble("chargeB");

		this.lastBlockPos = NbtUtils.readBlockPos(tag, "lastBlockPos").orElse(null);
		this.behavior = readBehavior(id, tag, lookupProvider);
	}

	void initBehavior(ServerLevel level, LuxNet luxNet) {
		this.behavior.onBind(level, luxNet, this);
	}

	private static @NotNull LuxNodeBehavior readBehavior(int id, CompoundTag tag, HolderLookup.Provider lookupProvider) {
		ResourceLocation behaviorTypeId = ResourceLocation.tryParse(tag.getString("behaviorType"));
		if (behaviorTypeId != null) {
			LuxNodeType<?> type = Contents.luxNodeTypeRegistry().get(behaviorTypeId);
			if (type != null) {
				try {
					CompoundTag tag2 = tag.contains("behavior", Tag.TAG_COMPOUND) ? tag.getCompound("behavior") : null;
					LuxNodeBehavior behavior = type.read(tag2, lookupProvider);
					//noinspection ConstantValue
					if (behavior != null) return behavior;

					MagiaLucisMod.LOGGER.error("Behavior type {} on node #{} failed to construct a behavior",
							type, id);
					return LuxNodeBehavior.none();
				} catch (Exception ex) {
					MagiaLucisMod.LOGGER.error("Behavior type {} on node #{} threw an exception",
							type, id);
					return LuxNodeBehavior.none();
				}
			}
		}

		MagiaLucisMod.LOGGER.error("Unknown behavior type for lux node #{}, behavior type: {}",
				id, tag.getString("behaviorType"));
		return LuxNodeBehavior.none();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof LuxNode luxNode)) return false;
		return this.id == luxNode.id;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.id);
	}

	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();

		boolean loaded = isLoaded();
		stb.append(loaded ? '[' : '(');
		stb.append(this.id);
		stb.append(": ");

		ResourceLocation id = behavior().type().id();
		stb.append(id.getNamespace().equals(MagiaLucisMod.MODID) ? id.getPath() : id.toString());

		stb.append(loaded ? ']' : ')');
		return stb.toString();
	}

	enum BindInterfaceResult {
		SUCCESS,
		FAIL,
		NO_CHANGE
	}
}
