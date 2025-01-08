package gurumirum.gemthing.capability;

import gurumirum.gemthing.contents.GemItems;
import gurumirum.gemthing.impl.RGB332;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Consumer;

public enum GemStats implements LuxStat {
	BRIGHTSTONE(0, RGB332.WHITE, 0, 100, false),
	SOUL_BRIGHTSTONE(0, RGB332.of(1, 0, 0), 0, 100, false),

	AMBER(1, RGB332.of(6, 3, 0), 30, 300),
	CITRINE(1, RGB332.of(6, 6, 1), 10, 200),
	AQUAMARINE(1, RGB332.of(0, 5, 3), 20, 160),
	PEARL(1, RGB332.of(7, 6, 3), 50, 150),

	PURIFIED_QUARTZ(2, RGB332.WHITE, 10, 1000, false),
	CRYSTALLIZED_REDSTONE(2, RGB332.of(5, 0, 0), 400, 800),
	POLISHED_LAPIS_LAZULI(2, RGB332.of(1, 1, 2), 10, 200),
	OBSIDIAN(2, RGB332.of(1, 0, 1), 400, 2000),

	DIAMOND(2, RGB332.of(4, 7, 3), 800, 4000, true, true),
	RUBY(2, RGB332.of(7, 0, 0), 50, 5000),
	EMERALD(2, RGB332.of(0, 7, 0), 50, 5000, true, true),
	SAPPHIRE(2, RGB332.of(0, 0, 3), 50, 5000),

	AMETHYST(3, RGB332.of(7, 2, 3), 500, 4000, true, true),
	TOPAZ(3, RGB332.of(6, 6, 0), 5000, 10000),
	MOONSTONE(3, RGB332.of(4, 6, 3), 1500, 20000),
	JET(3, RGB332.of(0, 0, 0), 100, 20000),

	BRILLIANT_DIAMOND(4, RGB332.of(6, 6, 3), 100, 10000, false),
	RUBY2(4, RGB332.of(7, 0, 0), 1000, 25000, false),
	EMERALD2(4, RGB332.of(0, 7, 0), 1000, 25000, false),
	SAPPHIRE2(4, RGB332.of(0, 0, 3), 1000, 25000, false),

	// Artificial gems onwards?

	DAIMONIUM(5, RGB332.of(0, 0, 0), Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, false);

	private final byte color;
	private final double minLuxThreshold;
	private final boolean hasTag;
	private final boolean vanilla;
	private final double rMaxTransfer;
	private final double gMaxTransfer;
	private final double bMaxTransfer;

	private @Nullable TagKey<Item> tag;

	GemStats(int tier, byte color, double minLuxThreshold, double maxLuxThreshold) {
		this(tier, color, minLuxThreshold, maxLuxThreshold, true, false);
	}

	GemStats(int tier, byte color, double minLuxThreshold, double maxLuxThreshold, boolean hasTag) {
		this(tier, color, minLuxThreshold, maxLuxThreshold, hasTag, false);
	}

	GemStats(int tier, byte color, double minLuxThreshold, double maxLuxThreshold, boolean hasTag, boolean vanilla) {
		this.vanilla = vanilla;
		if (tier < 0) throw new IllegalArgumentException("tier < 0");
		if (minLuxThreshold < 0) throw new IllegalArgumentException("minLuxThreshold < 0");
		if (maxLuxThreshold < 0) throw new IllegalArgumentException("maxLuxThreshold < 0");
		if (minLuxThreshold > maxLuxThreshold) throw new IllegalArgumentException("minLuxThreshold > maxLuxThreshold");

		this.color = color;
		this.minLuxThreshold = minLuxThreshold;
		this.hasTag = hasTag;
		this.rMaxTransfer = maxLuxThreshold * RGB332.rBrightness(color);
		this.gMaxTransfer = maxLuxThreshold * RGB332.gBrightness(color);
		this.bMaxTransfer = maxLuxThreshold * RGB332.bBrightness(color);
	}

	public boolean hasTag() {
		return this.hasTag;
	}

	public boolean isVanilla() {
		return this.vanilla;
	}

	public @NotNull TagKey<Item> tag() {
		if (!this.hasTag) throw new IllegalStateException("Gem " + this + " does not have item tag");
		if (this.tag == null) {
			this.tag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c",
					"gems/" + name().toLowerCase(Locale.ROOT)));
		}
		return this.tag;
	}

	public void forEachItem(Consumer<Item> consumer){
		if (this == GemStats.BRIGHTSTONE) {
			consumer.accept(GemItems.BRIGHTSTONE.asItem());
			consumer.accept(GemItems.RED_BRIGHTSTONE.asItem());
			consumer.accept(GemItems.ICY_BRIGHTSTONE.asItem());
		} else {
			consumer.accept(item());
		}
	}

	public Item item() {
		return switch (this) {
			case BRIGHTSTONE -> GemItems.BRIGHTSTONE.asItem();
			case SOUL_BRIGHTSTONE -> GemItems.SOUL_BRIGHTSTONE.asItem();
			case AMBER -> GemItems.AMBER.asItem();
			case CITRINE -> GemItems.CITRINE.asItem();
			case AQUAMARINE -> GemItems.AQUAMARINE.asItem();
			case PEARL -> GemItems.PEARL.asItem();
			case PURIFIED_QUARTZ -> GemItems.PURIFIED_QUARTZ.asItem();
			case CRYSTALLIZED_REDSTONE -> GemItems.CRYSTALLIZED_REDSTONE.asItem();
			case POLISHED_LAPIS_LAZULI -> GemItems.POLISHED_LAPIS_LAZULI.asItem();
			case OBSIDIAN -> GemItems.OBSIDIAN.asItem();
			case DIAMOND -> Items.DIAMOND;
			case RUBY -> GemItems.RUBY.asItem();
			case EMERALD -> Items.EMERALD;
			case SAPPHIRE -> GemItems.SAPPHIRE.asItem();
			case AMETHYST -> Items.AMETHYST_SHARD;
			case TOPAZ -> GemItems.TOPAZ.asItem();
			case MOONSTONE -> GemItems.MOONSTONE.asItem();
			case JET -> GemItems.JET.asItem();
			case BRILLIANT_DIAMOND -> GemItems.BRILLIANT_DIAMOND.asItem();
			case RUBY2 -> GemItems.RUBY2.asItem();
			case EMERALD2 -> GemItems.EMERALD2.asItem();
			case SAPPHIRE2 -> GemItems.SAPPHIRE2.asItem();
			case DAIMONIUM -> GemItems.DAIMONIUM.asItem();
		};
	}

	@Override
	public byte color() {
		return this.color;
	}
	@Override
	public double minLuxThreshold() {
		return this.minLuxThreshold;
	}
	@Override
	public double rMaxTransfer() {
		return this.rMaxTransfer;
	}
	@Override
	public double gMaxTransfer() {
		return this.gMaxTransfer;
	}
	@Override
	public double bMaxTransfer() {
		return this.bMaxTransfer;
	}
}
