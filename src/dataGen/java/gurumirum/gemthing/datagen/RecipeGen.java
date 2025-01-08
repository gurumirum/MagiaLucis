package gurumirum.gemthing.datagen;

import gurumirum.gemthing.capability.GemStats;
import gurumirum.gemthing.contents.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
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

		wandRecipe(true, Wands.ANCIENT_LIGHT)
				.define('1', ItemTags.STONE_BRICKS)
				.define('2', ModItems.ANCIENT_CORE)
				.unlockedBy("has_ancient_core", has(ModItems.ANCIENT_CORE))
				.save(out);

		wandRecipe(true, Wands.CONFIGURATION_WAND)
				.define('1', ItemTags.STONE_BRICKS)
				.define('2', GemItems.BRIGHTSTONE)
				.unlockedBy("has_brightstone", has(GemItems.BRIGHTSTONE))
				.save(out);
		wandRecipe(true, Wands.RED_CONFIGURATION_WAND)
				.define('1', ItemTags.STONE_BRICKS)
				.define('2', GemItems.RED_BRIGHTSTONE)
				.unlockedBy("has_red_brightstone", has(GemItems.RED_BRIGHTSTONE))
				.save(out);
		wandRecipe(true, Wands.ICY_CONFIGURATION_WAND)
				.define('1', ItemTags.STONE_BRICKS)
				.define('2', GemItems.ICY_BRIGHTSTONE)
				.unlockedBy("has_icy_brightstone", has(GemItems.ICY_BRIGHTSTONE))
				.save(out);

		wandRecipe(true, Wands.AMBER_TORCH)
				.define('1', ItemTags.LOGS)
				.define('2', GemStats.AMBER.tag())
				.unlockedBy("has_amber", has(GemStats.AMBER.tag()))
				.save(out);

		wandRecipe(false, Wands.LESSER_ICE_STAFF)
				.define('1', ItemTags.LOGS)
				.define('2', GemItems.ICY_BRIGHTSTONE)
				.unlockedBy("has_icy_brightstone", has(GemItems.ICY_BRIGHTSTONE))
				.save(out);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Wands.RECALL_STAFF)
				.pattern(" 32")
				.pattern("413")
				.pattern("14 ")
				.define('1', Tags.Items.INGOTS_GOLD)
				.define('2', GemItems.AQUAMARINE)
				.define('3', Tags.Items.LEATHERS)
				.define('4', Tags.Items.DYES_RED)
				.unlockedBy("has_aquamarine", has(GemItems.AQUAMARINE))
				.save(out);

		wandRecipe(false, Wands.HEAL_WAND)
				.define('1', Tags.Items.INGOTS_GOLD)
				.define('2', GemItems.PEARL)
				.unlockedBy("has_pearl", has(GemItems.PEARL))
				.save(out);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RELAY)
				.pattern(" 1 ")
				.pattern("1 1")
				.pattern("232")
				.define('1', ModItemTags.BRIGHTSTONES)
				.define('2', Tags.Items.NUGGETS_GOLD)
				.define('3', ItemTags.WOODEN_SLABS)
				.unlockedBy("has_brightstones", has(ModItemTags.BRIGHTSTONES))
				.save(out);
	}

	private ShapedRecipeBuilder wandRecipe(boolean tool, ItemLike result) {
		return wandRecipe(tool ? RecipeCategory.TOOLS : RecipeCategory.COMBAT, result);
	}

	private ShapedRecipeBuilder wandRecipe(RecipeCategory category, ItemLike result) {
		return ShapedRecipeBuilder.shaped(category, result)
				.pattern(" 12")
				.pattern(" 11")
				.pattern("1  ");
	}

	private List<ItemLike> ingredients(Ore ore) {
		List<ItemLike> list = new ArrayList<>();
		if (ore == Ore.SILVER) list.add(ModItems.RAW_SILVER);

		ore.allOreItems().forEach(list::add);

		return list;
	}
}
