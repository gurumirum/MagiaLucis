package gurumirum.magialucis.contents.block.lux.relay;

import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.capability.ModCapabilities;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.impl.LuxStatTooltip;
import gurumirum.magialucis.utils.ModUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class RelayBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {
	private static final EnumMap<Direction, VoxelShape> SHAPE = new EnumMap<>(Direction.class);

	static {
		SHAPE.put(Direction.UP, box(2, 0, 2, 14, 12, 14));
		SHAPE.put(Direction.DOWN, box(2, 4, 2, 14, 16, 14));
		SHAPE.put(Direction.EAST, box(0, 2, 2, 12, 14, 14));
		SHAPE.put(Direction.WEST, box(4, 2, 2, 16, 14, 14));
		SHAPE.put(Direction.SOUTH, box(2, 2, 0, 14, 14, 12));
		SHAPE.put(Direction.NORTH, box(2, 2, 4, 14, 14, 16));
	}

	public RelayBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
				.setValue(WATERLOGGED, false)
				.setValue(FACING, Direction.UP));
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new RelayBlockEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
	                                       @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return SHAPE.get(state.getValue(FACING));
	}

	@Override
	public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
		return defaultBlockState()
				.setValue(FACING, context.getClickedFace())
				.setValue(WATERLOGGED, fluidState.is(Fluids.WATER));
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
	protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level,
	                                                    @NotNull BlockPos pos, @NotNull Player player,
	                                                    @NotNull BlockHitResult hitResult) {
		if (hitResult.getDirection() == state.getValue(FACING).getOpposite())
			return InteractionResult.PASS;
		if (!player.isSecondaryUseActive())
			return InteractionResult.PASS;

		if (level.getBlockEntity(pos) instanceof RelayBlockEntity relay) {
			if (relay.stack().isEmpty()) return InteractionResult.PASS;
			if (!level.isClientSide) {
				ModUtils.giveOrDrop(player, level, relay.stack(), pos);
				relay.setStack(ItemStack.EMPTY);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	@Override
	protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state,
	                                                   @NotNull Level level, @NotNull BlockPos pos,
	                                                   @NotNull Player player, @NotNull InteractionHand hand,
	                                                   @NotNull BlockHitResult hitResult) {
		if (hitResult.getDirection() == state.getValue(FACING).getOpposite())
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		LuxStat gemStat = stack.getCapability(ModCapabilities.GEM_STAT);
		if (gemStat == null) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		if (level.getBlockEntity(pos) instanceof RelayBlockEntity relay) {
			if (ItemStack.isSameItem(relay.stack(), stack)) {
				return ItemInteractionResult.CONSUME;
			}
			if (!level.isClientSide) {
				ItemStack split = stack.split(1);
				ModUtils.giveOrDrop(player, level, relay.stack(), pos);
				relay.setStack(split);
			}
			return ItemInteractionResult.SUCCESS;
		}
		return ItemInteractionResult.FAIL;
	}

	@Override
	public @NotNull ItemStack getCloneItemStack(@NotNull BlockState state, @NotNull HitResult target,
	                                            @NotNull LevelReader level, @NotNull BlockPos pos,
	                                            @NotNull Player player) {
		ItemStack stack = new ItemStack(this);
		if (level.getBlockEntity(pos) instanceof RelayBlockEntity relay) {
			ItemStack relayItem = relay.stack();
			if (!relayItem.isEmpty()) {
				stack.set(ModDataComponents.RELAY_ITEM, new RelayItemData(relayItem.copy()));
			}
		}
		return stack;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		ItemStack s = RelayItemData.getItem(stack);
		if (!s.isEmpty()) {
			tooltip.add(s.getHoverName().copy().withStyle(ChatFormatting.GOLD));
			LuxStat gemStat = s.getCapability(ModCapabilities.GEM_STAT);
			if (gemStat != null) {
				LuxStatTooltip.formatStat(gemStat, tooltip, LuxStatTooltip.Type.GEM);
				LuxStatTooltip.skipAutoTooltipFor(stack);
			}
		}
	}
}
