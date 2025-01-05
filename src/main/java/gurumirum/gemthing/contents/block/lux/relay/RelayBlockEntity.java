package gurumirum.gemthing.contents.block.lux.relay;

import gurumirum.gemthing.capability.GemStats;
import gurumirum.gemthing.contents.ModBlockEntities;
import gurumirum.gemthing.contents.block.lux.BasicRelayBlockEntity;
import gurumirum.gemthing.impl.LuxNet;
import gurumirum.gemthing.impl.LuxNetEvent;
import gurumirum.gemthing.impl.LuxNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class RelayBlockEntity extends BasicRelayBlockEntity {
	private boolean hasOutboundConnection;

	public RelayBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.RELAY.get(), pos, blockState);
	}

	@Override
	protected void initializeLuxNodeData(@NotNull LuxNet luxNet, @NotNull LuxNode node) {
		node.setStats(GemStats.BRIGHTSTONE);
		setHasOutboundConnection(!node.outboundNodes().isEmpty());
	}

	@Override
	public void connectionUpdated(LuxNetEvent.ConnectionUpdated connectionUpdated) {
		if (luxNodeId() == connectionUpdated.sourceNode()) {
			setHasOutboundConnection(connectionUpdated.destinationNode() != NO_ID);
		}
	}

	public boolean hasOutboundConnection() {
		return this.hasOutboundConnection;
	}

	private void setHasOutboundConnection(boolean value) {
		if (this.hasOutboundConnection == value) return;
		this.hasOutboundConnection = value;
		syncToClient();
	}

	@Override
	public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
		CompoundTag tag = new CompoundTag();
		saveAdditional(tag, registries);
		tag.putBoolean("hasOutboundConnection", this.hasOutboundConnection);
		return tag;
	}

	@Override
	public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
		super.handleUpdateTag(tag, lookupProvider);
		this.hasOutboundConnection = tag.getBoolean("hasOutboundConnection");
	}
}
