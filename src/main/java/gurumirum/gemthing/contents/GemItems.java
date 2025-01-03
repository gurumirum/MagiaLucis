package gurumirum.gemthing.contents;

import gurumirum.gemthing.capability.Gems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum GemItems implements ItemLike {
	BRIGHTSTONE(Gems.BRIGHTSTONE),
	RED_BRIGHTSTONE(Gems.BRIGHTSTONE),
	SOUL_BRIGHTSTONE(Gems.SOUL_BRIGHTSTONE),

	AMBER(Gems.AMBER),
	CITRINE(Gems.CITRINE),
	AQUAMARINE(Gems.AQUAMARINE),
	PEARL(Gems.PEARL),

	PURIFIED_QUARTZ(Gems.PURIFIED_QUARTZ),
	CRYSTALLIZED_REDSTONE(Gems.CRYSTALLIZED_REDSTONE),
	POLISHED_LAPIS_LAZULI(Gems.POLISHED_LAPIS_LAZULI),
	OBSIDIAN(Gems.OBSIDIAN),

	// diamond
	RUBY(Gems.RUBY),
	// emerald
	SAPPHIRE(Gems.SAPPHIRE),

	// amethyst
	TOPAZ(Gems.TOPAZ),
	MOONSTONE(Gems.MOONSTONE),
	JET(Gems.JET),

	BRILLIANT_DIAMOND(Gems.BRILLIANT_DIAMOND),
	RUBY2(Gems.RUBY2),
	EMERALD2(Gems.EMERALD2),
	SAPPHIRE2(Gems.SAPPHIRE2),

	DAIMONIUM(Gems.DAIMONIUM);

	public final Gems gem;
	private final DeferredItem<Item> item;

	GemItems(Gems gem) {
		this(gem, ItemProfile.item());
	}
	GemItems(Gems gem, @NotNull ItemProfile<Item> itemProfile) {
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

	public boolean isNatural() {
		return this != BRIGHTSTONE && this != RED_BRIGHTSTONE && this != SOUL_BRIGHTSTONE && this != PURIFIED_QUARTZ && this.gem.tier < 4;
	}

	public static void init() {}
}
