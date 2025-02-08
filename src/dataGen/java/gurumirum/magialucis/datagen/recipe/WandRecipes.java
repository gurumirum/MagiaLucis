package gurumirum.magialucis.datagen.recipe;

import com.llamalad7.mixinextras.sugar.Share;
import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.contents.GemItems;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.ModItemTags;
import gurumirum.magialucis.datagen.RecipeGen;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import static gurumirum.magialucis.contents.Accessories.*;
import static gurumirum.magialucis.contents.ModBuildingBlocks.LAPIS_MANALIS_BRICK_SLAB;
import static gurumirum.magialucis.contents.ModBuildingBlocks.LAPIS_MANALIS_SLAB;
import static gurumirum.magialucis.contents.ModItems.*;
import static gurumirum.magialucis.contents.Wands.*;
import static net.minecraft.data.recipes.RecipeCategory.*;
import static net.minecraft.data.recipes.RecipeCategory.TOOLS;
import static net.minecraft.data.recipes.ShapedRecipeBuilder.shaped;

public abstract class WandRecipes extends SharedRecipeLogic {
	public static void add(@NotNull RecipeOutput out) {
		wandRecipe(true, ANCIENT_LIGHT, WandTier.PRIMITIVE)
				.define('1', ItemTags.STONE_BRICKS)
				.define('2', ANCIENT_CORE)
				.unlockedBy("has_ancient_core", has(ANCIENT_CORE))
				.save(out);

		wandRecipe(true, CONFIGURATION_WAND, WandTier.PRIMITIVE)
				.define('1', ItemTags.STONE_BRICKS)
				.define('2', GemItems.BRIGHTSTONE)
				.unlockedBy("has_brightstone", has(GemItems.BRIGHTSTONE))
				.save(out);
		wandRecipe(true, RED_CONFIGURATION_WAND, WandTier.PRIMITIVE)
				.define('1', ItemTags.STONE_BRICKS)
				.define('2', GemItems.RED_BRIGHTSTONE)
				.unlockedBy("has_red_brightstone", has(GemItems.RED_BRIGHTSTONE))
				.save(out);
		wandRecipe(true, ICY_CONFIGURATION_WAND, WandTier.PRIMITIVE)
				.define('1', ItemTags.STONE_BRICKS)
				.define('2', GemItems.ICY_BRIGHTSTONE)
				.unlockedBy("has_icy_brightstone", has(GemItems.ICY_BRIGHTSTONE))
				.save(out);

		wandRecipe(true, AMBER_TORCH, WandTier.PRIMITIVE)
				.define('1', ItemTags.LOGS)
				.define('2', gem(GemStats.AMBER))
				.unlockedBy("has_amber", hasGem(GemStats.AMBER))
				.save(out);

		wandRecipe(false, LESSER_ICE_STAFF, WandTier.PRIMITIVE)
				.define('1', ItemTags.LOGS)
				.define('2', GemItems.ICY_BRIGHTSTONE)
				.unlockedBy("has_icy_brightstone", has(GemItems.ICY_BRIGHTSTONE))
				.save(out);

		shaped(TOOLS, RECALL_STAFF)
				.pattern(" 32")
				.pattern("4C3")
				.pattern("14 ")
				.define('C', LUMINOUS_RESONATOR)
				.define('1', ModItemTags.ROSE_GOLD_INGOTS)
				.define('2', gem(GemStats.AQUAMARINE))
				.define('3', Tags.Items.LEATHERS)
				.define('4', Tags.Items.DYES_RED)
				.unlockedBy("has_aquamarine", hasGem(GemStats.AQUAMARINE))
				.save(out);

		wandRecipe(false, HEAL_WAND, WandTier.BASIC)
				.define('1', Tags.Items.INGOTS_GOLD)
				.define('2', gem(GemStats.PEARL))
				.unlockedBy("has_pearl", hasGem(GemStats.PEARL))
				.save(out);

		wandRecipe(false, ENDER_WAND, WandTier.BASIC)
				.define('1', ModItemTags.SILVER_INGOTS)
				.define('2', gem(GemStats.ENDER_PEARL))
				.unlockedBy("has_ender_pearl", hasGem(GemStats.ENDER_PEARL))
				.save(out);

		shaped(COMBAT, LAPIS_SHIELD)
				.pattern("121")
				.pattern("1C1")
				.pattern(" 1 ")
				.define('C', LUMINOUS_RESONATOR)
				.define('1', ModItemTags.ROSE_GOLD_INGOTS)
				.define('2', GemItems.POLISHED_LAPIS_LAZULI)
				.unlockedBy("has_polished_lapis_lazuli", has(GemItems.POLISHED_LAPIS_LAZULI))
				.save(out);

		shaped(COMBAT, DIAMOND_MACE)
				.pattern("  2")
				.pattern(" C ")
				.pattern("1  ")
				.define('C', LUMINOUS_RESONATOR)
				.define('1', Tags.Items.INGOTS_IRON)
				.define('2', gem(GemStats.DIAMOND))
				.unlockedBy("has_diamond", hasGem(GemStats.DIAMOND))
				.save(out);

		shaped(MISC, ModBlocks.RELAY)
				.pattern(" 1 ")
				.pattern("1 1")
				.pattern("232")
				.define('1', gem(GemStats.BRIGHTSTONE))
				.define('2', Tags.Items.NUGGETS_GOLD)
				.define('3', ItemTags.WOODEN_SLABS)
				.unlockedBy("has_brightstones", hasGem(GemStats.BRIGHTSTONE))
				.save(out);

		shaped(MISC, ModBlocks.AMBER_CHARGER)
				.pattern("121")
				.define('1', ItemTags.LOGS)
				.define('2', gem(GemStats.AMBER))
				.unlockedBy("has_amber", hasGem(GemStats.AMBER))
				.save(out);

		shaped(MISC, ModBlocks.LUMINOUS_CHARGER)
				.pattern("121")
				.define('1', ModItemTags.LAPIDES_MANALIS)
				.define('2', LUMINOUS_RESONATOR)
				.unlockedBy("has_luminous_resonator", has(LUMINOUS_RESONATOR))
				.save(out);

		shaped(MISC, ModBlocks.AMBER_LANTERN)
				.pattern(" 1 ")
				.pattern("232")
				.pattern(" 1 ")
				.define('1', ItemTags.LOGS)
				.define('2', gem(GemStats.AMBER))
				.define('3', LUMINOUS_RESONATOR)
				.unlockedBy("has_luminous_resonator", has(LUMINOUS_RESONATOR))
				.save(out);

		shaped(MISC, ModBlocks.LUMINOUS_LANTERN_BASE)
				.pattern("121")
				.pattern("343")
				.pattern("121")
				.define('1', ModItemTags.ROSE_GOLD_INGOTS)
				.define('2', ItemTags.LOGS)
				.define('3', ModItemTags.LAPIDES_MANALIS)
				.define('4', LUMINOUS_RESONANCE_CORE)
				.unlockedBy("has_luminous_resonator", has(LUMINOUS_RESONATOR))
				.save(out);

		shaped(MISC, ModBlocks.LUMINOUS_RESONANCE_LANTERN)
				.pattern(" 1 ")
				.pattern("1C1")
				.pattern(" 1 ")
				.define('1', gem(GemStats.BRIGHTSTONE))
				.define('C', ModBlocks.LUMINOUS_LANTERN_BASE)
				.unlockedBy("has_luminous_resonator", has(LUMINOUS_RESONATOR))
				.save(out);

		shaped(MISC, ModBlocks.AMBER_CORE)
				.pattern("121")
				.pattern("232")
				.pattern("121")
				.define('1', ItemTags.LOGS)
				.define('2', gem(GemStats.AMBER))
				.define('3', Items.HONEYCOMB)
				.unlockedBy("has_amber", hasGem(GemStats.AMBER))
				.save(out);

		shaped(MISC, ModBlocks.LIGHT_BASIN)
				.pattern("121")
				.pattern("333")
				.define('1', ModItemTags.ROSE_GOLD_INGOTS)
				.define('2', ModItemTags.SILVER_INGOTS)
				.define('3', ModItemTags.LAPIDES_MANALIS)
				.unlockedBy("has_silver_ingots", has(ModItemTags.SILVER_INGOTS))
				.save(out);

		shaped(MISC, ModBlocks.SUNLIGHT_CORE)
				.pattern("111")
				.pattern("121")
				.pattern("343")
				.define('1', gem(GemStats.BRIGHTSTONE))
				.define('2', CITRINE_MATRIX)
				.define('3', Tags.Items.NUGGETS_GOLD)
				.define('4', ItemTags.PLANKS)
				.unlockedBy("has_citrine", hasGem(GemStats.CITRINE))
				.save(out);

		shaped(MISC, ModBlocks.MOONLIGHT_CORE)
				.pattern("111")
				.pattern("121")
				.pattern("343")
				.define('1', gem(GemStats.BRIGHTSTONE))
				.define('2', IOLITE_MATRIX)
				.define('3', ModItemTags.SILVER_NUGGETS)
				.define('4', ItemTags.PLANKS)
				.unlockedBy("has_iolite", hasGem(GemStats.IOLITE))
				.save(out);

		shaped(MISC, ModBlocks.SUNLIGHT_FOCUS)
				.pattern("111")
				.pattern("242")
				.pattern("323")
				.define('1', ModItemTags.SILVER_INGOTS)
				.define('2', ModItemTags.LAPIDES_MANALIS)
				.define('3', Ingredient.of(LAPIS_MANALIS_SLAB, LAPIS_MANALIS_BRICK_SLAB))
				.define('4', MECHANICAL_COMPONENT)
				.unlockedBy("has_silver", has(ModItemTags.SILVER_INGOTS))
				.save(out);

		shaped(TOOLS, WAND_BELT)
				.pattern("122")
				.pattern("2 2")
				.pattern("222")
				.define('1', ModItemTags.BASIC_ALLOY_NUGGETS)
				.define('2', Tags.Items.LEATHERS)
				.unlockedBy("has_wands", has(ModItemTags.WANDS))
				.save(out);

		shaped(TOOLS, DRUID_WREATH)
				.pattern("212")
				.pattern("2 2")
				.define('1', gem(GemStats.AMBER))
				.define('2', ItemTags.LEAVES)
				.unlockedBy("has_amber", hasGem(GemStats.AMBER))
				.save(out);

		shaped(TOOLS, DRYAD_WREATH)
				.pattern("212")
				.pattern("2 2")
				.define('1', gem(GemStats.AMBER))
				.define('2', ItemTags.SMALL_FLOWERS)
				.unlockedBy("has_amber", hasGem(GemStats.AMBER))
				.save(out);
	}

	private static ShapedRecipeBuilder wandRecipe(boolean tool, ItemLike result, WandTier tier) {
		return wandRecipe(tool ? TOOLS : COMBAT, result, tier);
	}

	private static ShapedRecipeBuilder wandRecipe(RecipeCategory category, ItemLike result, WandTier wandTier) {
		return switch (wandTier) {
			case PRIMITIVE -> shaped(category, result)
					.pattern(" 12")
					.pattern(" 11")
					.pattern("1  ");
			case BASIC -> shaped(category, result)
					.pattern(" 12")
					.pattern(" C1")
					.pattern("1  ")
					.define('C', LUMINOUS_RESONATOR);
		};
	}

	private enum WandTier {
		PRIMITIVE,
		BASIC
	}
}
