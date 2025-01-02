package gurumirum.gemthing.contents.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Ticker {
	default void updateClient(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {}
	default void updateServer(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {}

	BlockEntityTicker<?> CLIENT_TICKER = (level, pos, state, blockEntity) ->
			((Ticker)blockEntity).updateClient(level, pos, state);
	BlockEntityTicker<?> SERVER_TICKER = (level, pos, state, blockEntity) ->
			((Ticker)blockEntity).updateServer(level, pos, state);

	static <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(@NotNull Level level, boolean hasClient, boolean hasServer) {
		return (level.isClientSide ? hasClient : hasServer) ? getTicker(level.isClientSide) : null;
	}

	@SuppressWarnings("unchecked")
	static <T extends BlockEntity> BlockEntityTicker<T> getTicker(boolean clientSide) {
		return (BlockEntityTicker<T>)(clientSide ? CLIENT_TICKER : SERVER_TICKER);
	}

	interface OneSided extends Ticker {
		void update(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state);

		@Override
		default void updateClient(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
			update(level, pos, state);
		}

		@Override
		default void updateServer(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
			update(level, pos, state);
		}
	}
}
