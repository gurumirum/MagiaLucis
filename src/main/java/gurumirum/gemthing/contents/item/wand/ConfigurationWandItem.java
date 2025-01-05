package gurumirum.gemthing.contents.item.wand;

import gurumirum.gemthing.capability.ModCapabilities;
import gurumirum.gemthing.contents.Contents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfigurationWandItem extends Item {
	public ConfigurationWandItem(Properties properties) {
		super(properties.stacksTo(1));
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag flags) {
		BlockPos linkSourcePos = stack.get(Contents.BLOCK_POS_DATA);
		if (linkSourcePos != null) tooltip.add(Component.literal("[" + linkSourcePos.toShortString() + "]")
				.withStyle(ChatFormatting.GOLD));
	}

	@Override
	public @NotNull InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		BlockPos pos = context.getClickedPos();
		BlockPos linkSourcePos = stack.get(Contents.BLOCK_POS_DATA.get());

		if (linkSourcePos == null) {
			var linkable = context.getLevel().getCapability(ModCapabilities.LINK_SOURCE, context.getClickedPos());
			if (linkable != null) {
				if (!context.getLevel().isClientSide) {
					if (context.isSecondaryUseActive()) {
						linkable.unlinkAll();
					} else {
						stack.set(Contents.BLOCK_POS_DATA.get(), pos);
					}
				}
				return InteractionResult.SUCCESS;
			} else {
				return InteractionResult.PASS;
			}
		}

		if (context.getLevel().isClientSide) return InteractionResult.SUCCESS;

		if (!context.getLevel().isLoaded(linkSourcePos)) {
			stack.remove(Contents.BLOCK_POS_DATA.get());
			return InteractionResult.SUCCESS;
		}

		var linkable = context.getLevel().getCapability(ModCapabilities.LINK_SOURCE, linkSourcePos);
		if (linkable != null) {
			if (linkSourcePos.equals(pos)) {
				linkable.unlink();
			} else {
				linkable.link(context.getClickLocation());
			}
			stack.remove(Contents.BLOCK_POS_DATA.get());
			return InteractionResult.SUCCESS;
		} else {
			stack.remove(Contents.BLOCK_POS_DATA.get());
			return InteractionResult.FAIL;
		}
	}
}
