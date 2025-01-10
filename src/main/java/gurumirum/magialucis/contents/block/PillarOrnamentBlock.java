package gurumirum.magialucis.contents.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class PillarOrnamentBlock extends Block {
	private final boolean top;

	public static PillarOrnamentBlock top(Properties properties) {
		return new PillarOrnamentBlock(properties, true);
	}

	public static PillarOrnamentBlock bottom(Properties properties) {
		return new PillarOrnamentBlock(properties, false);
	}

	public PillarOrnamentBlock(Properties properties, boolean top) {
		super(properties);
		this.top = top;
	}

	public boolean isTopOrnament() {
		return top;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		Direction clickedFace = context.getClickedFace();

		if (context.isSecondaryUseActive()) return defaultBlockState().setValue(FACING, clickedFace);

		BlockState s1 = context.getLevel().getBlockState(context.getClickedPos().relative(clickedFace));
		BlockState s2 = context.getLevel().getBlockState(context.getClickedPos().relative(clickedFace.getOpposite()));

		Direction facing;

		if (isConnectedPillarBlock(s1, clickedFace)) {
			facing = this.top ? clickedFace.getOpposite() : clickedFace;
		} else if (isConnectedPillarBlock(s2, clickedFace.getOpposite())) {
			facing = this.top ? clickedFace : clickedFace.getOpposite();
		} else {
			facing = clickedFace;
		}

		return defaultBlockState().setValue(FACING, facing);
	}

	private static boolean isConnectedPillarBlock(BlockState state, Direction direction) {
		if (state.getBlock() instanceof PillarOrnamentBlock pillarOrnamentBlock) {
			return state.getValue(FACING) == (pillarOrnamentBlock.isTopOrnament() ? direction : direction.getOpposite());
		} else {
			return state.getBlock() instanceof RotatedPillarBlock &&
					state.hasProperty(BlockStateProperties.AXIS) &&
					state.getValue(BlockStateProperties.AXIS) == direction.getAxis();
		}
	}
}
