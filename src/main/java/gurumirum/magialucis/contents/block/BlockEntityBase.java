package gurumirum.magialucis.contents.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

public abstract class BlockEntityBase extends BlockEntity {
	public BlockEntityBase(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
	}

	public void syncToClient() {
		Level level = getLevel();
		if (level != null) {
			BlockState state = getBlockState();
			level.sendBlockUpdated(getBlockPos(), state, state, 3);
		}
	}

	/**
	 * Overwritten to not call neighbor changed callback on nearby blocks and reevaluate redstone state. Yes that is a
	 * real thing original impl does
	 */
	@Override
	public void setChanged() {
		if (this.level != null) this.level.blockEntityChanged(getBlockPos());
	}

	public boolean updateProperty(@NotNull BooleanProperty property, boolean newValue) {
		if (isRemoved()) return false;

		boolean prevValue = getBlockState().getValue(property);

		if (newValue != prevValue) {
			if (this.level != null && !this.level.isClientSide) {
				this.level.setBlockAndUpdate(getBlockPos(), getBlockState()
						.setValue(property, newValue));
				return true;
			}
		}
		return false;
	}

	public boolean updateProperty(@NotNull IntegerProperty property, int newValue) {
		if (isRemoved()) return false;

		int prevValue = getBlockState().getValue(property);

		if (newValue != prevValue) {
			if (this.level != null && !this.level.isClientSide) {
				this.level.setBlockAndUpdate(getBlockPos(), getBlockState()
						.setValue(property, newValue));
				return true;
			}
		}
		return false;
	}

	public <E extends Enum<E> & StringRepresentable> boolean updateProperty(@NotNull EnumProperty<E> property, @NotNull E newValue) {
		if (isRemoved()) return false;

		E prevValue = getBlockState().getValue(property);

		if (!newValue.equals(prevValue)) {
			if (this.level != null && !this.level.isClientSide) {
				this.level.setBlockAndUpdate(getBlockPos(), getBlockState()
						.setValue(property, newValue));
				return true;
			}
		}
		return false;
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
