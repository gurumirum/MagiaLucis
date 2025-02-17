package gurumirum.magialucis.contents;

import gurumirum.magialucis.api.augment.Augment;
import gurumirum.magialucis.api.augment.SimpleAugment;
import gurumirum.magialucis.api.augment.SimpleAugment.Properties;
import gurumirum.magialucis.client.Textures;
import gurumirum.magialucis.contents.augment.OptionalTierAugment;
import gurumirum.magialucis.contents.profile.AugmentProfile;
import gurumirum.magialucis.utils.AugmentProvider;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum Augments implements AugmentProvider {
	LUX_CAPACITY_1,
	LUX_CAPACITY_2,
	LUX_CAPACITY_3,

	SPEED_1(AugmentProfile.customAugment(Augments::speed)),
	SPEED_2,
	SPEED_3,

	CASTING_SPEED_1(AugmentProfile.customAugment(Augments::castingSpeed)),
	CASTING_SPEED_2,
	CASTING_SPEED_3,

	STORAGE_1(AugmentProfile.customAugment(Augments::storage)),
	STORAGE_2,
	STORAGE_3,

	CONFIGURATION_WAND_DEBUG_VIEW(textured(Textures.AUGMENT_DEBUG_VIEW)),
	AMBER_WAND_INVISIBLE_FLAME(textured(Textures.AUGMENT_CONCEAL)),

	ENDER_WAND_COLLECTOR(textured(Textures.AUGMENT_COLLECTOR)),
	;

	private static AugmentProfile<SimpleAugment> textured(ResourceLocation texture) {
		return AugmentProfile.augment(p -> p.texture(texture));
	}

	private static OptionalTierAugment speed(Properties p) {
		return new OptionalTierAugment(p, Augments.SPEED_2::augment, Textures.AUGMENT_SPEED, Textures.AUGMENT_SPEED_1);
	}

	private static OptionalTierAugment castingSpeed(Properties p) {
		return new OptionalTierAugment(p, Augments.CASTING_SPEED_2::augment, Textures.AUGMENT_CASTING_SPEED, Textures.AUGMENT_CASTING_SPEED_1);
	}

	private static OptionalTierAugment storage(Properties p) {
		return new OptionalTierAugment(p, Augments.STORAGE_2::augment, Textures.AUGMENT_STORAGE, Textures.AUGMENT_STORAGE_1);
	}

	private final DeferredHolder<Augment, SimpleAugment> holder;

	Augments() {
		this(AugmentProfile.augment());
	}

	Augments(@NotNull AugmentProfile<SimpleAugment> profile) {
		String id = name().toLowerCase(Locale.ROOT);
		this.holder = profile.create(id);
	}

	public @NotNull ResourceLocation id() {
		return this.holder.getId();
	}

	public @NotNull Augment instance() {
		return this.holder.get();
	}

	@Override
	public @NotNull Holder<Augment> augment() {
		return this.holder.getDelegate();
	}

	public static void init() {}
}
