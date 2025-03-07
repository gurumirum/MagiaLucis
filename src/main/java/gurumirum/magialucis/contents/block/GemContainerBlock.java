package gurumirum.magialucis.contents.block;

import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.data.GemItemData;
import gurumirum.magialucis.contents.data.GemStat;
import gurumirum.magialucis.contents.data.GemStatLogic;
import gurumirum.magialucis.impl.LuxStatTooltip;
import gurumirum.magialucis.utils.ModUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class GemContainerBlock extends Block implements EntityBlock {
	public GemContainerBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level,
	                                                    @NotNull BlockPos pos, @NotNull Player player,
	                                                    @NotNull BlockHitResult hitResult) {
		if (!player.isSecondaryUseActive()) return InteractionResult.PASS;

		if (level.getBlockEntity(pos) instanceof GemContainer gemContainer) {
			if (gemContainer.stack().isEmpty()) return InteractionResult.PASS;
			if (!level.isClientSide) {
				ModUtils.giveOrDrop(player, level, gemContainer.stack(), pos);
				gemContainer.setStack(ItemStack.EMPTY);
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
		GemStat gemStat = GemStatLogic.get(stack);
		if (gemStat == null) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		if (level.getBlockEntity(pos) instanceof GemContainer gemContainer) {
			if (ItemStack.isSameItem(gemContainer.stack(), stack)) {
				return ItemInteractionResult.CONSUME;
			}
			if (!level.isClientSide) {
				ItemStack split = stack.split(1);
				ModUtils.giveOrDrop(player, level, gemContainer.stack(), pos);
				gemContainer.setStack(split);
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
		if (level.getBlockEntity(pos) instanceof GemContainer gemContainer) {
			ItemStack gemItem = gemContainer.stack();
			if (!gemItem.isEmpty()) {
				stack.set(ModDataComponents.GEM_ITEM, new GemItemData(gemItem.copy()));
			}
		}
		return stack;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		ItemStack s = GemItemData.getItem(stack);
		if (!s.isEmpty()) {
			tooltip.add(s.getHoverName().copy().withStyle(ChatFormatting.GOLD));
		}

		addDescription(stack, context, tooltip, flag);

		GemItemData gemItemData = stack.get(ModDataComponents.GEM_ITEM);
		if (gemItemData != null) {
			GemStat gemStat = GemStatLogic.get(gemItemData.stack());
			if (gemStat != null) {
				LuxStatTooltip.formatStat(gemStat, tooltip, LuxStatTooltip.Type.GEM);
				LuxStatTooltip.skipAutoTooltipFor(stack);
			}
		}
	}

	protected void addDescription(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
	                              @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		tooltip.add(Component.translatable("block.magialucis.tooltip.gem_container.0"));
		tooltip.add(Component.translatable("block.magialucis.tooltip.gem_container.1"));
		tooltip.add(Component.translatable("block.magialucis.tooltip.gem_container.2"));
	}

	public interface GemContainer {
		@NotNull ItemStack stack();
		void setStack(@NotNull ItemStack stack);
	}
}
