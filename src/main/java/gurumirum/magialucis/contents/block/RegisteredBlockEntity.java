package gurumirum.magialucis.contents.block;

import gurumirum.magialucis.impl.BlockEntityTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

// Block entities that gets externally tracked for unload event because IBlockEntityExtension#onChunkUnloaded is a fucking scam
public abstract class RegisteredBlockEntity extends BlockEntityBase {
	private boolean registered;

	public RegisteredBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (this.level != null && !this.level.isClientSide) {
			BlockEntityTracker.register(this);
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		if (this.level instanceof ServerLevel serverLevel) {
			unregister(serverLevel, UnregisterContext.REMOVED);
		}
	}

	public final void register(@NotNull ServerLevel serverLevel) {
		if (!this.registered) {
			this.registered = true;
			onRegister(serverLevel);
		}
	}

	public final void unregister(@NotNull ServerLevel serverLevel, @NotNull UnregisterContext context) {
		if (this.registered) {
			this.registered = false;
			onUnregister(serverLevel, context);
		}
	}

	protected abstract void onRegister(@NotNull ServerLevel serverLevel);
	protected abstract void onUnregister(@NotNull ServerLevel serverLevel, @NotNull UnregisterContext context);

	public enum UnregisterContext {
		REMOVED,
		CHUNK_PARTIALLY_UNLOADED,
		CHUNK_FULLY_UNLOADED,
		SERVER_STOPPING;

		public boolean isRemoved() {
			return this == REMOVED;
		}

		public boolean isChunkUnloaded() {
			return this == CHUNK_PARTIALLY_UNLOADED || this == CHUNK_FULLY_UNLOADED;
		}

		public boolean isServerStopping() {
			return this == SERVER_STOPPING;
		}
	}
}
