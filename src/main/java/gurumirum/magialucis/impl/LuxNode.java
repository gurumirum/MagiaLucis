package gurumirum.magialucis.impl;

import gurumirum.magialucis.capability.LuxStat;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.Objects;

public final class LuxNode {
	public final int id;

	private @Nullable LuxNodeInterface iface;

	final Vector3d storedColorCharge = new Vector3d();
	final Vector3d incomingColorChargeCache = new Vector3d();

	private final Vector3d maxColorCharge = new Vector3d();

	private byte color;
	double minLuxThreshold;

	public LuxNode(int id) {
		this.id = id;
	}

	public @Nullable LuxNodeInterface iface() {
		return iface;
	}

	public void setStats(@NotNull LuxStat stat) {
		setStats(stat.color(), stat.minLuxThreshold(), stat.rMaxTransfer(), stat.gMaxTransfer(), stat.bMaxTransfer());
	}

	private void setStats(byte color, double minLuxThreshold, double rMaxTransfer, double gMaxTransfer, double bMaxTransfer) {
		this.color = color;
		this.minLuxThreshold = Double.isNaN(minLuxThreshold) ? 0 : Math.max(minLuxThreshold, 0);
		this.maxColorCharge.x = Double.isNaN(rMaxTransfer) ? 0 : Math.max(rMaxTransfer, 0);
		this.maxColorCharge.y = Double.isNaN(gMaxTransfer) ? 0 : Math.max(gMaxTransfer, 0);
		this.maxColorCharge.z = Double.isNaN(bMaxTransfer) ? 0 : Math.max(bMaxTransfer, 0);

		trimColorCharge();
	}

	BindInterfaceResult bindInterface(@Nullable LuxNodeInterface iface) {
		if (this.iface == iface) return BindInterfaceResult.NO_CHANGE;
		else if (iface != null && this.iface != null) return BindInterfaceResult.FAIL;
		this.iface = iface;
		return BindInterfaceResult.SUCCESS;
	}

	void trimColorCharge() {
		if (Double.isNaN(this.storedColorCharge.x) || this.storedColorCharge.x > this.maxColorCharge.x)
			this.storedColorCharge.x = this.maxColorCharge.x;
		if (Double.isNaN(this.storedColorCharge.y) || this.storedColorCharge.y > this.maxColorCharge.y)
			this.storedColorCharge.y = this.maxColorCharge.y;
		if (Double.isNaN(this.storedColorCharge.z) || this.storedColorCharge.z > this.maxColorCharge.z)
			this.storedColorCharge.z = this.maxColorCharge.z;
	}

	void invokeSyncNodeStats() {
		LuxNodeInterface iface = this.iface;
		if (iface != null) {
			iface.syncNodeStats(this.color, this.minLuxThreshold,
					this.maxColorCharge.x, this.maxColorCharge.y, this.maxColorCharge.z);
		}
	}

	CompoundTag save() {
		CompoundTag tag = new CompoundTag();

		if (this.storedColorCharge.x > 0) tag.putDouble("chargeR", this.storedColorCharge.x);
		if (this.storedColorCharge.y > 0) tag.putDouble("chargeG", this.storedColorCharge.y);
		if (this.storedColorCharge.z > 0) tag.putDouble("chargeB", this.storedColorCharge.z);

		tag.putByte("color", this.color);
		if (this.minLuxThreshold > 0) tag.putDouble("minLuxThreshold", this.minLuxThreshold);
		if (this.maxColorCharge.x > 0) tag.putDouble("maxColorChargeR", this.maxColorCharge.x);
		if (this.maxColorCharge.y > 0) tag.putDouble("maxColorChargeG", this.maxColorCharge.y);
		if (this.maxColorCharge.z > 0) tag.putDouble("maxColorChargeB", this.maxColorCharge.z);

		return tag;
	}

	LuxNode(int id, CompoundTag tag) {
		this(id);

		this.storedColorCharge.x = tag.getDouble("chargeR");
		this.storedColorCharge.y = tag.getDouble("chargeG");
		this.storedColorCharge.z = tag.getDouble("chargeB");

		setStats(
				tag.getByte("color"),
				tag.getDouble("minLuxThreshold"),
				tag.getDouble("maxColorChargeR"),
				tag.getDouble("maxColorChargeG"),
				tag.getDouble("maxColorChargeB"));

		trimColorCharge();
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

	enum BindInterfaceResult {
		SUCCESS,
		FAIL,
		NO_CHANGE
	}
}
