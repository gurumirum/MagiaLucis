package gurumirum.gemthing.contents.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class AmberLightBlock extends Block {
	private static final VoxelShape SHAPE = box(5, 5, 5, 11, 11, 11);

	public AmberLightBlock(Properties properties) {
		super(properties);
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
	public void animateTick(@NotNull BlockState state, Level level, BlockPos pos, @NotNull RandomSource random) {
		double x = (double)pos.getX() + 0.5;
		double y = (double)pos.getY() + 0.5;
		double z = (double)pos.getZ() + 0.5;
		level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0);
	}
}
