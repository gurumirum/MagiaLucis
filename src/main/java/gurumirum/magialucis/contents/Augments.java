package gurumirum.magialucis.contents;

import gurumirum.magialucis.api.augment.Augment;
import gurumirum.magialucis.api.augment.SimpleAugment;
import gurumirum.magialucis.client.Textures;
import gurumirum.magialucis.contents.augment.TieredAugment;
import gurumirum.magialucis.contents.augment.TieredAugmentType;
import gurumirum.magialucis.contents.augment.TieredAugmentTypes;
import gurumirum.magialucis.contents.profile.AugmentProfile;
import gurumirum.magialucis.utils.AugmentProvider;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static gurumirum.magialucis.contents.augment.TieredAugmentTypes.*;

public enum Augments implements AugmentProvider {
	OVERCHARGE_1(tiered(OVERCHARGE, 0)),
	OVERCHARGE_2(tiered(OVERCHARGE, 1)),
	OVERCHARGE_3(tiered(OVERCHARGE, 2)),

	ACCELERATION_1(tiered(ACCELERATION, 0)),
	ACCELERATION_2(tiered(ACCELERATION, 1)),
	ACCELERATION_3(tiered(ACCELERATION, 2)),

	QUICK_CAST_1(tiered(QUICK_CAST, 0)),
	QUICK_CAST_2(tiered(QUICK_CAST, 1)),
	QUICK_CAST_3(tiered(QUICK_CAST, 2)),

	EXPANSION_1(tiered(EXPANSION, 0)),
	EXPANSION_2(tiered(EXPANSION, 1)),
	EXPANSION_3(tiered(EXPANSION, 2)),

	CONFIGURATION_WAND_DEBUG_VIEW(textured(Textures.AUGMENT_DEBUG_VIEW, 1)),
	AMBER_TORCH_CONCEALED_FLAME(textured(Textures.AUGMENT_CONCEAL, 2)),

	ENDER_WAND_COLLECTOR(textured(Textures.AUGMENT_COLLECTOR, 1)),
	;

	private static AugmentProfile<SimpleAugment> textured(ResourceLocation texture, int descriptions) {
		return AugmentProfile.augment(p -> p.texture(texture).descriptions(descriptions));
	}

	private static AugmentProfile<TieredAugment> tiered(TieredAugmentType type, int index) {
		return type.profile(index);
	}

	private final DeferredHolder<Augment, ? extends SimpleAugment> holder;

	Augments() {
		this(AugmentProfile.augment());
	}

	Augments(@NotNull AugmentProfile<? extends SimpleAugment> profile) {
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

	public boolean is(@NotNull Holder<Augment> augment) {
		return augment.is(id());
	}

	public static void init() {
		TieredAugmentTypes.init();
	}
}
