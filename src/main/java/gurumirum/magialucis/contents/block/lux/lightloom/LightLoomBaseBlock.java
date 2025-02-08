package gurumirum.magialucis.contents.block.lux.lightloom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class LightLoomBaseBlock extends Block {
	private static final EnumMap<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

	static {
		VoxelShape center = box(6, 6, 6, 10, 10, 10);

		SHAPES.put(Direction.NORTH, Shapes.or(
				center,
				box(1, 7, 7, 2, 9, 9),
				box(14, 7, 7, 15, 9, 9),
				box(13, 3, 7, 14, 9, 9),
				box(2, 3, 7, 3, 9, 9),
				box(11, 3, 7, 13, 4, 9),
				box(7, 3, 11, 9, 4, 13),
				box(7, 3, 13, 9, 11, 14),
				box(7, 9, 14, 9, 11, 15),
				box(3, 3, 7, 5, 4, 9),
				box(4, 0, 4, 12, 1, 12),
				box(10, 1, 7, 11, 4, 9),
				box(7, 1, 10, 9, 4, 11),
				box(7, 1, 5, 9, 4, 6),
				box(5, 1, 7, 6, 4, 9)));
		SHAPES.put(Direction.EAST, Shapes.or(
				center,
				box(7, 7, 1, 9, 9, 2),
				box(7, 7, 14, 9, 9, 15),
				box(7, 3, 13, 9, 9, 14),
				box(7, 3, 2, 9, 9, 3),
				box(7, 3, 11, 9, 4, 13),
				box(3, 3, 7, 5, 4, 9),
				box(2, 3, 7, 3, 11, 9),
				box(1, 9, 7, 2, 11, 9),
				box(7, 3, 3, 9, 4, 5),
				box(4, 0, 4, 12, 1, 12),
				box(7, 1, 10, 9, 4, 11),
				box(5, 1, 7, 6, 4, 9),
				box(10, 1, 7, 11, 4, 9),
				box(7, 1, 5, 9, 4, 6)));
		SHAPES.put(Direction.SOUTH, Shapes.or(
				center,
				box(14, 7, 7, 15, 9, 9),
				box(1, 7, 7, 2, 9, 9),
				box(2, 3, 7, 3, 9, 9),
				box(13, 3, 7, 14, 9, 9),
				box(3, 3, 7, 5, 4, 9),
				box(7, 3, 3, 9, 4, 5),
				box(7, 3, 2, 9, 11, 3),
				box(7, 9, 1, 9, 11, 2),
				box(11, 3, 7, 13, 4, 9),
				box(4, 0, 4, 12, 1, 12),
				box(5, 1, 7, 6, 4, 9),
				box(7, 1, 5, 9, 4, 6),
				box(7, 1, 10, 9, 4, 11),
				box(10, 1, 7, 11, 4, 9)));
		SHAPES.put(Direction.WEST, Shapes.or(
				center,
				box(7, 7, 14, 9, 9, 15),
				box(7, 7, 1, 9, 9, 2),
				box(7, 3, 2, 9, 9, 3),
				box(7, 3, 13, 9, 9, 14),
				box(7, 3, 3, 9, 4, 5),
				box(11, 3, 7, 13, 4, 9),
				box(13, 3, 7, 14, 11, 9),
				box(14, 9, 7, 15, 11, 9),
				box(7, 3, 11, 9, 4, 13),
				box(4, 0, 4, 12, 1, 12),
				box(7, 1, 5, 9, 4, 6),
				box(10, 1, 7, 11, 4, 9),
				box(5, 1, 7, 6, 4, 9),
				box(7, 1, 10, 9, 4, 11)));
	}

	public LightLoomBaseBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		return defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level,
	                                       @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return SHAPES.get(state.getValue(HORIZONTAL_FACING));
	}
}
