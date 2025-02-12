package gurumirum.magialucis.contents;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum GemItems implements ItemLike {
	BRIGHTSTONE(Gem.BRIGHTSTONE),
	RED_BRIGHTSTONE(Gem.BRIGHTSTONE),
	ICY_BRIGHTSTONE(Gem.BRIGHTSTONE),
	SOUL_BRIGHTSTONE(Gem.SOUL_BRIGHTSTONE),

	AMBER(Gem.AMBER),
	CITRINE(Gem.CITRINE),
	IOLITE(Gem.IOLITE),
	AQUAMARINE(Gem.AQUAMARINE),
	PEARL(Gem.PEARL),
	OBSIDIAN(Gem.OBSIDIAN),

	PURIFIED_QUARTZ(Gem.PURIFIED_QUARTZ),
	CRYSTALLIZED_REDSTONE(Gem.CRYSTALLIZED_REDSTONE),
	POLISHED_LAPIS_LAZULI(Gem.POLISHED_LAPIS_LAZULI),

	RUBY(Gem.RUBY),
	SAPPHIRE(Gem.SAPPHIRE),

	TOPAZ(Gem.TOPAZ),
	MOONSTONE(Gem.MOONSTONE),
	JET(Gem.JET);

	public final Gem gem;
	private final DeferredItem<Item> item;

	GemItems(Gem gem) {
		this(gem, ItemProfile.item());
	}
	GemItems(Gem gem, @NotNull ItemProfile<Item> itemProfile) {
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
