package gurumirum.magialucis.contents.block.lux.lightloom;

import gurumirum.magialucis.api.luxnet.LuxNetCollisionContext;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.block.ModBlockStates;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.impl.LuxStatTooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class LightLoomBlock extends LightLoomBaseBlock implements EntityBlock {
	private static final VoxelShape SHAPE_LUX_NET_COLLISION = box(4, 0, 4, 12, 12, 12);

	private final LightLoomType type;

	public LightLoomBlock(Properties properties, LightLoomType type) {
		super(properties);
		this.type = type;
	}

	@Override
	public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		BlockState below = context.getLevel().getBlockState(context.getClickedPos().below());
		return below.is(ModBlocks.ARTISANRY_TABLE.block()) && !below.getValue(ModBlockStates.LEFT) ?
				defaultBlockState().setValue(HORIZONTAL_FACING,
						below.getValue(HORIZONTAL_FACING).getCounterClockWise()) :
				null;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new LightLoomBlockEntity(this.type, pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
			@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
		return Ticker.client(level);
	}

	@Override
	protected @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level,
	                                             @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return LuxNetCollisionContext.is(context) ? SHAPE_LUX_NET_COLLISION : getShape(state, level, pos, context);
	}

	@Override
	protected @NotNull BlockState updateShape(
			@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState,
			@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
		if (direction == Direction.DOWN) {
			if (neighborState.is(ModBlocks.ARTISANRY_TABLE.block()) && !neighborState.getValue(ModBlockStates.LEFT)) {
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
