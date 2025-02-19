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
	LUX_CAPACITY_1(tiered(LUX_CAPACITY, 0)),
	LUX_CAPACITY_2(tiered(LUX_CAPACITY, 1)),
	LUX_CAPACITY_3(tiered(LUX_CAPACITY, 2)),

	SPEED_1(tiered(SPEED, 0)),
	SPEED_2(tiered(SPEED, 1)),
	SPEED_3(tiered(SPEED, 2)),

	CASTING_SPEED_1(tiered(CASTING_SPEED, 0)),
	CASTING_SPEED_2(tiered(CASTING_SPEED, 1)),
	CASTING_SPEED_3(tiered(CASTING_SPEED, 2)),

	STORAGE_1(tiered(STORAGE, 0)),
	STORAGE_2(tiered(STORAGE, 1)),
	STORAGE_3(tiered(STORAGE, 2)),

	CONFIGURATION_WAND_DEBUG_VIEW(textured(Textures.AUGMENT_DEBUG_VIEW, 1)),
	AMBER_WAND_INVISIBLE_FLAME(textured(Textures.AUGMENT_CONCEAL, 2)),

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
