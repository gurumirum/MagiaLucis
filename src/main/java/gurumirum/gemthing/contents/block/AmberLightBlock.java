package gurumirum.gemthing.contents.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AmberLightBlock extends Block implements SimpleWaterloggedBlock {
	private static final VoxelShape SHAPE = box(5, 5, 5, 11, 11, 11);

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public AmberLightBlock(Properties properties) {
		super(properties);
		registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
	                                       @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public void animateTick(@NotNull BlockState state, @NotNull Level level, BlockPos pos, @NotNull RandomSource random) {
		double x = (double)pos.getX() + 0.5;
		double y = (double)pos.getY() + 0.5;
		double z = (double)pos.getZ() + 0.5;
		if(state.getValue(WATERLOGGED)) {
			level.addParticle(ParticleTypes.BUBBLE, x, y, z, 0.0D, 0.001D, 0.0D);
			level.addParticle(ParticleTypes.BUBBLE, x, y, z, 0.0D, 0.002D, 0.0D);
		}
		else {
			level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0);
			level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0001, 0.0D);
			level.addParticle(ParticleTypes.SMOKE, x, y + 0.01, z, 0.0D, 0.001, 0.0D);
		}
	}

	@Override
	public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return Objects.requireNonNull(super.getStateForPlacement(context)).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}

	@Override
	protected @NotNull FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected @NotNull BlockState updateShape(BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}
}
