package gurumirum.gemthing.contents;

import gurumirum.gemthing.capability.GemStat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

public enum Gems implements ItemLike {
	BRIGHTSTONE(GemStat.BRIGHTSTONE),

	AMBER(GemStat.AMBER),
	CITRINE(GemStat.CITRINE),
	AQUAMARINE(GemStat.AQUAMARINE),
	PEARL(GemStat.PEARL),

	PURIFIED_QUARTZ(GemStat.PURIFIED_QUARTZ),
	CRYSTALLIZED_REDSTONE(GemStat.CRYSTALLIZED_REDSTONE),
	POLISHED_LAPIS_LAZULI(GemStat.POLISHED_LAPIS_LAZULI),
	OBSIDIAN(GemStat.OBSIDIAN),

	// diamond
	RUBY(GemStat.RUBY),
	// emerald
	SAPPHIRE(GemStat.SAPPHIRE),

	// amethyst
	TOPAZ(GemStat.TOPAZ),
	MOONSTONE(GemStat.MOONSTONE),
	JET(GemStat.JET),

	BRILLIANT_DIAMOND(GemStat.BRILLIANT_DIAMOND),
	RUBY2(GemStat.RUBY2),
	EMERALD2(GemStat.EMERALD2),
	SAPPHIRE2(GemStat.SAPPHIRE2),

	DAIMONIUM(GemStat.DAIMONIUM);

	public final GemStat stat;
	private final DeferredItem<Item> item;

	Gems(GemStat stat) {
		this(stat, null, null);
	}
	Gems(GemStat stat, @Nullable Function<Item.Properties, Item> itemFactory) {
		this(stat, itemFactory, null);
	}
	Gems(GemStat stat, @Nullable Consumer<Item.Properties> properties) {
		this(stat, null, properties);
	}
	Gems(GemStat stat, @Nullable Function<Item.Properties, Item> itemFactory, @Nullable Consumer<Item.Properties> properties) {
		this.stat = stat;
		this.item = Contents.ITEMS.register(name().toLowerCase(Locale.ROOT), () -> {
			Item.Properties p = new Item.Properties();
			if (properties != null) properties.accept(p);
			return itemFactory == null ? new Item(p) : itemFactory.apply(p);
		});
	}

	public @NotNull ResourceLocation id() {
		return this.item.getId();
	}

	@Override
	public @NotNull Item asItem() {
		return this.item.asItem();
	}

	public boolean isNatural() {
		return this != BRIGHTSTONE && this != PURIFIED_QUARTZ && this.stat.tier < 4;
	}

	public static void init() {}
}
