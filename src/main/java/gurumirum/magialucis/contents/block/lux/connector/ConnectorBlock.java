package gurumirum.magialucis.contents.block.lux.connector;

import gurumirum.magialucis.contents.block.GemContainerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
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
import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class ConnectorBlock extends GemContainerBlock implements SimpleWaterloggedBlock {
	private static final EnumMap<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

	static {
		SHAPES.put(Direction.DOWN, Shapes.or(
				box(0, 0, 0, 16, 6, 16),
				box(10, 6, 0, 16, 10, 6),
				box(0, 6, 0, 6, 10, 6),
				box(0, 6, 10, 6, 10, 16),
				box(10, 6, 10, 16, 10, 16),
				box(2, 6, 2, 14, 14, 14)));
		SHAPES.put(Direction.UP, Shapes.or(
				box(0, 10, 0, 16, 16, 16),
				box(10, 6, 10, 16, 10, 16),
				box(0, 6, 10, 6, 10, 16),
				box(0, 6, 0, 6, 10, 6),
				box(10, 6, 0, 16, 10, 6),
				box(2, 2, 2, 14, 10, 14)));
		SHAPES.put(Direction.NORTH, Shapes.or(
				box(0, 0, 0, 16, 16, 6),
				box(0, 0, 6, 6, 6, 10),
				box(10, 0, 6, 16, 6, 10),
				box(10, 10, 6, 16, 16, 10),
				box(0, 10, 6, 6, 16, 10),
				box(2, 2, 6, 14, 14, 14)));
		SHAPES.put(Direction.SOUTH, Shapes.or(
				box(0, 0, 10, 16, 16, 16),
				box(10, 0, 6, 16, 6, 10),
				box(0, 0, 6, 6, 6, 10),
				box(0, 10, 6, 6, 16, 10),
				box(10, 10, 6, 16, 16, 10),
				box(2, 2, 2, 14, 14, 10)));
		SHAPES.put(Direction.WEST, Shapes.or(
				box(0, 0, 0, 6, 16, 16),
				box(6, 0, 10, 10, 6, 16),
				box(6, 0, 0, 10, 6, 6),
				box(6, 10, 0, 10, 16, 6),
				box(6, 10, 10, 10, 16, 16),
				box(6, 2, 2, 14, 14, 14)));
		SHAPES.put(Direction.EAST, Shapes.or(
				box(10, 0, 0, 16, 16, 16),
				box(6, 0, 0, 10, 6, 6),
				box(6, 0, 10, 10, 6, 16),
				box(6, 10, 10, 10, 16, 16),
				box(6, 10, 0, 10, 16, 6),
				box(2, 2, 2, 10, 14, 14)));
	}

	public ConnectorBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
				.setValue(WATERLOGGED, false)
				.setValue(FACING, Direction.DOWN));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new ConnectorBlockEntity(pos, state);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
		return defaultBlockState()
				.setValue(FACING, context.getClickedFace().getOpposite())
				.setValue(WATERLOGGED, fluidState.is(Fluids.WATER));
	}

	@Override
	protected @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level,
	                                       @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return SHAPES.get(state.getValue(FACING));
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
		return state;
	}

	@Override
	protected void addDescription(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
	                              @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		tooltip.add(Component.translatable("block.magialucis.tooltip.direct_lux_transmitter"));
		tooltip.add(Component.translatable("block.magialucis.connector.tooltip.0"));
		super.addDescription(stack, context, tooltip, flag);
	}
}
