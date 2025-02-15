package gurumirum.magialucis.contents;

import gurumirum.magialucis.contents.data.Augment;
import gurumirum.magialucis.utils.AugmentProvider;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public enum Augments implements AugmentProvider {
	LUX_CAPACITY_1,
	LUX_CAPACITY_2,
	LUX_CAPACITY_3,

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
							.collect(Collectors.toList())
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

		public Properties descriptions(int descriptions) {
			this.descriptions = descriptions;
			return this;
		}
	}
}
