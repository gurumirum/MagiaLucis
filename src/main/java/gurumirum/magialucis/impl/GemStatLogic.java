package gurumirum.magialucis.impl;

import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.Contents;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class GemStatLogic {
	private GemStatLogic() {}

	public static @Nullable GemStat get(ItemStack stack) {
		return stack.getItemHolder().getData(Contents.GEM_STAT_DATA_MAP_TYPE);
	}

	public static @NotNull LuxStat getOrDefault(ItemStack stack) {
		return Objects.requireNonNullElse(get(stack), LuxStat.NULL);
	}
}
