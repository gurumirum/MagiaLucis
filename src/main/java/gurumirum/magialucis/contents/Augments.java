package gurumirum.magialucis.contents;

import gurumirum.magialucis.contents.data.Augment;
import gurumirum.magialucis.utils.AugmentProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public enum Augments implements AugmentProvider {
	LUX_CAPACITY_1,
	LUX_CAPACITY_2(p -> p.precursor(LUX_CAPACITY_1)),
	LUX_CAPACITY_3(p -> p.precursor(LUX_CAPACITY_2)),

	IDK;

	private final DeferredHolder<Augment, Augment> holder;

	Augments() {
		this(null);
	}

	Augments(@Nullable Consumer<Properties> properties) {
		String id = name().toLowerCase(Locale.ROOT);
		this.holder = Contents.AUGMENTS.register(id, () -> {
			Properties p = new Properties();
			if (properties != null) properties.accept(p);
			return new Augment(
					Component.translatable("magialucis.augment.magialucis." + id),
					IntStream.range(0, p.descriptions)
							.mapToObj(i ->
									Component.translatable("magialucis.augment.magialucis." + id + ".description." + i))
							.collect(Collectors.toList()),
					p.precursor != null ? HolderSet.direct(p.precursor.stream().map(AugmentProvider::augment).toList()) : HolderSet.empty(),
					p.incompatible != null ? HolderSet.direct(p.incompatible.stream().map(AugmentProvider::augment).toList()) : HolderSet.empty()
			);
		});
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

	private static final class Properties {
		private int descriptions;
		private @Nullable Set<AugmentProvider> precursor;
		private @Nullable Set<AugmentProvider> incompatible;

		public Properties descriptions(int descriptions) {
			this.descriptions = descriptions;
			return this;
		}

		public Properties precursor(@Nullable AugmentProvider @NotNull ... precursor) {
			this.precursor = Set.of(precursor);
			return this;
		}

		public Properties incompatible(@Nullable AugmentProvider @NotNull ... incompatible) {
			this.incompatible = Set.of(incompatible);
			return this;
		}
	}
}
