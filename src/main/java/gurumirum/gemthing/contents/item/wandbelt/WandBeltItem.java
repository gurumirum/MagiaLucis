package gurumirum.gemthing.contents.item.wandbelt;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.contents.ModItemTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WandBeltItem extends Item {
	public static final String CURIO_SLOT = GemthingMod.MODID + "_wand_belt";

	public WandBeltItem(Properties properties) {
		super(properties.stacksTo(1).fireResistant());
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player,
	                                                       @NotNull InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);

		if (!(stack.getCapability(Capabilities.ItemHandler.ITEM) instanceof IItemHandlerModifiable inv))
			return InteractionResultHolder.fail(stack);
		int selectedIndex = getSelectedIndex(stack);

		player.openMenu(new MenuProvider() {
			@Override
			public @NotNull Component getDisplayName() {
				return stack.getDisplayName();
			}
			@Override
			public @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
				return new WandBeltMenu(containerId, playerInventory, inv, selectedIndex);
			}
		});

		return InteractionResultHolder.success(stack);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
		if (contents == null || contents.getSlots() <= 0) return;

		// TODO localize
		tooltip.add(Component.literal(contents.getSlots() + " things?"));
	}

	@Override
	public boolean shouldCauseReequipAnimation(@NotNull ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged) {
		return slotChanged ? !oldStack.equals(newStack) : !newStack.is(oldStack.getItem());
	}

	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return !newStack.is(oldStack.getItem());
	}

	public static int getSelectedIndex(ItemStack stack) {
		Byte selectedIndex = stack.get(Contents.WAND_BELT_SELECTED_INDEX);
		return selectedIndex == null ? -1 : Byte.toUnsignedInt(selectedIndex);
	}

	public static void setSelectedIndex(ItemStack stack, int newIndex) {
		if (newIndex < 0) stack.remove(Contents.WAND_BELT_SELECTED_INDEX);
		else stack.set(Contents.WAND_BELT_SELECTED_INDEX, (byte)newIndex);
	}

	public static class ItemHandler extends ComponentItemHandler {
		public ItemHandler(MutableDataComponentHolder parent) {
			super(parent, DataComponents.CONTAINER, 18);
		}

		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			return stack.isEmpty() || stack.is(ModItemTags.WANDS);
		}
	}
}
