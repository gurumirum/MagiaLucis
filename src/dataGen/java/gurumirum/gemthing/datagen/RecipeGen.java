package gurumirum.gemthing.datagen;

import gurumirum.gemthing.contents.ModBlocks;
import gurumirum.gemthing.contents.ModItems;
import gurumirum.gemthing.contents.NormalOres;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RecipeGen extends RecipeProvider {
	public RecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void buildRecipes(@NotNull RecipeOutput out, HolderLookup.@NotNull Provider provider) {
		List<ItemLike> silverIngredient = List.of(ModItems.RAW_SILVER, NormalOres.SILVER.ore(), NormalOres.SILVER.deepslateOre());
		oreSmelting(out, silverIngredient, RecipeCategory.MISC, ModItems.SILVER_INGOT, 1, 200, "silver_ingot");
		oreBlasting(out, silverIngredient, RecipeCategory.MISC, ModItems.SILVER_INGOT, 1, 100, "silver_ingot");
		nineBlockStorageRecipes(out, RecipeCategory.MISC, ModItems.SILVER_INGOT, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SILVER);
		nineBlockStorageRecipes(out, RecipeCategory.MISC, ModItems.RAW_SILVER, RecipeCategory.BUILDING_BLOCKS, ModBlocks.RAW_SILVER_BLOCK);
		nineBlockStorageRecipesWithCustomPacking(out, RecipeCategory.MISC, ModItems.SILVER_NUGGET, RecipeCategory.MISC, ModItems.SILVER_INGOT, "silver_ingot_from_nuggets", "silver_ingot");
	}
}
