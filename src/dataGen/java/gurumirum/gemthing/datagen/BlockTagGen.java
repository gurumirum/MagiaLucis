package gurumirum.gemthing.datagen;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.contents.ModBlocks;
import gurumirum.gemthing.contents.NormalOres;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BlockTagGen extends BlockTagsProvider {
	public BlockTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, GemthingMod.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider) {
		for (NormalOres ore : NormalOres.values()) {
			IntrinsicTagAppender<Block> tags = tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "ores/" + ore.oreId())));
			if (ore.hasOre()) tags.add(ore.ore());
			if (ore.hasDeepslateOre()) tags.add(ore.deepslateOre());
		}

		c("storage_blocks/silver").add(ModBlocks.SILVER.block());
		c("storage_blocks/raw_silver").add(ModBlocks.RAW_SILVER_BLOCK.block());

		tag(BlockTags.BEACON_BASE_BLOCKS).add(ModBlocks.SILVER.block());
	}

	private IntrinsicTagAppender<Block> c(String path) {
		return tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", path)));
	}
}
