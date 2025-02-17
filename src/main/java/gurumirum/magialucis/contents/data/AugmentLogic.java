package gurumirum.magialucis.contents.data;

import gurumirum.magialucis.api.augment.Augment;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.ModDataMaps;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Objects;
import java.util.Set;

public final class AugmentLogic {
	private AugmentLogic() {}

	public static @NotNull ItemAugment getAugments(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.AUGMENTS, ItemAugment.empty());
	}

	public static @Nullable @Unmodifiable Set<Holder<Augment>> getSpecRaw(ItemStack stack) {
		return stack.getItemHolder().getData(ModDataMaps.AUGMENT_SPEC);
	}

	public static @NotNull @Unmodifiable Set<Holder<Augment>> getSpec(ItemStack stack) {
		return Objects.requireNonNullElse(getSpecRaw(stack), Set.of());
	}
}
