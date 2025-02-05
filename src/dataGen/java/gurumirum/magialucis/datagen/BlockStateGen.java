package gurumirum.magialucis.datagen;

import gurumirum.magialucis.contents.BlockProvider;
import gurumirum.magialucis.contents.ModBuildingBlocks;
import gurumirum.magialucis.contents.Ore;
import gurumirum.magialucis.contents.block.ModBlockStateProps;
import gurumirum.magialucis.contents.block.lux.lightloom.LightLoomType;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Direction;
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
import org.jetbrains.annotations.Nullable;

import java.util.Map;

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

		lantern(AMBER_LANTERN.block(), "amber_lantern", "amber_lantern");
		lantern(LUMINOUS_LANTERN_BASE.block(), "luminous_lantern", null);
		lantern(LUMINOUS_RESONANCE_LANTERN.block(), "luminous_lantern", "luminous_resonance_lantern");

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

		var artisanryTableLeft = new ModelFile.UncheckedModelFile(id("block/artisanry_table_left"));
		var artisanryTableRight = new ModelFile.UncheckedModelFile(id("block/artisanry_table_right"));
		var artisanryTableLeftL = new ModelFile.UncheckedModelFile(id("block/artisanry_table_left_lightloom"));
		var artisanryTableRightL = new ModelFile.UncheckedModelFile(id("block/artisanry_table_right_lightloom"));
		horizontalBlock(ARTISANRY_TABLE.block(), state ->
				state.getValue(ModBlockStateProps.LEFT) ?
						state.getValue(ModBlockStateProps.LIGHTLOOM) ? artisanryTableLeftL : artisanryTableLeft :
						state.getValue(ModBlockStateProps.LIGHTLOOM) ? artisanryTableRightL : artisanryTableRight);

		var lightloom = new ModelFile.UncheckedModelFile(id("block/lightloom"));
		var lightloomItem = new ModelFile.UncheckedModelFile(id("item/lightloom"));

		for (LightLoomType type : LightLoomType.values()) {
			horizontalBlock(type.block(), lightloom);
			simpleBlockItem(type.block(), lightloomItem);
		}

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

	private final Map<String, ModelFile> lanternModels = new Object2ObjectOpenHashMap<>();

	private void lantern(Block block, String baseName, @Nullable String overlayName) {
		getVariantBuilder(block).forAllStates(state -> {
			Direction dir = state.getValue(BlockStateProperties.FACING);
			ConfiguredModel.Builder<?> b = ConfiguredModel.builder();

			boolean _enabled = overlayName != null && (
					state.hasProperty(BlockStateProperties.ENABLED) ?
							state.getValue(BlockStateProperties.ENABLED) : true);
			b.modelFile(lantern(dir, _enabled, baseName, overlayName));

			if (dir.getAxis() != Direction.Axis.Y) {
				b.rotationY((int)(dir.toYRot()) % 360);
			}

			return b.build();
		});
		simpleBlockItem(block, lantern(Direction.DOWN, overlayName != null, baseName, overlayName));
	}

	private ModelFile lantern(Direction facing, boolean enabled, String baseName, @Nullable String overlayName) {
		String s = switch (facing) {
			case DOWN -> "up";
			case UP -> "down";
			default -> "side";
		};
		String modelName = baseName + "_" + s + (enabled ? "" : "_disabled");

		return this.lanternModels.computeIfAbsent(modelName, n -> {
			BlockModelBuilder b = models().withExistingParent(n, id("block/lantern_" + s));
			b.texture("top", id("block/" + baseName + "_top"));
			b.texture("bottom", id("block/" + baseName + "_bottom"));
			b.texture("side", id("block/" + baseName + "_side"));
			b.texture("side_overlay", id(enabled ? "block/" + overlayName + "_side_overlay" : "block/empty"));
			if (facing == Direction.UP) {
				b.texture("base_top", id("block/" + baseName + "_base_top"));
				b.texture("base_bottom", id("block/" + baseName + "_base_bottom"));
			} else if (facing != Direction.DOWN) {
				b.texture("back", id("block/" + baseName + "_back"));
				b.texture("base_back", id("block/" + baseName + "_base_back"));
			}
			return b;
		});
	}

	private void s(BlockProvider blockProvider) {
		Block b = blockProvider.block();
		simpleBlock(b);
		itemModels().simpleBlockItem(b);
	}
}
