package gurumirum.magialucis.contents.block.lux.splitter;

import gurumirum.magialucis.contents.block.GemContainerBlock;
import gurumirum.magialucis.contents.block.RelativeDirection;
import gurumirum.magialucis.impl.luxnet.LuxNetCollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class SplitterBlock extends GemContainerBlock implements SimpleWaterloggedBlock {
	private static final EnumMap<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);
	private static final VoxelShape SHAPE_LUX_NET_COLLISION = Shapes.block();

	static {
		SHAPES.put(Direction.DOWN, Shapes.or(
				box(0, 11, 11, 5, 12, 16),
				box(0, 11, 0, 5, 12, 5),
				box(0, 12, 0, 5, 16, 16),
				box(5, 12, 0, 11, 16, 5),
				box(5, 12, 11, 11, 16, 16),
				box(11, 12, 0, 16, 16, 16),
				box(11, 11, 11, 16, 12, 16),
				box(11, 11, 0, 16, 12, 5),
				box(0, 5, 12, 4, 11, 16),
				box(0, 5, 0, 4, 11, 4),
				box(12, 5, 0, 16, 11, 4),
				box(12, 5, 12, 16, 11, 16),
				box(0, 0, 11, 5, 5, 16),
				box(5, 0, 12, 11, 4, 16),
				box(5, 0, 0, 11, 4, 4),
				box(0, 0, 0, 5, 5, 5),
				box(0, 0, 5, 4, 4, 11),
				box(12, 0, 5, 16, 4, 11),
				box(11, 0, 11, 16, 5, 16),
				box(11, 0, 0, 16, 5, 5),
				box(1, 1, 1, 15, 15, 15)));
		SHAPES.put(Direction.UP, Shapes.or(
				box(0, 4, 0, 5, 5, 5),
				box(0, 4, 11, 5, 5, 16),
				box(0, 0, 0, 5, 4, 16),
				box(5, 0, 11, 11, 4, 16),
				box(5, 0, 0, 11, 4, 5),
				box(11, 0, 0, 16, 4, 16),
				box(11, 4, 0, 16, 5, 5),
				box(11, 4, 11, 16, 5, 16),
				box(0, 5, 0, 4, 11, 4),
				box(0, 5, 12, 4, 11, 16),
				box(12, 5, 12, 16, 11, 16),
				box(12, 5, 0, 16, 11, 4),
				box(0, 11, 0, 5, 16, 5),
				box(5, 12, 0, 11, 16, 4),
				box(5, 12, 12, 11, 16, 16),
				box(0, 11, 11, 5, 16, 16),
				box(0, 12, 5, 4, 16, 11),
				box(12, 12, 5, 16, 16, 11),
				box(11, 11, 0, 16, 16, 5),
				box(11, 11, 11, 16, 16, 16),
				box(1, 1, 1, 15, 15, 15)));
		SHAPES.put(Direction.NORTH, Shapes.or(
				box(11, 11, 11, 16, 16, 12),
				box(11, 0, 11, 16, 5, 12),
				box(11, 0, 12, 16, 16, 16),
				box(5, 0, 12, 11, 5, 16),
				box(5, 11, 12, 11, 16, 16),
				box(0, 0, 12, 5, 16, 16),
				box(0, 11, 11, 5, 16, 12),
				box(0, 0, 11, 5, 5, 12),
				box(12, 12, 5, 16, 16, 11),
				box(12, 0, 5, 16, 4, 11),
				box(0, 0, 5, 4, 4, 11),
				box(0, 12, 5, 4, 16, 11),
				box(11, 11, 0, 16, 16, 5),
				box(5, 12, 0, 11, 16, 4),
				box(5, 0, 0, 11, 4, 4),
				box(11, 0, 0, 16, 5, 5),
				box(12, 5, 0, 16, 11, 4),
				box(0, 5, 0, 4, 11, 4),
				box(0, 11, 0, 5, 16, 5),
				box(0, 0, 0, 5, 5, 5),
				box(1, 1, 1, 15, 15, 15)));
		SHAPES.put(Direction.SOUTH, Shapes.or(
				box(0, 11, 4, 5, 16, 5),
				box(0, 0, 4, 5, 5, 5),
				box(0, 0, 0, 5, 16, 4),
				box(5, 0, 0, 11, 5, 4),
				box(5, 11, 0, 11, 16, 4),
				box(11, 0, 0, 16, 16, 4),
				box(11, 11, 4, 16, 16, 5),
				box(11, 0, 4, 16, 5, 5),
				box(0, 12, 5, 4, 16, 11),
				box(0, 0, 5, 4, 4, 11),
				box(12, 0, 5, 16, 4, 11),
				box(12, 12, 5, 16, 16, 11),
				box(0, 11, 11, 5, 16, 16),
				box(5, 12, 12, 11, 16, 16),
				box(5, 0, 12, 11, 4, 16),
				box(0, 0, 11, 5, 5, 16),
				box(0, 5, 12, 4, 11, 16),
				box(12, 5, 12, 16, 11, 16),
				box(11, 11, 11, 16, 16, 16),
				box(11, 0, 11, 16, 5, 16),
				box(1, 1, 1, 15, 15, 15)));
		SHAPES.put(Direction.WEST, Shapes.or(
				box(11, 11, 0, 12, 16, 5),
				box(11, 0, 0, 12, 5, 5),
				box(12, 0, 0, 16, 16, 5),
				box(12, 0, 5, 16, 5, 11),
				box(12, 11, 5, 16, 16, 11),
				box(12, 0, 11, 16, 16, 16),
				box(11, 11, 11, 12, 16, 16),
				box(11, 0, 11, 12, 5, 16),
				box(5, 12, 0, 11, 16, 4),
				box(5, 0, 0, 11, 4, 4),
				box(5, 0, 12, 11, 4, 16),
				box(5, 12, 12, 11, 16, 16),
				box(0, 11, 0, 5, 16, 5),
				box(0, 12, 5, 4, 16, 11),
				box(0, 0, 5, 4, 4, 11),
				box(0, 0, 0, 5, 5, 5),
				box(0, 5, 0, 4, 11, 4),
				box(0, 5, 12, 4, 11, 16),
				box(0, 11, 11, 5, 16, 16),
				box(0, 0, 11, 5, 5, 16),
				box(1, 1, 1, 15, 15, 15)));
		SHAPES.put(Direction.EAST, Shapes.or(
				box(4, 11, 11, 5, 16, 16),
				box(4, 0, 11, 5, 5, 16),
				box(0, 0, 11, 4, 16, 16),
				box(0, 0, 5, 4, 5, 11),
				box(0, 11, 5, 4, 16, 11),
				box(0, 0, 0, 4, 16, 5),
				box(4, 11, 0, 5, 16, 5),
				box(4, 0, 0, 5, 5, 5),
				box(5, 12, 12, 11, 16, 16),
				box(5, 0, 12, 11, 4, 16),
				box(5, 0, 0, 11, 4, 4),
				box(5, 12, 0, 11, 16, 4),
				box(11, 11, 11, 16, 16, 16),
				box(12, 12, 5, 16, 16, 11),
				box(12, 0, 5, 16, 4, 11),
				box(11, 0, 11, 16, 5, 16),
				box(12, 5, 12, 16, 11, 16),
				box(12, 5, 0, 16, 11, 4),
				box(11, 11, 0, 16, 16, 5),
				box(11, 0, 0, 16, 5, 5),
				box(1, 1, 1, 15, 15, 15)));
	}

	public SplitterBlock(Properties properties) {
		super(properties);

		BlockState state = defaultBlockState()
				.setValue(WATERLOGGED, false)
				.setValue(FACING, Direction.UP);

		registerDefaultState(state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new SplitterBlockEntity(pos, state);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
		return defaultBlockState()
				.setValue(FACING, context.getNearestLookingDirection().getOpposite())
				.setValue(WATERLOGGED, fluidState.is(Fluids.WATER));
	}

	@Override
	protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
	                                       @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return SHAPES.get(state.getValue(FACING));
	}

	@Override
	protected @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level,
	                                             @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return context instanceof LuxNetCollisionContext ? SHAPE_LUX_NET_COLLISION : getShape(state, level, pos, context);
	}

	@Override
	protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level,
	                                                    @NotNull BlockPos pos, @NotNull Player player,
	                                                    @NotNull BlockHitResult hitResult) {
		InteractionResult result = super.useWithoutItem(state, level, pos, player, hitResult);
		if (result != InteractionResult.PASS) return result;
		if (player.isSecondaryUseActive()) return InteractionResult.PASS;

		Direction facing = state.getValue(FACING);
		Direction side = hitResult.getDirection();

		if (side == facing.getOpposite()) return InteractionResult.PASS;

		if (!level.isClientSide && level.getBlockEntity(pos) instanceof SplitterBlockEntity distributor) {
			RelativeDirection rel = RelativeDirection.getRelativeDirection(facing, side);
			byte prev = distributor.apertureLevel(rel);
			distributor.cycleApertureLevel(rel);

			level.playSound(null, pos, prev < distributor.apertureLevel(rel) ?
							SoundEvents.IRON_TRAPDOOR_OPEN : SoundEvents.IRON_TRAPDOOR_CLOSE,
					SoundSource.BLOCKS, .75f, level.getRandom().nextFloat() * 0.1f + 1.1f);
		}

		return InteractionResult.SUCCESS;
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
		tooltip.add(Component.translatable("block.magialucis.splitter.tooltip.0"));
		tooltip.add(Component.translatable("block.magialucis.splitter.tooltip.1"));
		super.addDescription(stack, context, tooltip, flag);
	}
}
