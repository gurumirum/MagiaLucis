package gurumirum.magialucis.datagen.recipe;

import gurumirum.magialucis.contents.ModItemTags;
import gurumirum.magialucis.contents.Ore;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import static gurumirum.magialucis.MagiaLucisMod.id;
import static gurumirum.magialucis.contents.ModBuildingBlocks.*;
import static gurumirum.magialucis.contents.ModItems.*;
import static net.minecraft.data.recipes.RecipeCategory.BUILDING_BLOCKS;
import static net.minecraft.data.recipes.RecipeCategory.MISC;
import static net.minecraft.data.recipes.ShapelessRecipeBuilder.shapeless;

public abstract class MaterialRecipes extends SharedRecipeLogic {
	public static void add(@NotNull RecipeOutput out) {
		for (Ore ore : Ore.values()) {
			ItemLike smeltResult = ore.smeltItem();
			String group = ore == Ore.SILVER ? "silver_ingot" : ore.oreBaseName();

			oreSmelting(out, ingredients(ore), MISC, smeltResult, 1, 200, group);
			oreBlasting(out, ingredients(ore), MISC, smeltResult, 1, 100, group);
		}

		nineBlockStorageRecipes(out, MISC, SILVER_INGOT, BUILDING_BLOCKS, SILVER_BLOCK);
		nineBlockStorageRecipes(out, MISC, RAW_SILVER, BUILDING_BLOCKS, RAW_SILVER_BLOCK);
		nineBlockStorageRecipes(out, MISC, ELECTRUM_INGOT, BUILDING_BLOCKS, ELECTRUM_BLOCK);
		nineBlockStorageRecipes(out, MISC, ROSE_GOLD_INGOT, BUILDING_BLOCKS, ROSE_GOLD_BLOCK);
		nineBlockStorageRecipes(out, MISC, STERLING_SILVER_INGOT, BUILDING_BLOCKS, STERLING_SILVER_BLOCK);
		nineBlockStorageRecipes(out, MISC, LUMINOUS_ALLOY_INGOT, BUILDING_BLOCKS, LUMINOUS_ALLOY_BLOCK);

		nineBlockStorageRecipesWithCustomPacking(out, MISC, COPPER_NUGGET,
				MISC, Items.COPPER_INGOT, "copper_ingot_from_nuggets", "copper_ingot");
		nineBlockStorageRecipesWithCustomPacking(out, MISC, SILVER_NUGGET,
				MISC, SILVER_INGOT, "silver_ingot_from_nuggets", "silver_ingot");
		nineBlockStorageRecipesWithCustomPacking(out, MISC, ELECTRUM_NUGGET,
				MISC, ELECTRUM_INGOT, "electrum_ingot_from_nuggets", "electrum_ingot");
		nineBlockStorageRecipesWithCustomPacking(out, MISC, ROSE_GOLD_NUGGET,
				MISC, ROSE_GOLD_INGOT, "rose_gold_ingot_from_nuggets", "rose_gold_ingot");
		nineBlockStorageRecipesWithCustomPacking(out, MISC, STERLING_SILVER_NUGGET,
				MISC, STERLING_SILVER_INGOT, "sterling_silver_ingot_from_nuggets", "sterling_silver_ingot");
		nineBlockStorageRecipesWithCustomPacking(out, MISC, LUMINOUS_ALLOY_NUGGET,
				MISC, LUMINOUS_ALLOY_INGOT, "luminous_alloy_ingot_from_nuggets", "luminous_alloy");

		shapeless(MISC, ELECTRUM_NUGGET, 2)
				.requires(Ingredient.of(Tags.Items.NUGGETS_GOLD), 2)
				.requires(ModItemTags.SILVER_NUGGETS)
				.unlockedBy("has_gold", has(Tags.Items.INGOTS_GOLD))
				.save(out, id("electrum_hand_alloying"));

		shapeless(MISC, ROSE_GOLD_NUGGET, 2)
				.requires(Ingredient.of(Tags.Items.NUGGETS_GOLD), 2)
				.requires(ModItemTags.COPPER_NUGGETS)
				.unlockedBy("has_gold", has(Tags.Items.INGOTS_GOLD))
				.save(out, id("rose_gold_hand_alloying"));

		shapeless(MISC, STERLING_SILVER_NUGGET, 2)
				.requires(Ingredient.of(ModItemTags.SILVER_NUGGETS), 2)
				.requires(ModItemTags.COPPER_NUGGETS)
				.unlockedBy("has_silver", has(ModItemTags.SILVER_INGOTS))
				.save(out, id("sterling_silver_hand_alloying"));
	}
}
