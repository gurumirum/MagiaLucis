package gurumirum.gemthing.impl;

import gurumirum.gemthing.GemthingMod;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Objects;

public final class LuxNode {
	public final int id;

	private @Nullable LuxNodeInterface iface;

	final IntSet adjacentInboundNodeIds = new IntOpenHashSet();
	int outboundNodeId;

	private @Nullable IntSet adjacentInboundNodeIdsView;

	public LuxNode(int id) {
		this.id = id;
	}

	public boolean bindInterface(@Nullable LuxNodeInterface iface) {
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

	public void unbindInterface() {
		bindInterface(null);
	}

	public @Nullable LuxNodeInterface iface() {
		return iface;
	}

	public @NotNull @UnmodifiableView IntSet adjacentInboundNodeIds() {
		return this.adjacentInboundNodeIdsView == null ?
				this.adjacentInboundNodeIdsView = IntSets.unmodifiable(this.adjacentInboundNodeIds) :
				this.adjacentInboundNodeIdsView;
	}

	public int outboundNode() {
		return this.outboundNodeId;
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
