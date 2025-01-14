package gurumirum.magialucis.contents.block.lux.ambercore;

import gurumirum.magialucis.contents.block.Ticker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static gurumirum.magialucis.contents.block.ModBlockStateProps.OVERSATURATED;
import static gurumirum.magialucis.contents.block.ModBlockStateProps.SKYLIGHT_INTERFERENCE;

public class AmberCoreBlock extends Block implements EntityBlock {
	public AmberCoreBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
				.setValue(SKYLIGHT_INTERFERENCE, false)
				.setValue(OVERSATURATED, false));
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new AmberCoreBlockEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(SKYLIGHT_INTERFERENCE, OVERSATURATED);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level,
	                                                                        @NotNull BlockState state,
	                                                                        @NotNull BlockEntityType<T> blockEntityType) {
		return Ticker.server(level);
	}
}
