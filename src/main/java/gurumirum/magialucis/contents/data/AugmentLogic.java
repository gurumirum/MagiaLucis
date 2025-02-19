package gurumirum.magialucis.contents.data;

import gurumirum.magialucis.api.augment.Augment;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.ModDataMaps;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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

	public static MutableComponent augmentName(String key) {
		return augmentName(Component.translatable(key));
	}

	public static MutableComponent augmentName(Component parameter) {
		return Component.translatable("magialucis.augment.tooltip.name", parameter.copy()
						.withStyle(ChatFormatting.WHITE))
				.withStyle(ChatFormatting.GRAY);
	}

	public static MutableComponent augmentDesc(String key) {
		return augmentDesc(Component.translatable(key));
	}

	public static MutableComponent augmentDesc(Component parameter) {
		return Component.translatable("magialucis.augment.tooltip.description", parameter)
				.withStyle(ChatFormatting.GRAY);
	}
}
