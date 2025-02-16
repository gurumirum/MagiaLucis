package gurumirum.magialucis.datagen;

import gurumirum.magialucis.contents.ModDamageTypes;
import gurumirum.magialucis.contents.structure.TempleStructure;
import gurumirum.magialucis.datagen.data.OreGen;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

import static gurumirum.magialucis.api.MagiaLucisApi.id;
import static net.minecraft.core.registries.Registries.*;
import static net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.BIOME_MODIFIERS;

public final class DatapackEntryGen {
	private DatapackEntryGen() {}

	public static final ResourceKey<Structure> TEMPLE_STRUCTURE = ResourceKey.create(STRUCTURE, id("temple"));
	public static final ResourceKey<StructureSet> TEMPLE_STRUCTURE_SET = ResourceKey.create(STRUCTURE_SET, id("temple"));

	public static final TagKey<Biome> HAS_TEMPLE = TagKey.create(Registries.BIOME, id("has_temple"));

	public static RegistrySetBuilder getEntries() {
		return new RegistrySetBuilder()
				.add(CONFIGURED_FEATURE, OreGen::addConfigureFeature)
				.add(PLACED_FEATURE, OreGen::addPlacedFeature)
				.add(BIOME_MODIFIERS, OreGen::addBiomeModifier)
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

	private static void damageType(BootstrapContext<DamageType> context, ResourceKey<DamageType> key) {
		context.register(key, new DamageType(key.location().getPath(), 0.1f));
	}
}
