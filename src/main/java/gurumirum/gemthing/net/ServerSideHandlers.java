package gurumirum.gemthing.net;

import gurumirum.gemthing.capability.LinkSource;
import gurumirum.gemthing.capability.ModCapabilities;
import gurumirum.gemthing.contents.ModItems;
import gurumirum.gemthing.contents.item.wand.ConfigurationWandItem;
import gurumirum.gemthing.contents.item.wandbelt.WandBelt;
import gurumirum.gemthing.contents.item.wandbelt.WandBeltItem;
import gurumirum.gemthing.impl.InWorldBeamCraftingManager;
import gurumirum.gemthing.net.msgs.SetBeamCraftingInfoMsg;
import gurumirum.gemthing.net.msgs.SetLinkMsg;
import gurumirum.gemthing.net.msgs.SetWandBeltSelectedIndexMsg;
import gurumirum.gemthing.net.msgs.SwapWandBeltItemMsg;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerSideHandlers {
	public static void handleSetBeamCraftingInfo(SetBeamCraftingInfoMsg msg, IPayloadContext context) {
		InWorldBeamCraftingManager.setFocus(context.player(), msg.blockPos());
	}

	public static void handleSetWandBeltSelectedIndex(SetWandBeltSelectedIndexMsg msg, IPayloadContext context) {
		ItemStack stack = WandBelt.get(context.player());
		if (!stack.is(ModItems.WAND_BELT.asItem())) return;

		WandBeltItem.setSelectedIndex(stack, msg.selectedIndex());
	}

	public static void handleSwapWandBeltItem(SwapWandBeltItemMsg msg, IPayloadContext context) {
		if (msg.wandBeltIndex() < 0 || msg.wandBeltIndex() >= 18) return;

		Inventory inventory = context.player().getInventory();
		if (msg.playerInventoryIndex() < 0 || msg.playerInventoryIndex() >= inventory.getContainerSize()) return;

		ItemStack wandBelt = WandBelt.get(context.player());
		if (wandBelt.isEmpty()) return;
		if (!(wandBelt.getCapability(Capabilities.ItemHandler.ITEM) instanceof IItemHandlerModifiable itemHandler))
			return;

		ItemStack heldItem = inventory.getItem(msg.playerInventoryIndex());
		if (!itemHandler.isItemValid(msg.wandBeltIndex(), heldItem)) return;

		ItemStack itemToSwap = itemHandler.getStackInSlot(msg.wandBeltIndex());
		itemHandler.setStackInSlot(msg.wandBeltIndex(), heldItem);
		inventory.setItem(msg.playerInventoryIndex(), itemToSwap);
	}

	public static void handleSetLink(SetLinkMsg msg, IPayloadContext context) {
		Player player = context.player();
		BlockPos pos = msg.pos();

		if (!player.level().isLoaded(pos) ||
				Math.sqrt(msg.pos().distToCenterSqr(player.position())) >= ConfigurationWandItem.MAX_DISTANCE) return;

		LinkSource linkSource = player.level().getCapability(ModCapabilities.LINK_SOURCE, msg.pos());
		if (linkSource != null) {
			linkSource.setLink(msg.index(), msg.orientation());
		}
	}
}
