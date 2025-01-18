package gurumirum.magialucis.contents.block.lux.relay;

import gurumirum.magialucis.contents.ModDataComponents;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record RelayItemData(@NotNull ItemStack stack) {
	@Override
	public boolean equals(Object o) {
		return o instanceof RelayItemData(ItemStack s) && ItemStack.isSameItemSameComponents(s, this.stack);
	}

	@Override
	public int hashCode() {
		return ItemStack.hashItemAndComponents(this.stack);
	}

	public static @NotNull ItemStack getItem(DataComponentHolder holder) {
		RelayItemData relayItemData = holder.get(ModDataComponents.RELAY_ITEM);
		return relayItemData == null ? ItemStack.EMPTY : relayItemData.stack;
	}
}
