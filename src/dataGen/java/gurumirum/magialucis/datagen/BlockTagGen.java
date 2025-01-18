package gurumirum.magialucis.datagen;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.ModBlockTags;
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

import static gurumirum.magialucis.contents.ModBlocks.*;
import static gurumirum.magialucis.contents.ModBuildingBlocks.*;

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
			var tags = c("ores/" + ore.oreId());
			for (OreType oreType : OreType.values()) {
				if (!ore.exists(oreType)) continue;

				Block oreBlock = ore.expectOreBlock(oreType);
				tags.add(oreBlock);
				ores.add(oreBlock);

				switch (oreType) {
					case STONE -> stoneOres.add(oreBlock);
					case DEEPSLATE -> deepslateOres.add(oreBlock);
					case NETHER -> netherOres.add(oreBlock);
				}

				tag(BlockTags.MINEABLE_WITH_PICKAXE).add(oreBlock);

				switch (ore.miningLevel()) {
					case WOOD -> {}
					case STONE -> tag(BlockTags.NEEDS_STONE_TOOL).add(oreBlock);
					case IRON -> tag(BlockTags.NEEDS_IRON_TOOL).add(oreBlock);
					case DIAMOND -> tag(BlockTags.NEEDS_DIAMOND_TOOL).add(oreBlock);
				}
			}
		}

		tag(ModBlockTags.SILVER_BLOCKS).add(SILVER_BLOCK.block());
		tag(ModBlockTags.RAW_SILVER_BLOCKS).add(RAW_SILVER_BLOCK.block());
		tag(ModBlockTags.ELECTRUM_BLOCKS).add(ELECTRUM_BLOCK.block());
		tag(ModBlockTags.ROSE_GOLD_BLOCKS).add(ROSE_GOLD_BLOCK.block());
		tag(ModBlockTags.STERLING_SILVER_BLOCKS).add(STERLING_SILVER_BLOCK.block());

		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
				REMOTE_CHARGER.block(),
				REMOTE_CHARGER_2.block(),
				RELAY.block(),
				LIGHT_BASIN.block(),
				SUNLIGHT_CORE.block(),
				MOONLIGHT_CORE.block(),
				SUNLIGHT_FOCUS.block(),
				LUX_SOURCE.block(),
				LUX_SOURCE_2.block(),
				FIELD_MONITOR.block(),

				LAPIS_MANALIS.block(),
				LAPIS_MANALIS_BRICKS.block(),
				LAPIS_MANALIS_PILLAR.block(),
				LAPIS_MANALIS_PILLAR_ORNAMENT_DORIC.block(),
				LAPIS_MANALIS_PILLAR_ORNAMENT_IONIC.block(),
				LAPIS_MANALIS_PILLAR_ORNAMENT_CORINTHIAN.block(),
				LAPIS_MANALIS_PILLAR_BASE_DORIC.block(),
				LAPIS_MANALIS_PILLAR_BASE_IONIC.block(),
				LAPIS_MANALIS_SLAB.block(),
				LAPIS_MANALIS_BRICK_SLAB.block(),
				LAPIS_MANALIS_STAIRS.block(),
				LAPIS_MANALIS_BRICK_STAIRS.block(),
				SILVER_BLOCK.block(),
				RAW_SILVER_BLOCK.block(),
				ELECTRUM_BLOCK.block(),
				ROSE_GOLD_BLOCK.block(),
				STERLING_SILVER_BLOCK.block());

		tag(BlockTags.MINEABLE_WITH_AXE).add(
				AMBER_CORE.block());

		tag(BlockTags.NEEDS_STONE_TOOL).add(
				SILVER_BLOCK.block(),
				ELECTRUM_BLOCK.block(),
				ROSE_GOLD_BLOCK.block(),
				STERLING_SILVER_BLOCK.block());

		tag(BlockTags.BEACON_BASE_BLOCKS).add(
				SILVER_BLOCK.block(),
				ELECTRUM_BLOCK.block(),
				ROSE_GOLD_BLOCK.block(),
				STERLING_SILVER_BLOCK.block());

		tag(ModBlockTags.LAPIDES_MANALIS).add(
				LAPIS_MANALIS.block(),
				LAPIS_MANALIS_BRICKS.block(),
				LAPIS_MANALIS_PILLAR.block(),
				LAPIS_MANALIS_PILLAR_ORNAMENT_DORIC.block(),
				LAPIS_MANALIS_PILLAR_ORNAMENT_IONIC.block(),
				LAPIS_MANALIS_PILLAR_ORNAMENT_CORINTHIAN.block(),
				LAPIS_MANALIS_PILLAR_BASE_DORIC.block(),
				LAPIS_MANALIS_PILLAR_BASE_IONIC.block());
	}

	private IntrinsicTagAppender<Block> c(String path) {
		return tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", path)));
	}
}
