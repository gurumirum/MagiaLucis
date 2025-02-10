package gurumirum.magialucis.contents.block.lux.relay;

import gurumirum.magialucis.contents.block.GemContainerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class RelayBlock extends GemContainerBlock implements SimpleWaterloggedBlock {
	private static final EnumMap<Direction, VoxelShape> SHAPE = new EnumMap<>(Direction.class);

	static {
		SHAPE.put(Direction.UP, box(2, 0, 2, 14, 12, 14));
		SHAPE.put(Direction.DOWN, box(2, 4, 2, 14, 16, 14));
		SHAPE.put(Direction.EAST, box(0, 2, 2, 12, 14, 14));
		SHAPE.put(Direction.WEST, box(4, 2, 2, 16, 14, 14));
		SHAPE.put(Direction.SOUTH, box(2, 2, 0, 14, 14, 12));
		SHAPE.put(Direction.NORTH, box(2, 2, 4, 14, 14, 16));
	}

	public RelayBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
				.setValue(WATERLOGGED, false)
				.setValue(FACING, Direction.UP));
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new RelayBlockEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
	                                       @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return SHAPE.get(state.getValue(FACING));
	}

	@Override
	public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
		return defaultBlockState()
				.setValue(FACING, context.getClickedFace())
				.setValue(WATERLOGGED, fluidState.is(Fluids.WATER));
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
	protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level,
	                                                    @NotNull BlockPos pos, @NotNull Player player,
	                                                    @NotNull BlockHitResult hitResult) {
		if (hitResult.getDirection() == state.getValue(FACING).getOpposite())
			return InteractionResult.PASS;

		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Override
	protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state,
	                                                   @NotNull Level level, @NotNull BlockPos pos,
	                                                   @NotNull Player player, @NotNull InteractionHand hand,
	                                                   @NotNull BlockHitResult hitResult) {
		if (hitResult.getDirection() == state.getValue(FACING).getOpposite())
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected void addDescription(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
	                              @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		tooltip.add(Component.translatable("block.magialucis.relay.tooltip.0"));
		tooltip.add(Component.translatable("item.magialucis.tooltip.link_source"));
		tooltip.add(Component.translatable("block.magialucis.relay.tooltip.1"));
		tooltip.add(Component.translatable("block.magialucis.relay.tooltip.2"));
		tooltip.add(Component.translatable("block.magialucis.relay.tooltip.3"));
	}
}
