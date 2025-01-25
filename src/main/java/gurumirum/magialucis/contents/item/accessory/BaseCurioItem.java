package gurumirum.magialucis.contents.item.accessory;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.List;

public class BaseCurioItem extends Item implements ICurioItem {
	public BaseCurioItem(Properties properties) {
		super(properties.stacksTo(1));
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player,
	                                                       @NotNull InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		if (!level.isClientSide) {
			if (tryEquipCurio(player, stack)) {
				player.setItemInHand(usedHand, stack = ItemStack.EMPTY);
			}
		}
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
		return List.of();
	}
	public static boolean tryEquipCurio(@NotNull Player player, @NotNull ItemStack stack) {
		ICuriosItemHandler curiosItemHandler = CuriosApi.getCuriosInventory(player).orElse(null);
		if (curiosItemHandler == null) return false;

		var slots = CuriosApi.getItemStackSlots(stack, player);

		// try putting it on empty slot
		for (var e : slots.entrySet()) {
			ICurioStacksHandler curioStacksHandler = curiosItemHandler.getCurios().get(e.getKey());
			if (curioStacksHandler == null) continue;

			IDynamicStackHandler stacks = curioStacksHandler.getStacks();

			for (int i = 0; i < stacks.getSlots(); i++) {
				if (stacks.insertItem(i, stack, false).isEmpty()) return true;
			}
		}

		for (var e : slots.entrySet()) {
			ICurioStacksHandler curioStacksHandler = curiosItemHandler.getCurios().get(e.getKey());
			if (curioStacksHandler == null) continue;

			IDynamicStackHandler stacks = curioStacksHandler.getStacks();

			for (int i = 0; i < stacks.getSlots(); i++) {
				if (!stacks.isItemValid(i, stack)) continue;

				ItemStack orig = stacks.extractItem(i, Integer.MAX_VALUE, false);
				if (stacks.insertItem(i, stack, false).isEmpty()) {
					giveOrDrop(player, orig);
					return true;
				} else if (!stacks.insertItem(i, orig, false).isEmpty()) {
					giveOrDrop(player, orig);
				}
			}
		}

		return false;
	}

	private static void giveOrDrop(@NotNull Player player, @NotNull ItemStack stack) {
		if (!player.addItem(stack)) {
			ItemEntity itemEntity = player.drop(stack, false);
			if (itemEntity != null) {
				itemEntity.setNoPickUpDelay();
				itemEntity.setTarget(player.getUUID());
			}
		}
	}
}
