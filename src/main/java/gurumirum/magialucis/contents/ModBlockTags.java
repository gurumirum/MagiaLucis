package gurumirum.magialucis.contents;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import static gurumirum.magialucis.api.MagiaLucisApi.id;

public final class ModBlockTags {
	private ModBlockTags() {}

	// well uh, this is the correct pluralization probably
	public static final TagKey<Block> LAPIDES_MANALIS = BlockTags.create(id("lapides_manalis"));

	public static final TagKey<Block> SILVER_BLOCKS = BlockTags.create(c("storage_blocks/silver"));
	public static final TagKey<Block> RAW_SILVER_BLOCKS = BlockTags.create(c("storage_blocks/raw_silver"));
	public static final TagKey<Block> ELECTRUM_BLOCKS = BlockTags.create(c("storage_blocks/electrum"));
	public static final TagKey<Block> ROSE_GOLD_BLOCKS = BlockTags.create(c("storage_blocks/rose_gold"));
	public static final TagKey<Block> STERLING_SILVER_BLOCKS = BlockTags.create(c("storage_blocks/sterling_silver"));
	public static final TagKey<Block> LUMINOUS_ALLOY_BLOCKS = BlockTags.create(c("storage_blocks/luminous_alloy"));

	private static ResourceLocation c(String id) {
		return ResourceLocation.fromNamespaceAndPath("c", id);
	}
}
