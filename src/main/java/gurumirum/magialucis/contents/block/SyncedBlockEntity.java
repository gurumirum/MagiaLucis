package gurumirum.magialucis.contents.block;

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
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

public abstract class SyncedBlockEntity extends BlockEntity {
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

	@MustBeInvokedByOverriders
	protected void save(CompoundTag tag, HolderLookup.Provider lookupProvider, SaveLoadContext context) {
		super.saveAdditional(tag, lookupProvider);
	}

	@MustBeInvokedByOverriders
	protected void load(CompoundTag tag, HolderLookup.Provider lookupProvider, SaveLoadContext context) {
		super.loadAdditional(tag, lookupProvider);
	}

	@Override
	public final @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
		CompoundTag tag = new CompoundTag();
		save(tag, registries, SaveLoadContext.INITIAL_SYNC);
		return tag;
	}

	@Override
	public final Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this, (blockEntity, registryAccess) -> {
			CompoundTag tag = new CompoundTag();
			save(tag, registryAccess, SaveLoadContext.DYNAMIC_SYNC);
			return tag;
		});
	}

	@Override
	public final void onDataPacket(@NotNull Connection net, ClientboundBlockEntityDataPacket pkt,
	                               HolderLookup.@NotNull Provider lookupProvider) {
		CompoundTag tag = pkt.getTag();
		if (!tag.isEmpty()) {
			load(tag, lookupProvider, SaveLoadContext.DYNAMIC_SYNC);
		}
	}

	@Override
	public final void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
		load(tag, lookupProvider, SaveLoadContext.INITIAL_SYNC);
	}

	@Override
	protected final void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
		load(tag, lookupProvider, SaveLoadContext.SAVE_LOAD);
	}

	@Override
	protected final void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
		save(tag, lookupProvider, SaveLoadContext.SAVE_LOAD);
	}

	public enum SaveLoadContext {
		SAVE_LOAD,
		INITIAL_SYNC,
		DYNAMIC_SYNC;

		public boolean isSaveLoad() {
			return this == SAVE_LOAD;
		}

		public boolean isSync() {
			return this != SAVE_LOAD;
		}
	}
}
