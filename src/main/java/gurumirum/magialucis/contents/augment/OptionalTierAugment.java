package gurumirum.magialucis.contents.augment;

import gurumirum.magialucis.api.augment.Augment;
import gurumirum.magialucis.api.augment.SimpleAugment;
import gurumirum.magialucis.contents.data.AugmentLogic;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class OptionalTierAugment extends SimpleAugment {
	private final Supplier<@Nullable Holder<Augment>> nextTierAugment;
	private final ResourceLocation tierlessTexture;
	private final ResourceLocation tieredTexture;

	public OptionalTierAugment(@NotNull Properties properties,
							   @NotNull Supplier<@Nullable Holder<Augment>> nextTierAugment,
	                           @NotNull ResourceLocation tierlessTexture,
	                           @NotNull ResourceLocation tieredTexture) {
		super(properties);
		this.nextTierAugment = nextTierAugment;
		this.tierlessTexture = tierlessTexture;
		this.tieredTexture = tieredTexture;
	}

	@Override
	public @NotNull ResourceLocation texture(@NotNull ItemStack stack) {
		Holder<Augment> nextTier = this.nextTierAugment.get();
		return nextTier != null && AugmentLogic.getSpec(stack).contains(nextTier) ?
				this.tieredTexture : this.tierlessTexture;
	}
}
