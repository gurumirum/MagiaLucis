package gurumirum.magialucis.contents.block.lux.lightloom;

import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.block.ModBlockStateProps;
import gurumirum.magialucis.impl.LuxStatTooltip;
import gurumirum.magialucis.impl.luxnet.LuxNetCollisionContext;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class LightLoomBlock extends Block implements EntityBlock {
	private static final EnumMap<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

	private static final VoxelShape SHAPE_LUX_NET_COLLISION = box(4, 0, 4, 12, 12, 12);

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

	private final LightLoomType type;

	public LightLoomBlock(Properties properties, LightLoomType type) {
		super(properties);
		this.type = type;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		BlockState below = context.getLevel().getBlockState(context.getClickedPos().below());
		return below.is(ModBlocks.ARTISANRY_TABLE.block()) && !below.getValue(ModBlockStateProps.LEFT) ?
				defaultBlockState().setValue(HORIZONTAL_FACING,
						below.getValue(HORIZONTAL_FACING).getCounterClockWise()) :
				null;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new LightLoomBlockEntity(this.type, pos, state);
	}

	@Override
	protected @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level,
	                                       @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return SHAPES.get(state.getValue(HORIZONTAL_FACING));
	}

	@Override
	protected @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level,
	                                             @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return context instanceof LuxNetCollisionContext ? SHAPE_LUX_NET_COLLISION : getShape(state, level, pos, context);
	}

	@Override
	protected @NotNull BlockState updateShape(
			@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState,
			@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
		if (direction == Direction.DOWN) {
			if (neighborState.is(ModBlocks.ARTISANRY_TABLE.block()) && !neighborState.getValue(ModBlockStateProps.LEFT)) {
				return state.setValue(HORIZONTAL_FACING, neighborState.getValue(HORIZONTAL_FACING).getCounterClockWise());
			} else {
				return Blocks.AIR.defaultBlockState();
			}
		}

		return state;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag tooltipFlag) {
		LuxStatTooltip.formatStat(this.type.luxStat(), tooltip, LuxStatTooltip.Type.CONSUMER);
	}
}
