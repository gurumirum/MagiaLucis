package gurumirum.magialucis.contents.block.lux.lightbasin;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.block.ModBlockStates;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.impl.LuxStatTooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LightBasinBlock extends Block implements EntityBlock {
	public static final LuxStat STAT = GemStats.BRIGHTSTONE;

	private static final VoxelShape SHAPE = box(0, 0, 0, 16, 12, 16);

	public LightBasinBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
				.setValue(ModBlockStates.WORKING, false));
	}

	@Override protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
		builder.add(ModBlockStates.WORKING);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new LightBasinBlockEntity(pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level,
	                                                                        @NotNull BlockState state,
	                                                                        @NotNull BlockEntityType<T> blockEntityType) {
		return Ticker.both(level);
	}

	@Override
	protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
	                                       @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level,
	                                                    @NotNull BlockPos pos, @NotNull Player player,
	                                                    @NotNull BlockHitResult hitResult) {
		if (level.getBlockEntity(pos) instanceof LightBasinBlockEntity lightBasin) {
			if (player.isSecondaryUseActive()) {
				lightBasin.dropAllContents(player);
				return InteractionResult.SUCCESS;
			} else {
				return lightBasin.dropLastContent(player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
			}
		}
		return InteractionResult.FAIL;
	}

	@Override
	protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state,
	                                                   @NotNull Level level, @NotNull BlockPos pos,
	                                                   @NotNull Player player, @NotNull InteractionHand hand,
	                                                   @NotNull BlockHitResult hitResult) {
		if (hand == InteractionHand.OFF_HAND || player.isSecondaryUseActive() || stack.isEmpty()) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}

		if (level.getBlockEntity(pos) instanceof LightBasinBlockEntity lightBasin) {
			IItemHandlerModifiable inventory = lightBasin.inventory();
			for (int i = 0; i < inventory.getSlots(); i++) {
				ItemStack remaining = inventory.insertItem(i, stack, level.isClientSide);
				if (remaining != stack) {
					if (!level.isClientSide) player.setItemInHand(hand, remaining);
					return ItemInteractionResult.SUCCESS;
				}
			}
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	protected void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
	                        @NotNull BlockState newState, boolean movedByPiston) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof LightBasinBlockEntity lightBasin) {
				lightBasin.dropAllContents(null);
			}
		}

		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag tooltipFlag) {
		tooltip.add(Component.translatable("block.magialucis.light_basin.tooltip.0"));

		LuxStatTooltip.formatStat(STAT, tooltip, LuxStatTooltip.Type.CONSUMER);
	}
}
