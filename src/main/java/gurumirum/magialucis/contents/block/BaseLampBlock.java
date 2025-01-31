package gurumirum.magialucis.contents.block;

import gurumirum.magialucis.impl.luxnet.LuxNetCollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public abstract class BaseLampBlock extends Block {
	private static final EnumMap<Direction, VoxelShape> SHAPE = new EnumMap<>(Direction.class);
	private static final EnumMap<Direction, VoxelShape> LUX_NODE_SHAPE = new EnumMap<>(Direction.class);

	static {
		VoxelShape base = Shapes.or(
				box(4, 3, 4, 12, 13, 12),
				box(5, 2, 5, 11, 14, 11));

		SHAPE.put(Direction.DOWN, base);
		SHAPE.put(Direction.UP, Shapes.or(base,
				box(3, 0, 3, 13, 2, 13)));
		SHAPE.put(Direction.EAST, Shapes.or(base,
				box(0, 2, 5, 2, 14, 11),
				box(2, 6, 7, 4, 8, 9),
				box(2, 10, 7, 4, 12, 9)));
		SHAPE.put(Direction.WEST, Shapes.or(base,
				box(14, 2, 5, 16, 14, 11),
				box(12, 6, 7, 14, 8, 9),
				box(12, 10, 7, 14, 12, 9)));
		SHAPE.put(Direction.SOUTH, Shapes.or(base,
				box(5, 2, 0, 11, 14, 2),
				box(7, 6, 2, 9, 8, 4),
				box(7, 10, 2, 9, 12, 4)));
		SHAPE.put(Direction.NORTH, Shapes.or(base,
				box(5, 2, 14, 11, 14, 16),
				box(7, 6, 12, 9, 8, 14),
				box(7, 10, 12, 9, 12, 14)));

		base = box(4, 2, 4, 12, 14, 12);

		LUX_NODE_SHAPE.put(Direction.DOWN, base);
		LUX_NODE_SHAPE.put(Direction.UP, Shapes.or(base, box(3, 0, 3, 13, 2, 13)));
		LUX_NODE_SHAPE.put(Direction.EAST, box(0, 2, 4, 12, 14, 12));
		LUX_NODE_SHAPE.put(Direction.WEST, box(4, 2, 4, 16, 14, 12));
		LUX_NODE_SHAPE.put(Direction.SOUTH, box(4, 2, 0, 12, 14, 12));
		LUX_NODE_SHAPE.put(Direction.NORTH, box(4, 2, 4, 12, 14, 16));
	}

	public BaseLampBlock(Properties properties) {
		super(properties);

		BlockState state = defaultBlockState().setValue(FACING, Direction.DOWN).setValue(WATERLOGGED, false);
		if (hasEnabledProperty()) state = state.setValue(ENABLED, false);
		registerDefaultState(state);
	}

	protected boolean hasEnabledProperty() {
		return true;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
		if (hasEnabledProperty()) builder.add(ENABLED);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
		return defaultBlockState().setValue(FACING, context.getClickedFace())
				.setValue(WATERLOGGED, fluidState.is(Fluids.WATER));
	}

	@Override
	protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
	                                       @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return SHAPE.get(state.getValue(FACING));
	}

	@Override
	protected @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level,
	                                             @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return (context instanceof LuxNetCollisionContext ? LUX_NODE_SHAPE : SHAPE).get(state.getValue(FACING));
	}

	@Override
	protected @NotNull FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected @NotNull BlockState updateShape(BlockState state, @NotNull Direction facing,
	                                          @NotNull BlockState facingState, @NotNull LevelAccessor level,
	                                          @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	public static class Stateless extends BaseLampBlock {
		public Stateless(Properties properties) {
			super(properties);
		}

		@Override protected boolean hasEnabledProperty() {
			return false;
		}
	}
}
