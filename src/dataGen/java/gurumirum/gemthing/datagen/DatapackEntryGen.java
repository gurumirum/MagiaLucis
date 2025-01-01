package gurumirum.gemthing.datagen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;

import java.util.List;

import static gurumirum.gemthing.GemthingMod.id;
import static gurumirum.gemthing.contents.NormalOres.SILVER;
import static net.minecraft.core.registries.Registries.CONFIGURED_FEATURE;
import static net.minecraft.core.registries.Registries.PLACED_FEATURE;
import static net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.target;
import static net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.BIOME_MODIFIERS;

public final class DatapackEntryGen {
	private DatapackEntryGen() {}

	private static final ResourceKey<ConfiguredFeature<?, ?>> SILVER_ORE = ResourceKey.create(CONFIGURED_FEATURE, id("silver_ore"));
	private static final ResourceKey<ConfiguredFeature<?, ?>> SILVER_ORE_BURIED = ResourceKey.create(CONFIGURED_FEATURE, id("silver_ore_buried"));

	private static final ResourceKey<PlacedFeature> ORE_SILVER_EXTRA = ResourceKey.create(PLACED_FEATURE, id("ore_silver_extra"));
	private static final ResourceKey<PlacedFeature> ORE_SILVER = ResourceKey.create(PLACED_FEATURE, id("ore_silver"));
	private static final ResourceKey<PlacedFeature> ORE_SILVER_LOWER = ResourceKey.create(PLACED_FEATURE, id("ore_silver_lower"));

	public static RegistrySetBuilder getEntries() {
		return new RegistrySetBuilder()
				.add(CONFIGURED_FEATURE, ctx -> {
					RuleTest stone = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
					RuleTest deepslate = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

					List<TargetBlockState> silverOre = List.of(
							target(stone, SILVER.ore().defaultBlockState()),
							target(deepslate, SILVER.deepslateOre().defaultBlockState())
					);

					FeatureUtils.register(ctx, SILVER_ORE, Feature.ORE, new OreConfiguration(silverOre, 9));
					FeatureUtils.register(ctx, SILVER_ORE_BURIED, Feature.ORE, new OreConfiguration(silverOre, 9, .5f));
				})
				.add(PLACED_FEATURE, ctx -> {
					HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = ctx.lookup(CONFIGURED_FEATURE);

					PlacementUtils.register(
							ctx,
							ORE_SILVER_EXTRA,
							configuredFeatures.getOrThrow(SILVER_ORE),
							commonOrePlacement(50,
									HeightRangePlacement.uniform(VerticalAnchor.absolute(32),
											VerticalAnchor.absolute(256))));
					PlacementUtils.register(
							ctx,
							ORE_SILVER,
							configuredFeatures.getOrThrow(SILVER_ORE_BURIED),
							commonOrePlacement(4,
									HeightRangePlacement.triangle(VerticalAnchor.absolute(-64),
											VerticalAnchor.absolute(32))));
					PlacementUtils.register(
							ctx,
							ORE_SILVER_LOWER,
							configuredFeatures.getOrThrow(SILVER_ORE_BURIED),
							orePlacement(CountPlacement.of(UniformInt.of(0, 1)),
									HeightRangePlacement.uniform(VerticalAnchor.absolute(-64),
											VerticalAnchor.absolute(-48))));
				})
				.add(BIOME_MODIFIERS, ctx -> {
					HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
					HolderGetter<PlacedFeature> placedFeatures = ctx.lookup(PLACED_FEATURE);

					ctx.register(ResourceKey.create(BIOME_MODIFIERS, id(SILVER.oreId())),
							new AddFeaturesBiomeModifier(
									biomes.get(BiomeTags.IS_OVERWORLD).orElseThrow(),
									HolderSet.direct(
											placedFeatures.getOrThrow(ORE_SILVER_EXTRA),
											placedFeatures.getOrThrow(ORE_SILVER),
											placedFeatures.getOrThrow(ORE_SILVER_LOWER)
									),
									GenerationStep.Decoration.UNDERGROUND_ORES));
				});
	}

	private static List<PlacementModifier> orePlacement(PlacementModifier pCountPlacement, PlacementModifier pHeightRange) {
		return List.of(pCountPlacement, InSquarePlacement.spread(), pHeightRange, BiomeFilter.biome());
	}

	private static List<PlacementModifier> commonOrePlacement(int pCount, PlacementModifier pHeightRange) {
		return orePlacement(CountPlacement.of(pCount), pHeightRange);
	}
}
