package gurumirum.magialucis.contents.augment;

import gurumirum.magialucis.api.augment.Augment;
import gurumirum.magialucis.api.augment.SimpleAugment;
import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.contents.profile.AugmentProfile;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TieredAugmentType {
	public static TieredAugmentType create(@NotNull String baseName, int levels) {
		return create(baseName, levels, null);
	}

	public static TieredAugmentType create(@NotNull String baseName, int levels,
	                                       @Nullable PropertyOperation properties) {
		return create(Contents.AUGMENTS, baseName, levels, properties);
	}

	public static TieredAugmentType create(@NotNull DeferredRegister<Augment> register, @NotNull String baseName,
	                                       int levels) {
		return create(register, baseName, levels, null);
	}

	public static TieredAugmentType create(@NotNull DeferredRegister<Augment> register, @NotNull String baseName,
	                                       int levels, @Nullable PropertyOperation properties) {
		return createCustom(register, baseName, levels, TieredAugmentFactory.DEFAULT, properties);
	}

	public static TieredAugmentType createCustom(@NotNull String baseName, int levels,
	                                             @NotNull TieredAugmentFactory augmentFactory) {
		return createCustom(baseName, levels, augmentFactory, null);
	}

	public static TieredAugmentType createCustom(@NotNull String baseName, int levels,
	                                             @NotNull TieredAugmentFactory augmentFactory,
	                                             @Nullable PropertyOperation properties) {
		return createCustom(Contents.AUGMENTS, baseName, levels, augmentFactory, properties);
	}

	public static TieredAugmentType createCustom(@NotNull DeferredRegister<Augment> register, @NotNull String baseName,
	                                             int levels, @NotNull TieredAugmentFactory augmentFactory) {
		return createCustom(register, baseName, levels, augmentFactory, null);
	}

	public static TieredAugmentType createCustom(@NotNull DeferredRegister<Augment> register, @NotNull String baseName,
	                                             int levels, @NotNull TieredAugmentFactory augmentFactory,
	                                             @Nullable PropertyOperation properties) {
		return new TieredAugmentType(register, baseName, levels, augmentFactory, properties);
	}

	private final List<DeferredHolder<Augment, TieredAugment>> augments;
	private final ResourceLocation baseName;

	private @Nullable ResourceLocation tierlessTexture;
	private @Nullable ResourceLocation @Nullable [] tieredTextures;

	private TieredAugmentType(@NotNull DeferredRegister<Augment> register, @NotNull String baseName, int levels,
	                          @NotNull TieredAugmentFactory augmentFactory,
	                          @Nullable PropertyOperation properties) {
		if (levels <= 0) throw new IllegalArgumentException("levels <= 0");
		this.baseName = ResourceLocation.fromNamespaceAndPath(register.getNamespace(), baseName);

		var a = new ArrayList<DeferredHolder<Augment, TieredAugment>>();

		for (int i = 0; i < levels; i++) {
			int index = i;
			String name = baseName + "_" + (i + 1);
			a.add(register.register(name, () -> {
				SimpleAugment.Properties p = new SimpleAugment.Properties();
				if (properties != null) properties.accept(p, index);
				return augmentFactory.create(p, this, index);
			}));
		}

		this.augments = List.copyOf(a);
	}

	public @NotNull ResourceLocation baseName() {
		return this.baseName;
	}

	public @NotNull @Unmodifiable List<? extends Holder<Augment>> augments() {
		return this.augments;
	}

	public int levels() {
		return augments().size();
	}

	public @NotNull Holder<Augment> get(int index) {
		return augments().get(index);
	}

	public @NotNull ResourceLocation getTierlessTexture() {
		if (this.tierlessTexture == null) {
			this.tierlessTexture = baseName();
		}
		return this.tierlessTexture;
	}

	public @NotNull ResourceLocation getTieredTexture(int index) {
		if (this.tieredTextures == null) {
			this.tieredTextures = new ResourceLocation[levels()];
		}
		if (this.tieredTextures[index] == null) {
			this.tieredTextures[index] = baseName().withSuffix("_" + (index + 1));
		}
		return Objects.requireNonNull(this.tieredTextures[index]);
	}

	public @NotNull AugmentProfile<TieredAugment> profile(int index) {
		var holder = this.augments.get(index);
		return id -> {
			if (!Objects.equals(id, holder.getId().getPath()))
				throw new IllegalArgumentException("Mismatching ID of tiered augment, expected: " +
						holder.getId().getPath() + ", provided: " + id);
			return holder;
		};
	}

	@FunctionalInterface
	public interface TieredAugmentFactory {
		TieredAugmentFactory DEFAULT = TieredAugment::new;

		@NotNull TieredAugment create(SimpleAugment.@NotNull Properties p, @NotNull TieredAugmentType t, int i);
	}

	@FunctionalInterface
	public interface PropertyOperation {
		void accept(@NotNull SimpleAugment.Properties p, int i);
	}
}
