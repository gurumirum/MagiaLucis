package gurumirum.magialucis.datagen;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.Ore;
import gurumirum.magialucis.contents.OreType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BlockTagGen extends BlockTagsProvider {
	public BlockTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, MagiaLucisMod.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider) {
		var ores = tag(Tags.Blocks.ORES);
		var stoneOres = tag(Tags.Blocks.ORES_IN_GROUND_STONE);
		var deepslateOres = tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE);
		var netherOres = tag(Tags.Blocks.ORES_IN_GROUND_NETHERRACK);

		for (Ore ore : Ore.values()) {
			var tags = tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "ores/" + ore.oreId())));
			for (OreType oreType : OreType.values()) {
				if (ore.exists(oreType)) {
					Block oreBlock = ore.expectOreBlock(oreType);
					tags.add(oreBlock);
					ores.add(oreBlock);

					switch (oreType) {
						case STONE -> stoneOres.add(oreBlock);
						case DEEPSLATE -> deepslateOres.add(oreBlock);
						case NETHER -> netherOres.add(oreBlock);
					}
				}
			}
		}

		c("storage_blocks/silver").add(ModBlocks.SILVER.block());
		c("storage_blocks/raw_silver").add(ModBlocks.RAW_SILVER_BLOCK.block());

		tag(BlockTags.BEACON_BASE_BLOCKS).add(ModBlocks.SILVER.block());
	}

	private IntrinsicTagAppender<Block> c(String path) {
		return tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", path)));
	}
}
