package gurumirum.magialucis.contents.block.lux.charger;

import gurumirum.magialucis.contents.ChargerTier;
import gurumirum.magialucis.contents.block.Ticker;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.ENABLED;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class RemoteChargerBlock extends Block implements EntityBlock {
	private static final EnumMap<Direction, VoxelShape> SHAPE = new EnumMap<>(Direction.class);
	private static final EnumMap<Direction, VoxelShape> LUX_NODE_SHAPE = new EnumMap<>(Direction.class);

	static {
		VoxelShape base = Shapes.or(
				box(4, 3, 4, 12, 13, 12),
				box(5, 2, 5, 11, 14, 11));

		SHAPE.put(Direction.DOWN, base);
		SHAPE.put(Direction.UP, Shapes.or(base,
				box(3, 0, 3, 13, 2, 13)));
		SHAPE.put(Direction.EAST, Shapes.or(base,
				box(0, 2, 5, 2, 14, 11),
				box(2, 6, 7, 4, 8, 9),
				box(2, 10, 7, 4, 12, 9)));
		SHAPE.put(Direction.WEST, Shapes.or(base,
				box(14, 2, 5, 16, 14, 11),
				box(12, 6, 7, 14, 8, 9),
				box(12, 10, 7, 14, 12, 9)));
		SHAPE.put(Direction.SOUTH, Shapes.or(base,
				box(5, 2, 0, 11, 14, 2),
				box(7, 6, 2, 9, 8, 4),
				box(7, 10, 2, 9, 12, 4)));
		SHAPE.put(Direction.NORTH, Shapes.or(base,
				box(5, 2, 14, 11, 14, 16),
				box(7, 6, 12, 9, 8, 14),
				box(7, 10, 12, 9, 12, 14)));

		base = box(4, 2, 4, 12, 14, 12);

		LUX_NODE_SHAPE.put(Direction.DOWN, base);
		LUX_NODE_SHAPE.put(Direction.UP, Shapes.or(base, box(3, 0, 3, 13, 2, 13)));
		LUX_NODE_SHAPE.put(Direction.EAST, box(0, 2, 4, 12, 14, 12));
		LUX_NODE_SHAPE.put(Direction.WEST, box(4, 2, 4, 16, 14, 12));
		LUX_NODE_SHAPE.put(Direction.SOUTH, box(4, 2, 0, 12, 14, 12));
		LUX_NODE_SHAPE.put(Direction.NORTH, box(4, 2, 4, 12, 14, 16));
	}

	private final ChargerTier chargerTier;

	public RemoteChargerBlock(Properties properties, ChargerTier chargerTier) {
		super(properties);
		this.chargerTier = chargerTier;
		registerDefaultState(defaultBlockState()
				.setValue(FACING, Direction.DOWN)
				.setValue(ENABLED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, ENABLED);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getClickedFace());
	}

	@Override
	public @Nullable RemoteChargerBlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new RemoteChargerBlockEntity(this.chargerTier, pos, state);
	}

	@Override
	protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
	                                       @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return SHAPE.get(state.getValue(FACING));
	}

	@Override
	protected @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level,
	                                             @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return (context instanceof LuxNetCollisionContext ? LUX_NODE_SHAPE : SHAPE).get(state.getValue(FACING));
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
	                                                                        @NotNull BlockEntityType<T> blockEntityType) {
		return Ticker.server(level);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		tooltip.add(Component.translatable("item.magialucis.tooltip.remote_charger"));
		LuxStatTooltip.formatStat(this.chargerTier.stat(), tooltip, LuxStatTooltip.Type.CONSUMER);
	}
}
