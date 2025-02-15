package gurumirum.magialucis.contents.data;

import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.ModDataMaps;
import gurumirum.magialucis.utils.AugmentProvider;
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

	public static boolean canApply(ItemStack stack, AugmentProvider augment) {
		return canApply(stack, augment.augment());
	}

	public static boolean canApply(ItemStack stack, Holder<Augment> augment) {
		Set<Holder<Augment>> spec = getSpec(stack);
		if (!spec.contains(augment)) return false;

		ItemAugment itemAugment = getAugments(stack);
		if (itemAugment.has(augment)) return false;

		Augment a = augment.value();
		for (Holder<Augment> h : a.precursor()) {
			if (!itemAugment.has(h)) return false;
		}
		for (Holder<Augment> h : itemAugment.set()) {
			if (a.incompatible().contains(h)) return false;
		}

		return true;
	}
}
