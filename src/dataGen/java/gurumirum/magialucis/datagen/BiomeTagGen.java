package gurumirum.magialucis.datagen;

import gurumirum.magialucis.MagiaLucisMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BiomeTagGen extends BiomeTagsProvider {
	public BiomeTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, provider, MagiaLucisMod.MODID, existingFileHelper);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider) {
		tag(DatapackEntryGen.HAS_TEMPLE).addTags(
				BiomeTags.IS_MOUNTAIN,
				BiomeTags.IS_BADLANDS,
				BiomeTags.IS_HILL,
				BiomeTags.IS_TAIGA,
				BiomeTags.IS_FOREST
		).add(
				Biomes.STONY_SHORE,
				Biomes.MUSHROOM_FIELDS,
				Biomes.DESERT,
				Biomes.SAVANNA,
				Biomes.SNOWY_PLAINS,
				Biomes.PLAINS,
				Biomes.SUNFLOWER_PLAINS,
				Biomes.SAVANNA_PLATEAU
		);
	}
}
