package gurumirum.magialucis.contents.block.artisanrytable;

import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.contents.block.lux.lightloom.LightLoomBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

import static gurumirum.magialucis.contents.block.ModBlockStateProps.LEFT;
import static gurumirum.magialucis.contents.block.ModBlockStateProps.LIGHTLOOM;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class ArtisanryTableBlock extends Block implements EntityBlock {
	private static final Map<Direction, VoxelShape> SHAPES_LEFT = new EnumMap<>(Direction.class);
	private static final Map<Direction, VoxelShape> SHAPES_RIGHT = new EnumMap<>(Direction.class);

	static {
		SHAPES_LEFT.put(Direction.NORTH,
				Shapes.or(box(4, 13, 2, 16, 16, 16),
						box(4, 11, 8, 16, 13, 16),
						box(4, 8, 0, 16, 11, 12),
						box(0, 0, 0, 4, 8, 4),
						box(0, 0, 12, 4, 8, 16),
						box(0, 8, 0, 4, 16, 12),
						box(0, 8, 12, 4, 16, 16)));
		SHAPES_LEFT.put(Direction.EAST,
				Shapes.or(box(0, 13, 4, 14, 16, 16),
						box(0, 11, 4, 8, 13, 16),
						box(4, 8, 4, 16, 11, 16),
						box(12, 0, 0, 16, 8, 4),
						box(0, 0, 0, 4, 8, 4),
						box(4, 8, 0, 16, 16, 4),
						box(0, 8, 0, 4, 16, 4)));
		SHAPES_LEFT.put(Direction.SOUTH,
				Shapes.or(box(0, 13, 0, 12, 16, 14),
						box(0, 11, 0, 12, 13, 8),
						box(0, 8, 4, 12, 11, 16),
						box(12, 0, 12, 16, 8, 16),
						box(12, 0, 0, 16, 8, 4),
						box(12, 8, 4, 16, 16, 16),
						box(12, 8, 0, 16, 16, 4)));
		SHAPES_LEFT.put(Direction.WEST,
				Shapes.or(box(2, 13, 0, 16, 16, 12),
						box(8, 11, 0, 16, 13, 12),
						box(0, 8, 0, 12, 11, 12),
						box(0, 0, 12, 4, 8, 16),
						box(12, 0, 12, 16, 8, 16),
						box(0, 8, 12, 12, 16, 16),
						box(12, 8, 12, 16, 16, 16)));

		SHAPES_RIGHT.put(Direction.NORTH,
				Shapes.or(box(0, 8, 0, 16, 16, 16),
						box(12, 0, 12, 16, 8, 16),
						box(12, 0, 0, 16, 8, 4)));
		SHAPES_RIGHT.put(Direction.EAST,
				Shapes.or(box(0, 8, 0, 16, 16, 16),
						box(0, 0, 12, 4, 8, 16),
						box(12, 0, 12, 16, 8, 16)));
		SHAPES_RIGHT.put(Direction.SOUTH,
				Shapes.or(box(0, 8, 0, 16, 16, 16),
						box(0, 0, 0, 4, 8, 4),
						box(0, 0, 12, 4, 8, 16)));
		SHAPES_RIGHT.put(Direction.WEST,
				Shapes.or(box(0, 8, 0, 16, 16, 16),
						box(12, 0, 0, 16, 8, 4),
						box(0, 0, 0, 4, 8, 4)));
	}

	public ArtisanryTableBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
				.setValue(LIGHTLOOM, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING, LEFT, LIGHTLOOM);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return state.getValue(LEFT) ? new ArtisanryTableBlockEntity(pos, state) : null;
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
			@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
		return Ticker.server(level);
	}

	@Override
	protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
	                                       @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return (state.getValue(LEFT) ? SHAPES_LEFT : SHAPES_RIGHT).get(state.getValue(HORIZONTAL_FACING));
	}

	@Override
	public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		Direction direction = context.getHorizontalDirection().getOpposite();

		double v = direction.getAxis() == Direction.Axis.X ?
				1 - (context.getClickLocation().z - context.getClickedPos().getZ()) :
				context.getClickLocation().x - context.getClickedPos().getX();
		boolean left = (v <= 0.5) == (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE);

		if (place(context, left)) {
			return defaultBlockState()
					.setValue(HORIZONTAL_FACING, direction)
					.setValue(LEFT, left);
		} else if (place(context, !left)) {
			return defaultBlockState()
					.setValue(HORIZONTAL_FACING, direction)
					.setValue(LEFT, !left);
		} else return null;
	}

	private boolean place(BlockPlaceContext context, boolean left) {
		Level level = context.getLevel();
		Direction direction = context.getHorizontalDirection().getOpposite();
		WorldBorder worldBorder = level.getWorldBorder();

		BlockPos leftBlockPos, rightBlockPos;

		if (left) {
			leftBlockPos = context.getClickedPos();
			rightBlockPos = leftBlockPos.relative(direction.getClockWise());
		} else {
			rightBlockPos = context.getClickedPos();
			leftBlockPos = rightBlockPos.relative(direction.getCounterClockWise());
		}

		return worldBorder.isWithinBounds(leftBlockPos) &&
				worldBorder.isWithinBounds(rightBlockPos) &&
				level.getBlockState(leftBlockPos).canBeReplaced(context) &&
				level.getBlockState(rightBlockPos).canBeReplaced(context);
	}

	@Override
	public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
	                        @Nullable LivingEntity placer, @NotNull ItemStack stack) {
		if (!level.isClientSide) {
			Direction facing = state.getValue(HORIZONTAL_FACING);
			boolean left = state.getValue(LEFT);
			Direction otherBlockDirection = left ? facing.getClockWise() : facing.getCounterClockWise();

			level.setBlock(pos.relative(otherBlockDirection), state.setValue(LEFT, !left), 3);
		}
	}

	@Override
	protected @NotNull BlockState updateShape(
			@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState,
			@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
		Direction facing = state.getValue(HORIZONTAL_FACING);
		boolean left = state.getValue(LEFT);
		Direction otherBlockDirection = left ? facing.getClockWise() : facing.getCounterClockWise();

		if (otherBlockDirection == direction) {
			return neighborState.is(this) &&
					neighborState.getValue(HORIZONTAL_FACING) == facing &&
					neighborState.getValue(LEFT) != left ?
					(left ? state.setValue(LIGHTLOOM, neighborState.getValue(LIGHTLOOM)) : state) :
					Blocks.AIR.defaultBlockState();
		}

		if (!left && direction == Direction.UP) {
			return state.setValue(LIGHTLOOM, neighborState.getBlock() instanceof LightLoomBlock &&
					neighborState.getValue(HORIZONTAL_FACING) == facing.getCounterClockWise());
		}

		return state;
	}

	@Override
	protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level,
	                                                    @NotNull BlockPos pos, @NotNull Player player,
	                                                    @NotNull BlockHitResult hitResult) {
		ItemStack stack = player.getMainHandItem();
		if (stack.getItem() instanceof BlockItem blockItem &&
				blockItem.getBlock() instanceof LightLoomBlock) {
			return InteractionResult.PASS;
		}

		if (!level.isClientSide) {
			BlockPos leftPos = state.getValue(LEFT) ? pos :
					pos.relative(state.getValue(HORIZONTAL_FACING).getCounterClockWise());

			if (level.getBlockEntity(leftPos) instanceof ArtisanryTableBlockEntity artisanryTable) {
				player.openMenu(artisanryTable);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
	                        @NotNull BlockState newState, boolean movedByPiston) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof ArtisanryTableBlockEntity artisanryTable) {
				IItemHandlerModifiable inv = artisanryTable.inventory();
				for (int i = 0; i < inv.getSlots(); i++) {
					ItemStack stack = inv.getStackInSlot(i);
					Containers.dropItemStack(level, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, stack);
				}
			}
		}

		super.onRemove(state, level, pos, newState, movedByPiston);
	}
}
