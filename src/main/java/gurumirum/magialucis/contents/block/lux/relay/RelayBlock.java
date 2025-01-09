package gurumirum.magialucis.contents.block.lux.relay;

import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.capability.ModCapabilities;
import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.impl.LuxStatTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
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

public class RelayBlock extends Block implements EntityBlock {
	private static final EnumMap<Direction, VoxelShape> SHAPE = new EnumMap<>(Direction.class);

	static {
		SHAPE.put(Direction.UP, box(2, 0, 2, 14, 12, 14));
		SHAPE.put(Direction.DOWN, box(2, 4, 2, 14, 16, 14));
		SHAPE.put(Direction.EAST, box(0, 2, 2, 12, 14, 14));
		SHAPE.put(Direction.WEST, box(4, 2, 2, 16, 14, 14));
		SHAPE.put(Direction.SOUTH, box(2, 2, 4, 14, 14, 16));
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
	protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state,
	                                                   @NotNull Level level, @NotNull BlockPos pos,
	                                                   @NotNull Player player, @NotNull InteractionHand hand,
	                                                   @NotNull BlockHitResult hitResult) {
		LuxStat gemStat = stack.getCapability(ModCapabilities.GEM_STAT);

		if (level.getBlockEntity(pos) instanceof RelayBlockEntity relay) {
			if (gemStat == null) {
				if (relay.stack().isEmpty()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
				if (!level.isClientSide) {
					ItemStack relayItem = relay.stack();
					Containers.dropItemStack(level, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, relayItem);
					relay.setStack(ItemStack.EMPTY);
				}
			} else {
				if (ItemStack.isSameItem(relay.stack(), stack)) {
					return ItemInteractionResult.CONSUME;
				}
				if (!level.isClientSide) {
					ItemStack split = stack.split(1);
					ItemStack relayItem = relay.stack();
					Containers.dropItemStack(level, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, relayItem);
					relay.setStack(split);
				}
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
				stack.set(Contents.RELAY_ITEM, new RelayItemData(relayItem.copy()));
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
				LuxStatTooltip.formatStat(gemStat, tooltip);
				LuxStatTooltip.skipAutoTooltipFor(stack);
			}
		}
	}
}
