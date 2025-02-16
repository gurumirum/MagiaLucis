package gurumirum.magialucis.datagen;

import gurumirum.magialucis.contents.ModBuildingBlocks;
import gurumirum.magialucis.contents.Ore;
import gurumirum.magialucis.contents.block.ModBlockStates;
import gurumirum.magialucis.contents.block.lux.lightloom.LightLoomType;
import gurumirum.magialucis.contents.block.lux.splitter.SplitterBlockEntity;
import gurumirum.magialucis.utils.BlockProvider;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
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

import static gurumirum.magialucis.api.MagiaLucisApi.MODID;
import static gurumirum.magialucis.api.MagiaLucisApi.id;
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
		s(LUMINOUS_ALLOY_BLOCK);

		simpleBlock(AMBER_LIGHT.block(), defaultModel(AMBER_LIGHT.id()));
		models().getBuilder(AMBER_LIGHT.id().getPath()).texture("particle", "block/empty");

		directionalBlock(RELAY.block(), models().getExistingFile(id("block/relay")));

		directionalBlock(SPLITTER.block(), defaultModel(SPLITTER.id()));
		itemModels().withExistingParent(SPLITTER.id().getPath(), id("item/block_entity"))
				.transforms()
				.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
				.rotation(0, 225, 0)
				.translation(0, 4, 0)
				.scale(0.4f)
				.end()
				.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
				.rotation(0, 45, 0)
				.translation(0, 4, 0)
				.scale(0.4f)
				.end()
				.end();

		directionalBlock(CONNECTOR.block(), defaultModel(CONNECTOR.id()));
		itemModels().withExistingParent(CONNECTOR.id().getPath(), id("item/block_entity"))
				.transforms()
				.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
				.rotation(0, 225, 0)
				.translation(0, 4, 0)
				.scale(0.4f)
				.end()
				.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
				.rotation(0, 45, 0)
				.translation(0, 4, 0)
				.scale(0.4f)
				.end()
				.transform(ItemDisplayContext.GUI)
				.rotation(30, 225, 0)
				.translation(0, 1.5f, 0)
				.scale(0.625f)
				.end()
				.end();

		for (Direction side : Direction.values()) {
			for (byte apertureLevel = 0; apertureLevel < SplitterBlockEntity.APERTURE_LEVELS; apertureLevel++) {
				splitterSide(side, apertureLevel);
			}
		}

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
				.modelFile(state.getValue(ModBlockStates.SKYLIGHT_INTERFERENCE) ? amberCoreDisabled :
						state.getValue(ModBlockStates.OVERSATURATED) ? amberCoreOversaturated : amberCore)
				.build());
		simpleBlockItem(AMBER_CORE.block(), amberCore);

		simpleBlock(LIGHT_BASIN.block(), defaultModel(LIGHT_BASIN.id()));
		itemModels().simpleBlockItem(LIGHT_BASIN.block());

		directionalBlock(SUNLIGHT_CORE.block(), defaultModel(SUNLIGHT_CORE.id()));
		itemModels().withExistingParent(SUNLIGHT_CORE.id().getPath(), id("item/block_entity"));

		directionalBlock(MOONLIGHT_CORE.block(), defaultModel(MOONLIGHT_CORE.id()));
		itemModels().withExistingParent(MOONLIGHT_CORE.id().getPath(), id("item/block_entity"));

		simpleBlock(SUNLIGHT_FOCUS.block(), models().getBuilder(SUNLIGHT_FOCUS.id().getPath())
				.texture("particle", "block/sunlight_focus_mirror_top_center"));

		var artisanryTableLeft = new ModelFile.UncheckedModelFile(id("block/artisanry_table_left"));
		var artisanryTableRight = new ModelFile.UncheckedModelFile(id("block/artisanry_table_right"));
		var artisanryTableLeftL = new ModelFile.UncheckedModelFile(id("block/artisanry_table_left_lightloom"));
		var artisanryTableRightL = new ModelFile.UncheckedModelFile(id("block/artisanry_table_right_lightloom"));
		horizontalBlock(ARTISANRY_TABLE.block(), state ->
				state.getValue(ModBlockStates.LEFT) ?
						state.getValue(ModBlockStates.LIGHTLOOM) ? artisanryTableLeftL : artisanryTableLeft :
						state.getValue(ModBlockStates.LIGHTLOOM) ? artisanryTableRightL : artisanryTableRight);

		var lightloom = new ModelFile.UncheckedModelFile(id("block/lightloom"));
		var lightloomItem = new ModelFile.UncheckedModelFile(id("item/lightloom"));

		horizontalBlock(LIGHTLOOM_BASE.block(), lightloom);

		for (LightLoomType type : LightLoomType.values()) {
			horizontalBlock(type.block(), lightloom);
			simpleBlockItem(type.block(), lightloomItem);
		}

		s(PRIMITIVE_LUX_SOURCE);
		s(LUMINOUS_LUX_SOURCE);
		s(LUSTROUS_LUX_SOURCE);
		s(IRRADIANT_LUX_SOURCE);
		s(EXUBERANT_LUX_SOURCE);

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

	private void splitterSide(Direction side, int apertureLevel) {
		final float p1 = 1, p2 = 15;

		float x1, x2, y1, y2, z1, z2;

		if (side.getAxis() == Direction.Axis.X) {
			x1 = x2 = side.getStepX() > 0 ? p2 : p1;
		} else {
			x1 = p1;
			x2 = p2;
		}

		if (side.getAxis() == Direction.Axis.Y) {
			y1 = y2 = side.getStepY() > 0 ? p2 : p1;
		} else {
			y1 = p1;
			y2 = p2;
		}

		if (side.getAxis() == Direction.Axis.Z) {
			z1 = z2 = side.getStepZ() > 0 ? p2 : p1;
		} else {
			z1 = p1;
			z2 = p2;
		}

		models().getBuilder("block/" + SPLITTER.id().getPath() + "/aperture_" + side + "_" + apertureLevel)
				.renderType("cutout")
				.texture("texture", SPLITTER.id().withPath(p -> "block/" + p + "_aperture_" + apertureLevel))
				.element()
				.from(x1, y1, z1)
				.to(x2, y2, z2)
				.face(side).texture("#texture").end()
				.face(side.getOpposite()).texture("#texture").end();
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

	private ModelFile defaultModel(ResourceLocation id) {
		return new ModelFile.UncheckedModelFile(id.withPrefix("block/"));
	}
}
