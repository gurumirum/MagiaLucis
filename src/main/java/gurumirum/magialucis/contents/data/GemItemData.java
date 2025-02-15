package gurumirum.magialucis.contents.data;

import gurumirum.magialucis.contents.ModDataComponents;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record GemItemData(@NotNull ItemStack stack) {
	@Override
	public boolean equals(Object o) {
		return o instanceof GemItemData(ItemStack s) && ItemStack.isSameItemSameComponents(s, this.stack);
	}

	@Override
	public int hashCode() {
		return ItemStack.hashItemAndComponents(this.stack);
	}

	public static @NotNull ItemStack getItem(DataComponentHolder holder) {
		GemItemData gemItemData = holder.get(ModDataComponents.GEM_ITEM);
		return gemItemData == null ? ItemStack.EMPTY : gemItemData.stack;
	}
}
