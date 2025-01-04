package gurumirum.gemthing.contents.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class SyncedBlockEntity extends BlockEntity implements BlockEntityUtils {
	public SyncedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
	}

	public void syncToClient() {
		Level level = getLevel();
		if (level != null) {
			BlockState state = getBlockState();
			level.sendBlockUpdated(getBlockPos(), state, state, 3);
		}
	}

	@Override
	public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
		CompoundTag tag = new CompoundTag();
		saveAdditional(tag, registries);
		return tag;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(@NotNull Connection net, ClientboundBlockEntityDataPacket pkt,
	                         HolderLookup.@NotNull Provider lookupProvider) {
		CompoundTag compoundtag = pkt.getTag();
		if (!compoundtag.isEmpty()) {
			handleUpdateTag(compoundtag, lookupProvider);
		}
	}

	@Override
	public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
		super.handleUpdateTag(tag, lookupProvider);
	}
}
