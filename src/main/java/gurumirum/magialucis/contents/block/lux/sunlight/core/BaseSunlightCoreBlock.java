package gurumirum.magialucis.contents.block.lux.sunlight.core;

import gurumirum.magialucis.contents.block.Ticker;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public abstract class BaseSunlightCoreBlock extends Block implements EntityBlock {
	public BaseSunlightCoreBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
				.setValue(OVERSATURATED, false)
				.setValue(FACING, Direction.UP));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(OVERSATURATED, FACING);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		return defaultBlockState()
				.setValue(FACING, context.getClickedFace())
				.setValue(OVERSATURATED, false);
	}

	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level,
	                                                                        @NotNull BlockState state,
	                                                                        @NotNull BlockEntityType<T> blockEntityType) {
		return Ticker.client(level);
	}
}
