package gurumirum.magialucis.contents.block;

import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.Wands;
import gurumirum.magialucis.impl.field.FieldManager;
import gurumirum.magialucis.impl.field.Fields;
import gurumirum.magialucis.impl.field.ServerFieldInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static gurumirum.magialucis.contents.block.ModBlockStates.LANTERN;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class AmberLightBlock extends Block implements SimpleWaterloggedBlock {
	private static final VoxelShape SHAPE = box(5, 5, 5, 11, 11, 11);

	public AmberLightBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
				.setValue(WATERLOGGED, false)
				.setValue(LANTERN, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED, LANTERN);
	}

	@Override
	protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
	                                       @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return !state.getValue(LANTERN) ||
				context.isHoldingItem(Wands.AMBER_TORCH.asItem()) ||
				context.isHoldingItem(ModBlocks.AMBER_LANTERN.asItem()) ? SHAPE : Shapes.empty();
	}

	@Override
	protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	protected void randomTick(@NotNull BlockState state, @NotNull ServerLevel level,
	                          @NotNull BlockPos pos, @NotNull RandomSource random) {
		FieldManager manager = FieldManager.get(level);
		ServerFieldInstance inst = manager.get(Fields.AMBER_LANTERN);
		if (inst == null || !inst.hasInfluence(pos)) {
			level.setBlock(pos, state.getValue(WATERLOGGED) ?
					Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 2);
		}
	}

	@Override
	protected boolean isRandomlyTicking(@NotNull BlockState state) {
		return state.getValue(LANTERN);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
		if (state.getValue(LANTERN)) {
			Minecraft mc = Minecraft.getInstance();
			if (mc.player == null) return;

			ItemStack heldItem = mc.player.getMainHandItem();
			if (!heldItem.is(Wands.AMBER_TORCH.asItem()) && !heldItem.is(ModBlocks.AMBER_LANTERN.asItem())) return;
		}

		double x = pos.getX() + 0.5;
		double y = pos.getY() + 0.5;
		double z = pos.getZ() + 0.5;

		if (state.getValue(WATERLOGGED)) {
			level.addParticle(ParticleTypes.BUBBLE, x, y, z, 0, 0.001, 0);
			level.addParticle(ParticleTypes.BUBBLE, x, y, z, 0, 0.002, 0);
		} else {
			level.addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
		}
	}

	@Override
	public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
		return defaultBlockState().setValue(WATERLOGGED, fluidState.is(Fluids.WATER));
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
}
