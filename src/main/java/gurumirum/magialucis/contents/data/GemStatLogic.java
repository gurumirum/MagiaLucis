package gurumirum.magialucis.contents.data;

import gurumirum.magialucis.api.capability.LuxStat;
import gurumirum.magialucis.contents.ModDataMaps;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class GemStatLogic {
	private GemStatLogic() {}

	public static @Nullable GemStat get(ItemStack stack) {
		return stack.getItemHolder().getData(ModDataMaps.GEM_STAT);
	}

	public static @NotNull LuxStat getOrDefault(ItemStack stack) {
		return Objects.requireNonNullElse(get(stack), LuxStat.NULL);
	}
}
