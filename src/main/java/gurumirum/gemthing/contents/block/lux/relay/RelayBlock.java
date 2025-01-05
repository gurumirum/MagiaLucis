package gurumirum.gemthing.contents.block.lux.relay;

import gurumirum.gemthing.contents.block.lux.LuxNodeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RelayBlock extends LuxNodeBlock {
	public RelayBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new RelayBlockEntity(pos, state);
	}
}
