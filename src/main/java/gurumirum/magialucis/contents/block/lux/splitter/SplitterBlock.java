package gurumirum.magialucis.contents.block.lux.splitter;

import gurumirum.magialucis.contents.block.GemContainerBlock;
import gurumirum.magialucis.contents.block.RelativeDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class SplitterBlock extends GemContainerBlock implements SimpleWaterloggedBlock {
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
}
