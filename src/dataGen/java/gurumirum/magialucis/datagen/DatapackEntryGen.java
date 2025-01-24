package gurumirum.magialucis.datagen;

import gurumirum.magialucis.contents.ModDamageTypes;
import gurumirum.magialucis.contents.Ore;
import gurumirum.magialucis.contents.structure.TempleStructure;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;

import java.util.List;
import java.util.function.Consumer;

import static gurumirum.magialucis.MagiaLucisMod.id;
import static gurumirum.magialucis.contents.Ore.*;
import static net.minecraft.core.registries.Registries.*;
import static net.minecraft.world.level.levelgen.VerticalAnchor.aboveBottom;
import static net.minecraft.world.level.levelgen.VerticalAnchor.absolute;
import static net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.target;
import static net.minecraft.world.level.levelgen.placement.HeightRangePlacement.triangle;
import static net.minecraft.world.level.levelgen.placement.HeightRangePlacement.uniform;
import static net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.BIOME_MODIFIERS;

public final class DatapackEntryGen {
	private DatapackEntryGen() {}

	public static final ResourceKey<ConfiguredFeature<?, ?>> SILVER_ORE = ResourceKey.create(CONFIGURED_FEATURE, id("silver_ore"));
	public static final ResourceKey<ConfiguredFeature<?, ?>> SILVER_ORE_BURIED = ResourceKey.create(CONFIGURED_FEATURE, id("silver_ore_buried"));

	public static final ResourceKey<ConfiguredFeature<?, ?>> AMBER_ORE = ResourceKey.create(CONFIGURED_FEATURE, id("amber_ore"));
	public static final ResourceKey<ConfiguredFeature<?, ?>> AMBER_ORE_BURIED = ResourceKey.create(CONFIGURED_FEATURE, id("amber_ore_buried"));

	public static final ResourceKey<ConfiguredFeature<?, ?>> CITRINE_ORE = ResourceKey.create(CONFIGURED_FEATURE, id("citrine_ore"));
	public static final ResourceKey<ConfiguredFeature<?, ?>> CITRINE_ORE_BURIED = ResourceKey.create(CONFIGURED_FEATURE, id("citrine_ore_buried"));

	public static final ResourceKey<ConfiguredFeature<?, ?>> CORDIERITE_ORE = ResourceKey.create(CONFIGURED_FEATURE, id("cordierite_ore"));
	public static final ResourceKey<ConfiguredFeature<?, ?>> CORDIERITE_ORE_BURIED = ResourceKey.create(CONFIGURED_FEATURE, id("cordierite_ore_buried"));

	public static final ResourceKey<ConfiguredFeature<?, ?>> AQUAMARINE_ORE = ResourceKey.create(CONFIGURED_FEATURE, id("aquamarine_ore"));
	public static final ResourceKey<ConfiguredFeature<?, ?>> AQUAMARINE_ORE_BURIED = ResourceKey.create(CONFIGURED_FEATURE, id("aquamarine_ore_buried"));

	public static final ResourceKey<ConfiguredFeature<?, ?>> RUBY_ORE_SMALL = ResourceKey.create(CONFIGURED_FEATURE, id("ruby_ore_small"));
	public static final ResourceKey<ConfiguredFeature<?, ?>> RUBY_ORE_MEDIUM = ResourceKey.create(CONFIGURED_FEATURE, id("ruby_ore_medium"));
	public static final ResourceKey<ConfiguredFeature<?, ?>> RUBY_ORE_LARGE = ResourceKey.create(CONFIGURED_FEATURE, id("ruby_ore_large"));
	public static final ResourceKey<ConfiguredFeature<?, ?>> RUBY_ORE_BURIED = ResourceKey.create(CONFIGURED_FEATURE, id("ruby_ore_buried"));

