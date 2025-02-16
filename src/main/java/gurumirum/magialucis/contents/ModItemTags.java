package gurumirum.magialucis.contents;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static gurumirum.magialucis.api.MagiaLucisApi.id;

public final class ModItemTags {
	private ModItemTags() {}

	public static final TagKey<Item> WANDS = ItemTags.create(id("wands"));
	public static final TagKey<Item> BRIGHTSTONES = ItemTags.create(id("brightstones"));
	// well uh, this is the correct pluralization probably
	public static final TagKey<Item> LAPIDES_MANALIS = ItemTags.create(id("lapides_manalis"));

	public static final TagKey<Item> BASIC_ALLOY_INGOTS = ItemTags.create(id("basic_alloy_ingots"));
	public static final TagKey<Item> BASIC_ALLOY_NUGGETS = ItemTags.create(id("basic_alloy_nuggets"));

	public static final TagKey<Item> COPPER_NUGGETS = ItemTags.create(c("nuggets/copper"));

	public static final TagKey<Item> SILVER_INGOTS = ItemTags.create(c("ingots/silver"));
	public static final TagKey<Item> SILVER_NUGGETS = ItemTags.create(c("nuggets/silver"));
	public static final TagKey<Item> SILVER_RAW_MATERIALS = ItemTags.create(c("raw_materials/silver"));

	public static final TagKey<Item> ELECTRUM_INGOTS = ItemTags.create(c("ingots/electrum"));
	public static final TagKey<Item> ELECTRUM_NUGGETS = ItemTags.create(c("nuggets/electrum"));
	public static final TagKey<Item> ROSE_GOLD_INGOTS = ItemTags.create(c("ingots/rose_gold"));
	public static final TagKey<Item> ROSE_GOLD_NUGGETS = ItemTags.create(c("nuggets/rose_gold"));
	public static final TagKey<Item> STERLING_SILVER_INGOTS = ItemTags.create(c("ingots/sterling_silver"));
	public static final TagKey<Item> STERLING_SILVER_NUGGETS = ItemTags.create(c("nuggets/sterling_silver"));

	public static final TagKey<Item> LUMINOUS_ALLOY_INGOTS = ItemTags.create(c("ingots/luminous_alloy"));
	public static final TagKey<Item> LUMINOUS_ALLOY_NUGGETS = ItemTags.create(c("nuggets/luminous_alloy"));

	public static final TagKey<Item> SILVER_BLOCKS = ItemTags.create(c("storage_blocks/silver"));
	public static final TagKey<Item> RAW_SILVER_BLOCKS = ItemTags.create(c("storage_blocks/raw_silver"));
	public static final TagKey<Item> ELECTRUM_BLOCKS = ItemTags.create(c("storage_blocks/electrum"));
	public static final TagKey<Item> ROSE_GOLD_BLOCKS = ItemTags.create(c("storage_blocks/rose_gold"));
	public static final TagKey<Item> STERLING_SILVER_BLOCKS = ItemTags.create(c("storage_blocks/sterling_silver"));
	public static final TagKey<Item> LUMINOUS_ALLOY_BLOCKS = ItemTags.create(c("storage_blocks/luminous_alloy"));

	private static ResourceLocation c(String id) {
		return ResourceLocation.fromNamespaceAndPath("c", id);
	}
}
