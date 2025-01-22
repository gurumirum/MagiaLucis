package gurumirum.magialucis.datagen;

import gurumirum.magialucis.contents.BlockProvider;
import gurumirum.magialucis.contents.ModBuildingBlocks;
import gurumirum.magialucis.contents.Ore;
import gurumirum.magialucis.contents.block.ModBlockStateProps;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static gurumirum.magialucis.MagiaLucisMod.MODID;
import static gurumirum.magialucis.MagiaLucisMod.id;
import static gurumirum.magialucis.contents.ModBlocks.*;
import static gurumirum.magialucis.contents.ModBuildingBlocks.*;

public class BlockStateGen extends BlockStateProvider {
	private final ResourceLocation cubeBottomTopMirrored = id("block/pillar_mirrored");

	public BlockStateGen(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, MODID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		s(LAPIS_MANALIS);
		s(LAPIS_MANALIS_BRICKS);

		axisBlock((RotatedPillarBlock)LAPIS_MANALIS_PILLAR.block());
		itemModels().simpleBlockItem(LAPIS_MANALIS_PILLAR.block());

		pillarOrnament(LAPIS_MANALIS_PILLAR_ORNAMENT_DORIC, true);
		pillarOrnament(LAPIS_MANALIS_PILLAR_ORNAMENT_IONIC, true);
		pillarOrnament(LAPIS_MANALIS_PILLAR_ORNAMENT_CORINTHIAN, true);
		pillarOrnament(LAPIS_MANALIS_PILLAR_BASE_DORIC, false);
		pillarOrnament(LAPIS_MANALIS_PILLAR_BASE_IONIC, false);

		ResourceLocation texture = LAPIS_MANALIS.id().withPrefix("block/");
		slabBlock((SlabBlock)LAPIS_MANALIS_SLAB.block(), texture, texture);
		itemModels().simpleBlockItem(LAPIS_MANALIS_SLAB.block());
		stairsBlock((StairBlock)LAPIS_MANALIS_STAIRS.block(), texture);
		itemModels().simpleBlockItem(LAPIS_MANALIS_STAIRS.block());

		texture = LAPIS_MANALIS_BRICKS.id().withPrefix("block/");
		slabBlock((SlabBlock)LAPIS_MANALIS_BRICK_SLAB.block(), texture, texture);
		itemModels().simpleBlockItem(LAPIS_MANALIS_BRICK_SLAB.block());
		stairsBlock((StairBlock)LAPIS_MANALIS_BRICK_STAIRS.block(), texture);
		itemModels().simpleBlockItem(LAPIS_MANALIS_BRICK_STAIRS.block());

		s(SILVER_BLOCK);
		s(RAW_SILVER_BLOCK);
		s(ELECTRUM_BLOCK);
		s(ROSE_GOLD_BLOCK);
		s(STERLING_SILVER_BLOCK);

		simpleBlock(AMBER_LIGHT.block(), new ModelFile.UncheckedModelFile(AMBER_LIGHT.id().withPrefix("block/")));
		models().getBuilder(AMBER_LIGHT.id().getPath()).texture("particle", "block/empty");

		directionalBlock(RELAY.block(), models().getExistingFile(id("block/relay")));

		simpleBlockWithItem(AMBER_CHARGER.block(), models().withExistingParent(AMBER_CHARGER.id().getPath(), id("block/charger"))
				.texture("top", AMBER_CHARGER.id().withPath(p -> "block/" + p + "_top"))
				.texture("side", AMBER_CHARGER.id().withPath(p -> "block/" + p + "_side"))
				.texture("bottom", AMBER_CHARGER.id().withPath(p -> "block/" + p + "_bottom")));

		simpleBlockWithItem(LUMINOUS_CHARGER.block(), models().withExistingParent(LUMINOUS_CHARGER.id().getPath(), id("block/charger"))
				.texture("top", LUMINOUS_CHARGER.id().withPath(p -> "block/" + p + "_top"))
				.texture("side", LUMINOUS_CHARGER.id().withPath(p -> "block/" + p + "_side"))
				.texture("bottom", LUMINOUS_CHARGER.id().withPath(p -> "block/" + p + "_bottom")));

		BlockModelBuilder amberCore = models().cubeColumnHorizontal(AMBER_CORE.id().getPath(),
				id("block/amber_core_side"), mcLoc("block/oak_log_top"));
		BlockModelBuilder amberCoreDisabled = models().cubeColumnHorizontal(AMBER_CORE.id().getPath() + "_disabled",
				id("block/amber_core_side_disabled"), mcLoc("block/oak_log_top"));
		BlockModelBuilder amberCoreOversaturated = models().cubeColumnHorizontal(AMBER_CORE.id().getPath() + "_oversaturated",
				id("block/amber_core_side_oversaturated"), mcLoc("block/oak_log_top"));

		getVariantBuilder(AMBER_CORE.block()).forAllStates(state -> ConfiguredModel.builder()
				.modelFile(state.getValue(ModBlockStateProps.SKYLIGHT_INTERFERENCE) ? amberCoreDisabled :
						state.getValue(ModBlockStateProps.OVERSATURATED) ? amberCoreOversaturated : amberCore)
				.build());
		simpleBlockItem(AMBER_CORE.block(), amberCore);

		simpleBlock(LIGHT_BASIN.block(), new ModelFile.UncheckedModelFile(LIGHT_BASIN.id().withPrefix("block/")));
		itemModels().simpleBlockItem(LIGHT_BASIN.block());

		directionalBlock(SUNLIGHT_CORE.block(), new ModelFile.UncheckedModelFile(SUNLIGHT_CORE.id().withPrefix("block/")));
		itemModels().withExistingParent(SUNLIGHT_CORE.id().getPath(), id("item/block_entity"));

		directionalBlock(MOONLIGHT_CORE.block(), new ModelFile.UncheckedModelFile(MOONLIGHT_CORE.id().withPrefix("block/")));
		itemModels().withExistingParent(MOONLIGHT_CORE.id().getPath(), id("item/block_entity"));

		models().getBuilder(SUNLIGHT_FOCUS.id().getPath()).texture("particle", "block/sunlight_focus_mirror_top_center");
		simpleBlock(SUNLIGHT_FOCUS.block(), new ModelFile.UncheckedModelFile(SUNLIGHT_FOCUS.id().withPrefix("block/")));

		for (Ore ore : Ore.values()) ore.allOreBlocks().forEach(this::simpleBlock);
	}

