package gurumirum.gemthing.impl;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.capability.LuxStat;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector3d;

import java.util.Objects;

public final class LuxNode {
	public final int id;

	private @Nullable LuxNodeInterface iface;

	final Vector3d storedColorCharge = new Vector3d();
	final Vector3d incomingColorChargeCache = new Vector3d();

	final Vector3d maxColorCharge = new Vector3d();

	final IntSet inboundNodes = new IntOpenHashSet();
	final IntSet outboundNodes = new IntOpenHashSet();

	private @Nullable IntSet inboundNodesView;
	private @Nullable IntSet outboundNodesView;

	double minLuxThreshold;

	public LuxNode(int id) {
		this.id = id;
	}

	public @Nullable LuxNodeInterface iface() {
		return iface;
	}

	public @NotNull @UnmodifiableView IntSet inboundNodes() {
		return this.inboundNodesView == null ?
				this.inboundNodesView = IntSets.unmodifiable(this.inboundNodes) :
				this.inboundNodesView;
	}

	public @NotNull @UnmodifiableView IntSet outboundNodes() {
		return this.outboundNodesView == null ?
				this.outboundNodesView = IntSets.unmodifiable(this.outboundNodes) :
				this.outboundNodesView;
	}

	public void setStats(@NotNull LuxStat stat) {
		setStats(stat.minLuxThreshold(), stat.rMaxTransfer(), stat.gMaxTransfer(), stat.bMaxTransfer());
	}

	private void setStats(double minLuxThreshold, double rMaxTransfer, double gMaxTransfer, double bMaxTransfer) {
		this.minLuxThreshold = Double.isNaN(minLuxThreshold) ? 0 : Math.max(minLuxThreshold, 0);
		this.maxColorCharge.x = Double.isNaN(rMaxTransfer) ? 0 : Math.max(rMaxTransfer, 0);
		this.maxColorCharge.y = Double.isNaN(gMaxTransfer) ? 0 : Math.max(gMaxTransfer, 0);
		this.maxColorCharge.z = Double.isNaN(bMaxTransfer) ? 0 : Math.max(bMaxTransfer, 0);

		trimColorCharge();
	}

	void bindInterface(LuxNet luxNet, @Nullable LuxNodeInterface iface) {
		if (iface == null || this.iface == null) {
			this.iface = iface;
			if (iface != null) iface.onBind(luxNet, this);
		} else if (this.iface != iface) {
			GemthingMod.LOGGER.info("""
					Trying to bind a second lux node interface to node {}, ignoring
					  Existing interface: {}
					  Second interface: {}""", id, this.iface, iface);
		}
	}

	void trimColorCharge() {
		if (Double.isNaN(this.storedColorCharge.x) || this.storedColorCharge.x > this.maxColorCharge.x)
			this.storedColorCharge.x = this.maxColorCharge.x;
		if (Double.isNaN(this.storedColorCharge.y) || this.storedColorCharge.y > this.maxColorCharge.y)
			this.storedColorCharge.y = this.maxColorCharge.y;
		if (Double.isNaN(this.storedColorCharge.z) || this.storedColorCharge.z > this.maxColorCharge.z)
			this.storedColorCharge.z = this.maxColorCharge.z;
	}

	CompoundTag save() {
		CompoundTag tag = new CompoundTag();

		tag.putIntArray("outboundNodes", this.outboundNodes.toIntArray());

		if (this.storedColorCharge.x > 0) tag.putDouble("chargeR", this.storedColorCharge.x);
		if (this.storedColorCharge.y > 0) tag.putDouble("chargeG", this.storedColorCharge.y);
		if (this.storedColorCharge.z > 0) tag.putDouble("chargeB", this.storedColorCharge.z);

		if (this.minLuxThreshold > 0) tag.putDouble("minLuxThreshold", this.minLuxThreshold);
		if (this.maxColorCharge.x > 0) tag.putDouble("maxColorChargeR", this.maxColorCharge.x);
		if (this.maxColorCharge.y > 0) tag.putDouble("maxColorChargeG", this.maxColorCharge.y);
		if (this.maxColorCharge.z > 0) tag.putDouble("maxColorChargeB", this.maxColorCharge.z);

		return tag;
	}

	LuxNode(int id, CompoundTag tag) {
		this(id);

		for (int i : tag.getIntArray("outboundNodes")) this.outboundNodes.add(i);

		this.storedColorCharge.x = tag.getDouble("chargeR");
		this.storedColorCharge.y = tag.getDouble("chargeG");
		this.storedColorCharge.z = tag.getDouble("chargeB");

		setStats(
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
}