	public static final ResourceKey<ConfiguredFeature<?, ?>> SAPPHIRE_ORE_SMALL = ResourceKey.create(CONFIGURED_FEATURE, id("sapphire_ore_small"));
	public static final ResourceKey<ConfiguredFeature<?, ?>> SAPPHIRE_ORE_MEDIUM = ResourceKey.create(CONFIGURED_FEATURE, id("sapphire_ore_medium"));
	public static final ResourceKey<ConfiguredFeature<?, ?>> SAPPHIRE_ORE_LARGE = ResourceKey.create(CONFIGURED_FEATURE, id("sapphire_ore_large"));
	public static final ResourceKey<ConfiguredFeature<?, ?>> SAPPHIRE_ORE_BURIED = ResourceKey.create(CONFIGURED_FEATURE, id("sapphire_ore_buried"));

	public static final ResourceKey<ConfiguredFeature<?, ?>> TOPAZ_ORE_SMALL = ResourceKey.create(CONFIGURED_FEATURE, id("topaz_ore_small"));
	public static final ResourceKey<ConfiguredFeature<?, ?>> TOPAZ_ORE_MEDIUM = ResourceKey.create(CONFIGURED_FEATURE, id("topaz_ore_medium"));
	public static final ResourceKey<ConfiguredFeature<?, ?>> TOPAZ_ORE_LARGE = ResourceKey.create(CONFIGURED_FEATURE, id("topaz_ore_large"));
	public static final ResourceKey<ConfiguredFeature<?, ?>> TOPAZ_ORE_BURIED = ResourceKey.create(CONFIGURED_FEATURE, id("topaz_ore_buried"));

	public static final ResourceKey<PlacedFeature> ORE_SILVER_EXTRA = ResourceKey.create(PLACED_FEATURE, id("ore_silver_extra"));
	public static final ResourceKey<PlacedFeature> ORE_SILVER = ResourceKey.create(PLACED_FEATURE, id("ore_silver"));
	public static final ResourceKey<PlacedFeature> ORE_SILVER_LOWER = ResourceKey.create(PLACED_FEATURE, id("ore_silver_lower"));

	public static final ResourceKey<PlacedFeature> ORE_AMBER = ResourceKey.create(PLACED_FEATURE, id("ore_amber"));
	public static final ResourceKey<PlacedFeature> ORE_AMBER_BURIED = ResourceKey.create(PLACED_FEATURE, id("ore_amber_buried"));

	public static final ResourceKey<PlacedFeature> ORE_CITRINE = ResourceKey.create(PLACED_FEATURE, id("ore_citrine"));
	public static final ResourceKey<PlacedFeature> ORE_CITRINE_BURIED = ResourceKey.create(PLACED_FEATURE, id("ore_citrine_buried"));

	public static final ResourceKey<PlacedFeature> ORE_CORDIERITE = ResourceKey.create(PLACED_FEATURE, id("ore_cordierite"));
	public static final ResourceKey<PlacedFeature> ORE_CORDIERITE_BURIED = ResourceKey.create(PLACED_FEATURE, id("ore_cordierite_buried"));

	public static final ResourceKey<PlacedFeature> ORE_AQUAMARINE = ResourceKey.create(PLACED_FEATURE, id("ore_aquamarine"));
	public static final ResourceKey<PlacedFeature> ORE_AQUAMARINE_BURIED = ResourceKey.create(PLACED_FEATURE, id("ore_aquamarine_buried"));
	public static final ResourceKey<PlacedFeature> ORE_AQUAMARINE_OCEAN = ResourceKey.create(PLACED_FEATURE, id("ore_aquamarine_ocean"));

	public static final ResourceKey<PlacedFeature> ORE_RUBY = ResourceKey.create(PLACED_FEATURE, id("ore_ruby"));
	public static final ResourceKey<PlacedFeature> ORE_RUBY_MEDIUM = ResourceKey.create(PLACED_FEATURE, id("ore_ruby_medium"));
	public static final ResourceKey<PlacedFeature> ORE_RUBY_LARGE = ResourceKey.create(PLACED_FEATURE, id("ore_ruby_large"));
	public static final ResourceKey<PlacedFeature> ORE_RUBY_BURIED = ResourceKey.create(PLACED_FEATURE, id("ore_ruby_buried"));

