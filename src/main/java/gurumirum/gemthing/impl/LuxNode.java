package gurumirum.gemthing.impl;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.capability.LuxStat;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
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

	byte color;
	double minLuxThreshold;
	double maxLuxThreshold;

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
		this.color = stat.color();
		this.minLuxThreshold = stat.minLuxThreshold();
		if (Double.isNaN(this.minLuxThreshold) || this.minLuxThreshold < 0) this.minLuxThreshold = 0;
		this.maxLuxThreshold = stat.maxLuxThreshold();
		if (Double.isNaN(this.maxLuxThreshold) || this.maxLuxThreshold < 0) this.maxLuxThreshold = 0;
		this.maxColorCharge.set(
				this.maxLuxThreshold * RGB332.rBrightness(this.color),
				this.maxLuxThreshold * RGB332.gBrightness(this.color),
				this.maxLuxThreshold * RGB332.bBrightness(this.color));
	}

	boolean bindInterface(@Nullable LuxNodeInterface iface) {
		if (iface == null || this.iface == null) {
			this.iface = iface;
		} else if (this.iface != iface) {
			GemthingMod.LOGGER.info("""
					Trying to bind a second lux node interface to node {}, ignoring
					  Existing interface: {}
					  Second interface: {}""", id, this.iface, iface);
			return false;
		}
		return true;
	}

	void unbindInterface() {
		bindInterface(null);
	}

	void trimColorCharge() {
		this.storedColorCharge.min(this.maxColorCharge);
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
