package gurumirum.magialucis.datagen;

import gurumirum.magialucis.contents.ModBuildingBlocks;
import gurumirum.magialucis.contents.Ore;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static gurumirum.magialucis.MagiaLucisMod.MODID;
import static gurumirum.magialucis.MagiaLucisMod.id;
import static gurumirum.magialucis.contents.ModBlocks.RELAY;
import static gurumirum.magialucis.contents.ModBlocks.SUNLIGHT_FOCUS;
import static gurumirum.magialucis.contents.ModBuildingBlocks.*;

public class BlockStateGen extends BlockStateProvider {
	private final ResourceLocation cubeBottomTopMirrored = id("block/pillar_mirrored");

	public BlockStateGen(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, MODID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		simpleBlock(LAPIS_MANALIS.block());
		itemModels().simpleBlockItem(LAPIS_MANALIS.block());
		simpleBlock(LAPIS_MANALIS_BRICKS.block());
		itemModels().simpleBlockItem(LAPIS_MANALIS_BRICKS.block());

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

		simpleBlock(SILVER_BLOCK.block());
		itemModels().simpleBlockItem(SILVER_BLOCK.block());
		simpleBlock(RAW_SILVER_BLOCK.block());
		itemModels().simpleBlockItem(RAW_SILVER_BLOCK.block());

		directionalBlock(RELAY.block(), models().getExistingFile(id("block/relay")));
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
}
