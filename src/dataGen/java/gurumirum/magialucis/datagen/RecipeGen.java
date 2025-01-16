package gurumirum.magialucis.datagen;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.contents.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static gurumirum.magialucis.contents.ModBuildingBlocks.*;
import static gurumirum.magialucis.contents.ModItems.*;
import static gurumirum.magialucis.contents.Wands.*;
import static net.minecraft.data.recipes.RecipeCategory.*;
import static net.minecraft.data.recipes.ShapedRecipeBuilder.shaped;
import static net.minecraft.data.recipes.ShapelessRecipeBuilder.shapeless;

public class RecipeGen extends RecipeProvider {
	public RecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void buildRecipes(@NotNull RecipeOutput out, HolderLookup.@NotNull Provider provider) {
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

		shapeless(MISC, ELECTRUM_NUGGET, 2)
				.requires(Ingredient.of(Tags.Items.NUGGETS_GOLD), 2)
				.requires(ModItemTags.SILVER_NUGGETS)
				.unlockedBy("has_gold", has(Tags.Items.INGOTS_GOLD))
				.save(out, "electrum_hand_alloying");

		shapeless(MISC, ROSE_GOLD_NUGGET, 2)
				.requires(Ingredient.of(Tags.Items.NUGGETS_GOLD), 2)
				.requires(ModItemTags.COPPER_NUGGETS)
				.unlockedBy("has_gold", has(Tags.Items.INGOTS_GOLD))
				.save(out, "rose_gold_hand_alloying");

		shapeless(MISC, STERLING_SILVER_NUGGET, 2)
				.requires(Ingredient.of(ModItemTags.SILVER_NUGGETS), 2)
				.requires(ModItemTags.COPPER_NUGGETS)
				.unlockedBy("has_silver", has(ModItemTags.SILVER_INGOTS))
				.save(out, "sterling_silver_hand_alloying");

		wandRecipe(true, ANCIENT_LIGHT)
				.define('1', ItemTags.STONE_BRICKS)
				.define('2', ANCIENT_CORE)
				.unlockedBy("has_ancient_core", has(ANCIENT_CORE))
				.save(out);

		wandRecipe(true, CONFIGURATION_WAND)
				.define('1', ItemTags.STONE_BRICKS)
				.define('2', GemItems.BRIGHTSTONE)
				.unlockedBy("has_brightstone", has(GemItems.BRIGHTSTONE))
				.save(out);
		wandRecipe(true, RED_CONFIGURATION_WAND)
				.define('1', ItemTags.STONE_BRICKS)
				.define('2', GemItems.RED_BRIGHTSTONE)
				.unlockedBy("has_red_brightstone", has(GemItems.RED_BRIGHTSTONE))
				.save(out);
		wandRecipe(true, ICY_CONFIGURATION_WAND)
				.define('1', ItemTags.STONE_BRICKS)
				.define('2', GemItems.ICY_BRIGHTSTONE)
				.unlockedBy("has_icy_brightstone", has(GemItems.ICY_BRIGHTSTONE))
				.save(out);

		wandRecipe(true, AMBER_TORCH)
				.define('1', ItemTags.LOGS)
				.define('2', GemStats.AMBER.tag())
				.unlockedBy("has_amber", has(GemStats.AMBER.tag()))
				.save(out);

		wandRecipe(false, LESSER_ICE_STAFF)
				.define('1', ItemTags.LOGS)
				.define('2', GemItems.ICY_BRIGHTSTONE)
				.unlockedBy("has_icy_brightstone", has(GemItems.ICY_BRIGHTSTONE))
				.save(out);

		shaped(TOOLS, RECALL_STAFF)
				.pattern(" 32")
				.pattern("413")
				.pattern("14 ")
				.define('1', Tags.Items.INGOTS_GOLD)
				.define('2', GemItems.AQUAMARINE)
				.define('3', Tags.Items.LEATHERS)
				.define('4', Tags.Items.DYES_RED)
				.unlockedBy("has_aquamarine", has(GemItems.AQUAMARINE))
				.save(out);

		wandRecipe(false, HEAL_WAND)
				.define('1', Tags.Items.INGOTS_GOLD)
				.define('2', GemItems.PEARL)
				.unlockedBy("has_pearl", has(GemItems.PEARL))
				.save(out);

		shaped(MISC, ModBlocks.RELAY)
				.pattern(" 1 ")
				.pattern("1 1")
				.pattern("232")
				.define('1', ModItemTags.BRIGHTSTONES)
				.define('2', Tags.Items.NUGGETS_GOLD)
				.define('3', ItemTags.WOODEN_SLABS)
				.unlockedBy("has_brightstones", has(ModItemTags.BRIGHTSTONES))
				.save(out);

		shaped(MISC, WAND_BELT)
				.pattern("122")
				.pattern("2 2")
				.pattern("222")
				.define('1', Tags.Items.NUGGETS_IRON)
				.define('2', Tags.Items.LEATHERS)
				.unlockedBy("has_wands", has(ModItemTags.WANDS))
				.save(out);

		slab(out, BUILDING_BLOCKS, LAPIS_MANALIS_SLAB, LAPIS_MANALIS);
		slab(out, BUILDING_BLOCKS, LAPIS_MANALIS_BRICK_SLAB, LAPIS_MANALIS_BRICKS);

		shaped(BUILDING_BLOCKS, LAPIS_MANALIS_BRICKS, 4)
				.pattern("##")
				.pattern("##")
				.define('#', LAPIS_MANALIS)
				.unlockedBy("has_lapis_manalis", has(LAPIS_MANALIS))
				.save(out);

		shaped(BUILDING_BLOCKS, LAPIS_MANALIS_PILLAR, 2)
				.pattern("#")
				.pattern("#")
				.define('#', LAPIS_MANALIS)
				.unlockedBy("has_lapis_manalis", has(LAPIS_MANALIS))
				.save(out);

		shaped(BUILDING_BLOCKS, LAPIS_MANALIS_PILLAR_ORNAMENT_DORIC, 2)
				.pattern("1")
				.pattern("2")
				.define('1', LAPIS_MANALIS)
				.define('2', LAPIS_MANALIS_PILLAR)
				.unlockedBy("has_lapis_manalis_pillar", has(LAPIS_MANALIS_PILLAR))
				.save(out);

		shaped(BUILDING_BLOCKS, LAPIS_MANALIS_PILLAR_ORNAMENT_IONIC, 4)
				.pattern("11")
				.pattern("22")
				.define('1', LAPIS_MANALIS)
				.define('2', LAPIS_MANALIS_PILLAR)
				.unlockedBy("has_lapis_manalis_pillar", has(LAPIS_MANALIS_PILLAR))
				.save(out);

		shaped(BUILDING_BLOCKS, LAPIS_MANALIS_PILLAR_ORNAMENT_CORINTHIAN, 4)
				.pattern("##")
				.pattern("##")
				.define('#', LAPIS_MANALIS_PILLAR)
				.unlockedBy("has_lapis_manalis_pillar", has(LAPIS_MANALIS_PILLAR))
				.save(out);

		shaped(BUILDING_BLOCKS, LAPIS_MANALIS_PILLAR_BASE_DORIC, 2)
				.pattern("2")
				.pattern("1")
				.define('1', LAPIS_MANALIS)
				.define('2', LAPIS_MANALIS_PILLAR)
				.unlockedBy("has_lapis_manalis_pillar", has(LAPIS_MANALIS_PILLAR))
				.save(out);

		shaped(BUILDING_BLOCKS, LAPIS_MANALIS_PILLAR_BASE_IONIC, 4)
				.pattern("22")
				.pattern("11")
				.define('1', LAPIS_MANALIS)
				.define('2', LAPIS_MANALIS_PILLAR)
				.unlockedBy("has_lapis_manalis_pillar", has(LAPIS_MANALIS_PILLAR))
				.save(out);

		shapeless(BUILDING_BLOCKS, LAPIS_MANALIS)
				.requires(ModItemTags.LAPIDES_MANALIS)
				.unlockedBy("has_lapides_manalis", has(ModItemTags.LAPIDES_MANALIS))
				.save(out, "lapis_manalis_revert");

		stairBuilder(LAPIS_MANALIS_STAIRS, Ingredient.of(LAPIS_MANALIS))
				.unlockedBy("has_lapis_manalis", has(LAPIS_MANALIS))
				.save(out);

		stairBuilder(LAPIS_MANALIS_BRICK_STAIRS, Ingredient.of(LAPIS_MANALIS_BRICKS))
				.unlockedBy("has_lapis_manalis_bricks", has(LAPIS_MANALIS_BRICKS))
				.save(out);

		stonecutterResultFromBase(out, BUILDING_BLOCKS, LAPIS_MANALIS_BRICKS, LAPIS_MANALIS);
		stonecutterResultFromBase(out, BUILDING_BLOCKS, LAPIS_MANALIS_PILLAR, LAPIS_MANALIS);
		stonecutterResultFromBase(out, BUILDING_BLOCKS, LAPIS_MANALIS_PILLAR_ORNAMENT_DORIC, LAPIS_MANALIS);
		stonecutterResultFromBase(out, BUILDING_BLOCKS, LAPIS_MANALIS_PILLAR_ORNAMENT_IONIC, LAPIS_MANALIS);
		stonecutterResultFromBase(out, BUILDING_BLOCKS, LAPIS_MANALIS_PILLAR_ORNAMENT_CORINTHIAN, LAPIS_MANALIS);
		stonecutterResultFromBase(out, BUILDING_BLOCKS, LAPIS_MANALIS_PILLAR_BASE_DORIC, LAPIS_MANALIS);
		stonecutterResultFromBase(out, BUILDING_BLOCKS, LAPIS_MANALIS_PILLAR_BASE_IONIC, LAPIS_MANALIS);
		stonecutterResultFromBase(out, BUILDING_BLOCKS, LAPIS_MANALIS_PILLAR_ORNAMENT_DORIC, LAPIS_MANALIS_PILLAR);
		stonecutterResultFromBase(out, BUILDING_BLOCKS, LAPIS_MANALIS_PILLAR_ORNAMENT_IONIC, LAPIS_MANALIS_PILLAR);
		stonecutterResultFromBase(out, BUILDING_BLOCKS, LAPIS_MANALIS_PILLAR_ORNAMENT_CORINTHIAN, LAPIS_MANALIS_PILLAR);
		stonecutterResultFromBase(out, BUILDING_BLOCKS, LAPIS_MANALIS_PILLAR_BASE_DORIC, LAPIS_MANALIS_PILLAR);
		stonecutterResultFromBase(out, BUILDING_BLOCKS, LAPIS_MANALIS_PILLAR_BASE_IONIC, LAPIS_MANALIS_PILLAR);

		stonecutterResultFromBase(out, BUILDING_BLOCKS, LAPIS_MANALIS_SLAB, LAPIS_MANALIS, 2);
		stonecutterResultFromBase(out, BUILDING_BLOCKS, LAPIS_MANALIS_BRICK_SLAB, LAPIS_MANALIS, 2);
		stonecutterResultFromBase(out, BUILDING_BLOCKS, LAPIS_MANALIS_BRICK_SLAB, LAPIS_MANALIS_BRICKS, 2);
		stonecutterResultFromBase(out, BUILDING_BLOCKS, LAPIS_MANALIS_STAIRS, LAPIS_MANALIS);
		stonecutterResultFromBase(out, BUILDING_BLOCKS, LAPIS_MANALIS_BRICK_STAIRS, LAPIS_MANALIS);
		stonecutterResultFromBase(out, BUILDING_BLOCKS, LAPIS_MANALIS_BRICK_STAIRS, LAPIS_MANALIS_BRICKS);
	}

	private ShapedRecipeBuilder wandRecipe(boolean tool, ItemLike result) {
		return wandRecipe(tool ? TOOLS : COMBAT, result);
	}

	private ShapedRecipeBuilder wandRecipe(RecipeCategory category, ItemLike result) {
		return shaped(category, result)
				.pattern(" 12")
				.pattern(" 11")
				.pattern("1  ");
	}

	private List<ItemLike> ingredients(Ore ore) {
		List<ItemLike> list = new ArrayList<>();
		if (ore == Ore.SILVER) list.add(RAW_SILVER);

		ore.allOreItems().forEach(list::add);

		return list;
	}
}
