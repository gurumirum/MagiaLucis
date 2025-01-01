package gurumirum.gemthing.capability;

import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.impl.RGB332;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public enum GemStat {
	BRIGHTSTONE(0, RGB332.WHITE, 0, 100),

	AMBER(1, RGB332.of(6, 3, 0), 30, 300),
	CITRINE(1, RGB332.of(6, 6, 1), 10, 200),
	AQUAMARINE(1, RGB332.of(0, 5, 3), 20, 160),
	PEARL(1, RGB332.of(7, 6, 3), 50, 150),

	PURIFIED_QUARTZ(2, RGB332.WHITE, 10, 1000),
	CRYSTALLIZED_REDSTONE(2, RGB332.of(5, 0, 0), 400, 800),
	POLISHED_LAPIS_LAZULI(2, RGB332.of(1, 1, 2), 10, 200),
	OBSIDIAN(2, RGB332.of(1, 0, 1), 400, 2000),

	DIAMOND(2, RGB332.of(4, 7, 3), 800, 4000),
	RUBY(2, RGB332.of(7, 0, 0), 50, 5000),
	EMERALD(2, RGB332.of(0, 7, 0), 50, 5000),
	SAPPHIRE(2, RGB332.of(0, 0, 3), 50, 5000),

	AMETHYST(3, RGB332.of(7, 2, 3), 500, 4000),
	TOPAZ(3, RGB332.of(6, 6, 0), 5000, 10000),
	MOONSTONE(3, RGB332.of(4, 6, 3), 1500, 20000),
	JET(3, RGB332.of(0, 0, 0), 100, 20000),

	BRILLIANT_DIAMOND(4, RGB332.of(6, 6, 3), 100, 10000),
	RUBY2(4, RGB332.of(7, 0, 0), 1000, 25000),
	EMERALD2(4, RGB332.of(0, 7, 0), 1000, 25000),
	SAPPHIRE2(4, RGB332.of(0, 0, 3), 1000, 25000),

	// Artificial gems onwards?

	DAIMONIUM(5, RGB332.of(0, 0, 0), Long.MAX_VALUE, Long.MAX_VALUE);

	public final int tier;
	public final byte color;
	public final long minLuxThreshold;
	public final long maxLuxThreshold;

	GemStat(int tier, byte color, long minLuxThreshold, long maxLuxThreshold) {
		if (tier < 0) throw new IllegalArgumentException("tier < 0");
		if (minLuxThreshold < 0) throw new IllegalArgumentException("minLuxThreshold < 0");
		if (maxLuxThreshold < 0) throw new IllegalArgumentException("maxLuxThreshold < 0");
		if (minLuxThreshold > maxLuxThreshold) throw new IllegalArgumentException("minLuxThreshold > maxLuxThreshold");

		this.tier = tier;
		this.color = color;
		this.minLuxThreshold = minLuxThreshold;
		this.maxLuxThreshold = maxLuxThreshold;
	}

	public Item item() {
		return switch (this) {
			case BRIGHTSTONE -> Contents.Gems.BRIGHTSTONE.asItem();
			case AMBER -> Contents.Gems.AMBER.asItem();
			case CITRINE -> Contents.Gems.CITRINE.asItem();
			case AQUAMARINE -> Contents.Gems.AQUAMARINE.asItem();
			case PEARL -> Contents.Gems.PEARL.asItem();
			case PURIFIED_QUARTZ -> Contents.Gems.PURIFIED_QUARTZ.asItem();
			case CRYSTALLIZED_REDSTONE -> Contents.Gems.CRYSTALLIZED_REDSTONE.asItem();
			case POLISHED_LAPIS_LAZULI -> Contents.Gems.POLISHED_LAPIS_LAZULI.asItem();
			case OBSIDIAN -> Contents.Gems.OBSIDIAN.asItem();
			case DIAMOND -> Items.DIAMOND;
			case RUBY -> Contents.Gems.RUBY.asItem();
			case EMERALD -> Items.EMERALD;
			case SAPPHIRE -> Contents.Gems.SAPPHIRE.asItem();
			case AMETHYST -> Items.AMETHYST_SHARD;
			case TOPAZ -> Contents.Gems.TOPAZ.asItem();
			case MOONSTONE -> Contents.Gems.MOONSTONE.asItem();
			case JET -> Contents.Gems.JET.asItem();
			case BRILLIANT_DIAMOND -> Contents.Gems.BRILLIANT_DIAMOND.asItem();
			case RUBY2 -> Contents.Gems.RUBY2.asItem();
			case EMERALD2 -> Contents.Gems.EMERALD2.asItem();
			case SAPPHIRE2 -> Contents.Gems.SAPPHIRE2.asItem();
			case DAIMONIUM -> Contents.Gems.DAIMONIUM.asItem();
		};
	}
}
