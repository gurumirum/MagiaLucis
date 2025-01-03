package gurumirum.gemthing.datagen;

import gurumirum.gemthing.contents.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RecipeGen extends RecipeProvider {
	public RecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void buildRecipes(@NotNull RecipeOutput out, HolderLookup.@NotNull Provider provider) {
		for (Ore ore : Ore.values()) {
			ItemLike smeltResult = switch (ore) {
				case SILVER -> ModItems.SILVER_INGOT;
				case AMBER -> GemItems.AMBER;
				case CITRINE -> GemItems.CITRINE;
				case AQUAMARINE -> GemItems.AQUAMARINE;
				case RUBY -> GemItems.RUBY;
				case SAPPHIRE -> GemItems.SAPPHIRE;
				case TOPAZ -> GemItems.TOPAZ;
			};
			String group = ore == Ore.SILVER ? "silver_ingot" : ore.oreBaseName();

			oreSmelting(out, ingredients(ore), RecipeCategory.MISC, smeltResult, 1, 200, group);
			oreBlasting(out, ingredients(ore), RecipeCategory.MISC, smeltResult, 1, 100, group);
		}

		nineBlockStorageRecipes(out, RecipeCategory.MISC, ModItems.SILVER_INGOT, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SILVER);
		nineBlockStorageRecipes(out, RecipeCategory.MISC, ModItems.RAW_SILVER, RecipeCategory.BUILDING_BLOCKS, ModBlocks.RAW_SILVER_BLOCK);
		nineBlockStorageRecipesWithCustomPacking(out, RecipeCategory.MISC, ModItems.SILVER_NUGGET, RecipeCategory.MISC, ModItems.SILVER_INGOT, "silver_ingot_from_nuggets", "silver_ingot");
	}

	private List<ItemLike> ingredients(Ore ore) {
		List<ItemLike> list = new ArrayList<>();
		if (ore == Ore.SILVER) list.add(ModItems.RAW_SILVER);

		ore.allOreItems().forEach(list::add);

		return list;
	}
}