	public static final ResourceKey<PlacedFeature> ORE_SAPPHIRE = ResourceKey.create(PLACED_FEATURE, id("ore_sapphire"));
	public static final ResourceKey<PlacedFeature> ORE_SAPPHIRE_MEDIUM = ResourceKey.create(PLACED_FEATURE, id("ore_sapphire_medium"));
	public static final ResourceKey<PlacedFeature> ORE_SAPPHIRE_LARGE = ResourceKey.create(PLACED_FEATURE, id("ore_sapphire_large"));
	public static final ResourceKey<PlacedFeature> ORE_SAPPHIRE_BURIED = ResourceKey.create(PLACED_FEATURE, id("ore_sapphire_buried"));

	public static final ResourceKey<PlacedFeature> ORE_TOPAZ = ResourceKey.create(PLACED_FEATURE, id("ore_topaz"));
	public static final ResourceKey<PlacedFeature> ORE_TOPAZ_MEDIUM = ResourceKey.create(PLACED_FEATURE, id("ore_topaz_medium"));
	public static final ResourceKey<PlacedFeature> ORE_TOPAZ_LARGE = ResourceKey.create(PLACED_FEATURE, id("ore_topaz_large"));
	public static final ResourceKey<PlacedFeature> ORE_TOPAZ_BURIED = ResourceKey.create(PLACED_FEATURE, id("ore_topaz_buried"));

	public static final ResourceKey<Structure> TEMPLE_STRUCTURE = ResourceKey.create(STRUCTURE, id("temple"));
	public static final ResourceKey<StructureSet> TEMPLE_STRUCTURE_SET = ResourceKey.create(STRUCTURE_SET, id("temple"));

	public static final TagKey<Biome> HAS_TEMPLE = TagKey.create(Registries.BIOME, id("has_temple"));

