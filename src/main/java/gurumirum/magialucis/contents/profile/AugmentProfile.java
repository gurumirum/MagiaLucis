package gurumirum.magialucis.contents.profile;

import gurumirum.magialucis.api.augment.Augment;
import gurumirum.magialucis.api.augment.SimpleAugment;
import gurumirum.magialucis.contents.Contents;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface AugmentProfile<A extends SimpleAugment> {
	AugmentProfile<SimpleAugment> DEFAULT = augment(null);

	static AugmentProfile<SimpleAugment> augment() {
		return DEFAULT;
	}

	static AugmentProfile<SimpleAugment> augment(@Nullable Consumer<SimpleAugment.Properties> properties) {
		return customAugment(ProfileInternals.defaultAugmentFactory, properties);
	}

	static <A extends SimpleAugment> AugmentProfile<A> customAugment(@NotNull Function<SimpleAugment.Properties, A> augmentFactory) {
		return customAugment(augmentFactory, null);
	}

	static <A extends SimpleAugment> AugmentProfile<A> customAugment(@NotNull Function<SimpleAugment.Properties, A> augmentFactory,
	                                                        @Nullable Consumer<SimpleAugment.Properties> properties) {
		return id -> Contents.AUGMENTS.register(id, () -> {
			SimpleAugment.Properties p = new SimpleAugment.Properties();
			if (properties != null) properties.accept(p);
			return augmentFactory.apply(p);
		});
	}


	@NotNull DeferredHolder<Augment, A> create(@NotNull String id);
}
