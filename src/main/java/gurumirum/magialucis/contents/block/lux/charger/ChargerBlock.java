package gurumirum.magialucis.contents.block.lux.charger;

import gurumirum.magialucis.contents.ChargerTier;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.impl.LuxStatTooltip;
import gurumirum.magialucis.impl.luxnet.LuxNetCollisionContext;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChargerBlock extends Block implements EntityBlock {
	private static final VoxelShape SHAPE = box(2, 0, 2, 14, 4, 14);
	private static final VoxelShape SHAPE_LUX_NET_COLLISION = box(2, 0, 2, 14, 14, 14);

	private final ChargerTier chargerTier;

	public ChargerBlock(Properties properties, ChargerTier chargerTier) {
		super(properties);
		this.chargerTier = chargerTier;
	}

	@Override
	protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level,
	                                                    @NotNull BlockPos pos, @NotNull Player player,
	                                                    @NotNull BlockHitResult hitResult) {
		if (level.getBlockEntity(pos) instanceof ChargerBlockEntity charger) {
			return charger.dropItem() ? InteractionResult.SUCCESS : InteractionResult.PASS;
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

		if (level.getBlockEntity(pos) instanceof ChargerBlockEntity charger) {
			IItemHandlerModifiable inventory = charger.inventory();
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
			if (level.getBlockEntity(pos) instanceof ChargerBlockEntity charger) {
				charger.dropItem();
			}
		}

		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Override
	public @Nullable ChargerBlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new ChargerBlockEntity(this.chargerTier, pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
	                                                                        @NotNull BlockEntityType<T> blockEntityType) {
		return Ticker.server(level);
	}

	@Override
	protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
	                                       @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level,
	                                             @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return context instanceof LuxNetCollisionContext ? SHAPE_LUX_NET_COLLISION : SHAPE;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		tooltip.add(Component.translatable("item.magialucis.tooltip.charger"));
		LuxStatTooltip.formatStat(this.chargerTier.stat(), tooltip, LuxStatTooltip.Type.CONTAINER);
	}
}
