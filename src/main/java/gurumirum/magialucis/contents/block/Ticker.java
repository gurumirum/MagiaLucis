package gurumirum.magialucis.contents.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Ticker {
	private Ticker() {}

	private static final BlockEntityTicker<?> CLIENT_TICKER = (level, pos, state, blockEntity) ->
			((Client)blockEntity).updateClient(level, pos, state);
	private static final BlockEntityTicker<?> SERVER_TICKER = (level, pos, state, blockEntity) ->
			((Server)blockEntity).updateServer(level, pos, state);

	public static <T extends BlockEntity> @Nullable BlockEntityTicker<T> client(@NotNull Level level) {
		return level.isClientSide ? client() : null;
	}

	public static <T extends BlockEntity> @Nullable BlockEntityTicker<T> server(@NotNull Level level) {
		return level.isClientSide ? null : server();
	}

	public static <T extends BlockEntity> @NotNull BlockEntityTicker<T> both(@NotNull Level level) {
		return level.isClientSide ? client() : server();
	}

	@SuppressWarnings("unchecked")
	private static <T extends BlockEntity> @NotNull BlockEntityTicker<T> client() {
		return (BlockEntityTicker<T>)CLIENT_TICKER;
	}

	@SuppressWarnings("unchecked")
	private static <T extends BlockEntity> @NotNull BlockEntityTicker<T> server() {
		return (BlockEntityTicker<T>)SERVER_TICKER;
	}

	public interface Client {
		void updateClient(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state);
	}

	public interface Server {
		void updateServer(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state);
	}

	public interface Both extends Client, Server {}
}
