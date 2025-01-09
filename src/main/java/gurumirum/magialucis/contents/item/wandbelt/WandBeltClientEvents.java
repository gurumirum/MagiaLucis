package gurumirum.magialucis.contents.item.wandbelt;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.client.ModKeyMappings;
import gurumirum.magialucis.contents.ModItemTags;
import gurumirum.magialucis.net.msgs.SetWandBeltSelectedIndexMsg;
import gurumirum.magialucis.net.msgs.SwapWandBeltItemMsg;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = MagiaLucisMod.MODID, value = Dist.CLIENT)
public final class WandBeltClientEvents {
	private WandBeltClientEvents() {}

	@SubscribeEvent
	public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.player.isDeadOrDying() || mc.screen != null) return;

		ItemStack wandBeltStack = WandBelt.get(mc.player);
		if (wandBeltStack.isEmpty()) return;

		boolean keyDown = ModKeyMappings.CHANGE_WAND.isDown();
		if (!keyDown) return;

		ItemContainerContents container = wandBeltStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
		int selectedIndex = WandBeltItem.getSelectedIndex(wandBeltStack);
		boolean oneRow = computeOneRow(container, selectedIndex);

		if (selectedIndex == -1) selectedIndex = 0;
		else {
			int scrollDelta = (int)(event.getScrollDeltaY() != 0 ? -event.getScrollDeltaY() : event.getScrollDeltaX());
			selectedIndex = Math.floorMod((selectedIndex + scrollDelta), (oneRow ? 9 : 18));
		}

		WandBeltItem.setSelectedIndex(wandBeltStack, selectedIndex);
		PacketDistributor.sendToServer(new SetWandBeltSelectedIndexMsg(selectedIndex));
		event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onRClick(InputEvent.InteractionKeyMappingTriggered event) {
		if (!event.isUseItem()) return;

		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.player.isDeadOrDying() || mc.screen != null) return;

		ItemStack wandBeltStack = WandBelt.get(mc.player);
		if (wandBeltStack.isEmpty()) return;

		boolean keyDown = ModKeyMappings.CHANGE_WAND.isDown();
		if (!keyDown) return;

		int selectedIndex = WandBeltItem.getSelectedIndex(wandBeltStack);
		if (selectedIndex < 0 || selectedIndex >= 18) return;

		ItemStack heldItem = mc.player.getMainHandItem();
		if (!heldItem.isEmpty() && !heldItem.is(ModItemTags.WANDS)) return;

		PacketDistributor.sendToServer(new SwapWandBeltItemMsg(selectedIndex, mc.player.getInventory().selected));
		event.setCanceled(true);
	}

	public static boolean computeOneRow(ItemContainerContents container, int selectedIndex) {
		// if the selected index is on 2nd row, there should be two rows
		if (selectedIndex >= 9) return false;

		// if any item is in 2nd row then there should be two rows
		for (int i = 9, slots = Math.min(18, container.getSlots()); i < slots; i++) {
			if (!container.getStackInSlot(i).isEmpty()) return false;
		}

		// if all upper row is full then there should be two rows
		if (container.getSlots() < 9) return true;
		for (int i = 0; i < 9; i++) {
			if (container.getStackInSlot(i).isEmpty()) return true;
		}

		return false;
	}
}