	public static RegistrySetBuilder getEntries() {
		return new RegistrySetBuilder()
				.add(CONFIGURED_FEATURE, ctx -> {
					cfg(SILVER, l -> {
						FeatureUtils.register(ctx, SILVER_ORE, Feature.ORE, new OreConfiguration(l, 9));
						FeatureUtils.register(ctx, SILVER_ORE_BURIED, Feature.ORE, new OreConfiguration(l, 9, .5f));
					});

					cfg(AMBER, l -> {
						FeatureUtils.register(ctx, AMBER_ORE, Feature.ORE, new OreConfiguration(l, 7));
						FeatureUtils.register(ctx, AMBER_ORE_BURIED, Feature.ORE, new OreConfiguration(l, 7, 1.0F));
					});

					cfg(CITRINE, l -> {
						FeatureUtils.register(ctx, CITRINE_ORE, Feature.ORE, new OreConfiguration(l, 7));
						FeatureUtils.register(ctx, CITRINE_ORE_BURIED, Feature.ORE, new OreConfiguration(l, 7, 1.0F));
					});

					cfg(CORDIERITE, l -> {
						FeatureUtils.register(ctx, CORDIERITE_ORE, Feature.ORE, new OreConfiguration(l, 7));
						FeatureUtils.register(ctx, CORDIERITE_ORE_BURIED, Feature.ORE, new OreConfiguration(l, 7, 1.0F));
					});

					cfg(AQUAMARINE, l -> {
						FeatureUtils.register(ctx, AQUAMARINE_ORE, Feature.ORE, new OreConfiguration(l, 7));
						FeatureUtils.register(ctx, AQUAMARINE_ORE_BURIED, Feature.ORE, new OreConfiguration(l, 7, 1.0F));
					});

					cfg(RUBY, l -> {
						FeatureUtils.register(ctx, RUBY_ORE_SMALL, Feature.ORE, new OreConfiguration(l, 4, 0.5F));
						FeatureUtils.register(ctx, RUBY_ORE_MEDIUM, Feature.ORE, new OreConfiguration(l, 12, 0.7F));
						FeatureUtils.register(ctx, RUBY_ORE_LARGE, Feature.ORE, new OreConfiguration(l, 8, 1.0F));
						FeatureUtils.register(ctx, RUBY_ORE_BURIED, Feature.ORE, new OreConfiguration(l, 8, 0.5F));
					});

					cfg(SAPPHIRE, l -> {
						FeatureUtils.register(ctx, SAPPHIRE_ORE_SMALL, Feature.ORE, new OreConfiguration(l, 4, 0.5F));
						FeatureUtils.register(ctx, SAPPHIRE_ORE_MEDIUM, Feature.ORE, new OreConfiguration(l, 12, 0.7F));
						FeatureUtils.register(ctx, SAPPHIRE_ORE_LARGE, Feature.ORE, new OreConfiguration(l, 8, 1.0F));
						FeatureUtils.register(ctx, SAPPHIRE_ORE_BURIED, Feature.ORE, new OreConfiguration(l, 8, 0.5F));
					});

					cfg(TOPAZ, l -> {
						FeatureUtils.register(ctx, TOPAZ_ORE_SMALL, Feature.ORE, new OreConfiguration(l, 4, 0.5F));
						FeatureUtils.register(ctx, TOPAZ_ORE_MEDIUM, Feature.ORE, new OreConfiguration(l, 12, 0.7F));
						FeatureUtils.register(ctx, TOPAZ_ORE_LARGE, Feature.ORE, new OreConfiguration(l, 8, 1.0F));
						FeatureUtils.register(ctx, TOPAZ_ORE_BURIED, Feature.ORE, new OreConfiguration(l, 8, 0.5F));
					});
				})
				.add(PLACED_FEATURE, ctx -> {
					HolderGetter<ConfiguredFeature<?, ?>> cf = ctx.lookup(CONFIGURED_FEATURE);

					PlacementUtils.register(ctx, ORE_SILVER_EXTRA, cf.getOrThrow(SILVER_ORE),
							commonOrePlacement(75, uniform(absolute(32), absolute(256))));
					PlacementUtils.register(ctx, ORE_SILVER, cf.getOrThrow(SILVER_ORE_BURIED),
							commonOrePlacement(6, triangle(absolute(-64), absolute(32))));
					PlacementUtils.register(ctx, ORE_SILVER_LOWER, cf.getOrThrow(SILVER_ORE_BURIED),
							orePlacement(CountPlacement.of(UniformInt.of(0, 2)), uniform(absolute(-64), absolute(-48))));

					PlacementUtils.register(ctx, ORE_AMBER, cf.getOrThrow(AMBER_ORE),
							commonOrePlacement(6, triangle(absolute(0), absolute(100))));
					PlacementUtils.register(ctx, ORE_AMBER_BURIED, cf.getOrThrow(AMBER_ORE_BURIED),
							commonOrePlacement(10, uniform(absolute(-16), absolute(112))));

					PlacementUtils.register(ctx, ORE_CITRINE, cf.getOrThrow(CITRINE_ORE),
							commonOrePlacement(4, triangle(absolute(0), absolute(80))));
					PlacementUtils.register(ctx, ORE_CITRINE_BURIED, cf.getOrThrow(CITRINE_ORE_BURIED),
							commonOrePlacement(6, uniform(absolute(-16), absolute(64))));

					PlacementUtils.register(ctx, ORE_CORDIERITE, cf.getOrThrow(CORDIERITE_ORE),
							commonOrePlacement(4, triangle(absolute(0), absolute(80))));
					PlacementUtils.register(ctx, ORE_CORDIERITE_BURIED, cf.getOrThrow(CORDIERITE_ORE_BURIED),
							commonOrePlacement(6, uniform(absolute(-16), absolute(64))));

					PlacementUtils.register(ctx, ORE_AQUAMARINE, cf.getOrThrow(AQUAMARINE_ORE),
							commonOrePlacement(4, triangle(absolute(0), absolute(80))));
					PlacementUtils.register(ctx, ORE_AQUAMARINE_BURIED, cf.getOrThrow(AQUAMARINE_ORE_BURIED),
							commonOrePlacement(6, uniform(absolute(-16), absolute(64))));
					PlacementUtils.register(ctx, ORE_AQUAMARINE_OCEAN, cf.getOrThrow(AQUAMARINE_ORE),
							commonOrePlacement(4, triangle(absolute(0), absolute(80))));

					PlacementUtils.register(ctx, ORE_RUBY, cf.getOrThrow(RUBY_ORE_SMALL),
							commonOrePlacement(7, triangle(aboveBottom(-80), aboveBottom(80))));
					PlacementUtils.register(ctx, ORE_RUBY_MEDIUM, cf.getOrThrow(RUBY_ORE_MEDIUM),
							commonOrePlacement(2, uniform(absolute(-64), absolute(-4))));
					PlacementUtils.register(ctx, ORE_RUBY_LARGE, cf.getOrThrow(RUBY_ORE_LARGE),
							rareOrePlacement(9, triangle(aboveBottom(-80), aboveBottom(80))));
					PlacementUtils.register(ctx, ORE_RUBY_BURIED, cf.getOrThrow(RUBY_ORE_BURIED),
							commonOrePlacement(4, triangle(aboveBottom(-80), aboveBottom(80))));

					PlacementUtils.register(ctx, ORE_SAPPHIRE, cf.getOrThrow(SAPPHIRE_ORE_SMALL),
							commonOrePlacement(7, triangle(aboveBottom(-80), aboveBottom(80))));
					PlacementUtils.register(ctx, ORE_SAPPHIRE_MEDIUM, cf.getOrThrow(SAPPHIRE_ORE_MEDIUM),
							commonOrePlacement(2, uniform(absolute(-64), absolute(-4))));
					PlacementUtils.register(ctx, ORE_SAPPHIRE_LARGE, cf.getOrThrow(SAPPHIRE_ORE_LARGE),
							rareOrePlacement(9, triangle(aboveBottom(-80), aboveBottom(80))));
					PlacementUtils.register(ctx, ORE_SAPPHIRE_BURIED, cf.getOrThrow(SAPPHIRE_ORE_BURIED),
							commonOrePlacement(4, triangle(aboveBottom(-80), aboveBottom(80))));

					PlacementUtils.register(ctx, ORE_TOPAZ, cf.getOrThrow(TOPAZ_ORE_SMALL),
							commonOrePlacement(7, triangle(aboveBottom(-80), aboveBottom(80))));
					PlacementUtils.register(ctx, ORE_TOPAZ_MEDIUM, cf.getOrThrow(TOPAZ_ORE_MEDIUM),
							commonOrePlacement(2, uniform(absolute(-64), absolute(-4))));
					PlacementUtils.register(ctx, ORE_TOPAZ_LARGE, cf.getOrThrow(TOPAZ_ORE_LARGE),
							rareOrePlacement(9, triangle(aboveBottom(-80), aboveBottom(80))));
					PlacementUtils.register(ctx, ORE_TOPAZ_BURIED, cf.getOrThrow(TOPAZ_ORE_BURIED),
							commonOrePlacement(4, triangle(aboveBottom(-80), aboveBottom(80))));
				})
				.add(BIOME_MODIFIERS, ctx -> {
					HolderGetter<Biome> biomes = ctx.lookup(BIOME);
					HolderGetter<PlacedFeature> placedFeatures = ctx.lookup(PLACED_FEATURE);

					ctx.register(ResourceKey.create(BIOME_MODIFIERS, id("fucking_ores")),
							new AddFeaturesBiomeModifier(
									biomes.get(BiomeTags.IS_OVERWORLD).orElseThrow(),
									HolderSet.direct(
											placedFeatures.getOrThrow(ORE_SILVER_EXTRA),
											placedFeatures.getOrThrow(ORE_SILVER),
											placedFeatures.getOrThrow(ORE_SILVER_LOWER),
											placedFeatures.getOrThrow(ORE_AMBER),
											placedFeatures.getOrThrow(ORE_AMBER_BURIED),
											placedFeatures.getOrThrow(ORE_CITRINE),
											placedFeatures.getOrThrow(ORE_CITRINE_BURIED),
											placedFeatures.getOrThrow(ORE_CORDIERITE),
											placedFeatures.getOrThrow(ORE_CORDIERITE_BURIED),
											placedFeatures.getOrThrow(ORE_AQUAMARINE),
											placedFeatures.getOrThrow(ORE_AQUAMARINE_BURIED),
											placedFeatures.getOrThrow(ORE_RUBY),
											placedFeatures.getOrThrow(ORE_RUBY_MEDIUM),
											placedFeatures.getOrThrow(ORE_RUBY_LARGE),
											placedFeatures.getOrThrow(ORE_RUBY_BURIED),
											placedFeatures.getOrThrow(ORE_SAPPHIRE),
											placedFeatures.getOrThrow(ORE_SAPPHIRE_MEDIUM),
											placedFeatures.getOrThrow(ORE_SAPPHIRE_LARGE),
											placedFeatures.getOrThrow(ORE_SAPPHIRE_BURIED),
											placedFeatures.getOrThrow(ORE_TOPAZ),
											placedFeatures.getOrThrow(ORE_TOPAZ_MEDIUM),
											placedFeatures.getOrThrow(ORE_TOPAZ_LARGE),
											placedFeatures.getOrThrow(ORE_TOPAZ_BURIED)
									),
									GenerationStep.Decoration.UNDERGROUND_ORES));

					ctx.register(ResourceKey.create(BIOME_MODIFIERS, id("aquamarine_ocean")),
							new AddFeaturesBiomeModifier(
									biomes.get(BiomeTags.IS_OCEAN).orElseThrow(),
									HolderSet.direct(
											placedFeatures.getOrThrow(ORE_AQUAMARINE_OCEAN)
									),
									GenerationStep.Decoration.UNDERGROUND_ORES));
				})
				.add(DAMAGE_TYPE, ctx -> {
					damageType(ctx, ModDamageTypes.LESSER_ICE_PROJECTILE);
				})
				.add(STRUCTURE, ctx -> {
					HolderGetter<Biome> biomes = ctx.lookup(BIOME);

					ctx.register(TEMPLE_STRUCTURE, new TempleStructure(new Structure.StructureSettings(biomes.getOrThrow(HAS_TEMPLE))));
				})
				.add(STRUCTURE_SET, ctx -> {
					HolderGetter<Structure> structures = ctx.lookup(STRUCTURE);

					ctx.register(TEMPLE_STRUCTURE_SET, new StructureSet(
							structures.getOrThrow(TEMPLE_STRUCTURE),
							new RandomSpreadStructurePlacement(
									24,
									8,
									RandomSpreadType.LINEAR,
									68245825)));
				});
	}

	private static void cfg(Ore ore, Consumer<List<TargetBlockState>> consumer) {
		List<TargetBlockState> targetBlockStates = ore.entries().map(e -> target(switch (e.getKey()) {
			case STONE -> new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
			case DEEPSLATE -> new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
			case NETHER -> new BlockMatchTest(Blocks.NETHERRACK);
			case END -> new BlockMatchTest(Blocks.END_STONE); // TODO
		}, e.getValue().getFirst().get().defaultBlockState())).toList();
		consumer.accept(targetBlockStates);
	}

	private static List<PlacementModifier> orePlacement(PlacementModifier countPlacement, PlacementModifier heightRange) {
		return List.of(countPlacement, InSquarePlacement.spread(), heightRange, BiomeFilter.biome());
	}

	private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier heightRange) {
		return orePlacement(CountPlacement.of(count), heightRange);
	}

	private static List<PlacementModifier> rareOrePlacement(int chance, PlacementModifier heightRange) {
		return orePlacement(RarityFilter.onAverageOnceEvery(chance), heightRange);
	}

	private static void damageType(BootstrapContext<DamageType> context, ResourceKey<DamageType> key) {
		context.register(key, new DamageType(key.location().getPath(), 0.1f));
	}
}