	private void pillarOrnament(ModBuildingBlocks block, boolean top) {
		ResourceLocation modelAndTextureName = block.id().withPrefix("block/");
		ResourceLocation ornamentEnd = id("block/lapis_manalis");
		ResourceLocation pillarEnd = id("block/lapis_manalis_pillar_end");

		ResourceLocation topTexture = top ? ornamentEnd : pillarEnd;
		ResourceLocation bottomTexture = top ? pillarEnd : ornamentEnd;

		models().cubeBottomTop(block.id().getPath(), modelAndTextureName, bottomTexture, topTexture);

		models().withExistingParent(block.id().getPath() + "_mirrored", cubeBottomTopMirrored)
				.texture("side", modelAndTextureName)
				.texture("bottom", bottomTexture)
				.texture("top", topTexture);

		ModelFile modelFile = new ModelFile.UncheckedModelFile(modelAndTextureName);
		ModelFile modelFileMirrored = new ModelFile.UncheckedModelFile(modelAndTextureName.withSuffix("_mirrored"));

		directionalBlock(block.block(), state -> {
			boolean mirror = switch (state.getValue(BlockStateProperties.FACING)) {
				case UP, NORTH, EAST -> false;
				default -> true;
			};
			return mirror ? modelFileMirrored : modelFile;
		});

		itemModels().simpleBlockItem(block.block());
	}

	private void s(BlockProvider blockProvider) {
		Block b = blockProvider.block();
		simpleBlock(b);
		itemModels().simpleBlockItem(b);
	}
}
