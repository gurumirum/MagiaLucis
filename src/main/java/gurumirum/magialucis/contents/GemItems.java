package gurumirum.magialucis.contents;

import gurumirum.magialucis.capability.GemStats;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum GemItems implements ItemLike {
	BRIGHTSTONE(GemStats.BRIGHTSTONE),
	RED_BRIGHTSTONE(GemStats.BRIGHTSTONE),
	ICY_BRIGHTSTONE(GemStats.BRIGHTSTONE),
	SOUL_BRIGHTSTONE(GemStats.SOUL_BRIGHTSTONE),

	AMBER(GemStats.AMBER),
	CITRINE(GemStats.CITRINE),
	IOLITE(GemStats.IOLITE),
	AQUAMARINE(GemStats.AQUAMARINE),
	PEARL(GemStats.PEARL),

	PURIFIED_QUARTZ(GemStats.PURIFIED_QUARTZ),
	CRYSTALLIZED_REDSTONE(GemStats.CRYSTALLIZED_REDSTONE),
	POLISHED_LAPIS_LAZULI(GemStats.POLISHED_LAPIS_LAZULI),
	OBSIDIAN(GemStats.OBSIDIAN),

	// diamond
	RUBY(GemStats.RUBY),
	// emerald
	SAPPHIRE(GemStats.SAPPHIRE),

	// amethyst
	TOPAZ(GemStats.TOPAZ),
	MOONSTONE(GemStats.MOONSTONE),
	JET(GemStats.JET);

	public final GemStats gem;
	private final DeferredItem<Item> item;

	GemItems(GemStats gem) {
		this(gem, ItemProfile.item());
	}
	GemItems(GemStats gem, @NotNull ItemProfile<Item> itemProfile) {
		this.gem = gem;
		this.item = itemProfile.create(name().toLowerCase(Locale.ROOT));
	}

	public @NotNull ResourceLocation id() {
		return this.item.getId();
	}

	@Override
	public @NotNull Item asItem() {
		return this.item.asItem();
	}

	public static void init() {}
}
