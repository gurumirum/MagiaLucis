package gurumirum.magialucis.datagen.recipe;

import gurumirum.magialucis.contents.Gem;
import gurumirum.magialucis.contents.GemItems;
import gurumirum.magialucis.contents.ModItemTags;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import static gurumirum.magialucis.contents.ModItems.ANCIENT_CORE;
import static gurumirum.magialucis.contents.ModItems.LUMINOUS_RESONATOR;
import static gurumirum.magialucis.contents.Wands.*;
import static net.minecraft.data.recipes.RecipeCategory.COMBAT;
import static net.minecraft.data.recipes.RecipeCategory.TOOLS;
import static net.minecraft.data.recipes.ShapedRecipeBuilder.shaped;

public abstract class WandRecipes extends SharedRecipeLogic {
	public static final int WAND_RECIPE_TICKS = 400;

	public static void add(@NotNull RecipeOutput out) {
		shaped(TOOLS, ANCIENT_LIGHT)
				.pattern(" 12")
				.pattern(" 11")
				.pattern("1  ")
				.define('1', ItemTags.STONE_BRICKS)
				.define('2', ANCIENT_CORE)
				.unlockedBy("has_ancient_core", has(ANCIENT_CORE))
				.save(out);

		shaped(TOOLS, CONFIGURATION_WAND)
				.pattern(" 12")
				.pattern(" 11")
				.pattern("1  ")
				.define('1', ItemTags.STONE_BRICKS)
				.define('2', GemItems.BRIGHTSTONE)
				.unlockedBy("has_brightstone", has(GemItems.BRIGHTSTONE))
				.save(out);

		shaped(TOOLS, RED_CONFIGURATION_WAND)
				.pattern(" 12")
				.pattern(" 11")
				.pattern("1  ")
				.define('1', ItemTags.STONE_BRICKS)
				.define('2', GemItems.RED_BRIGHTSTONE)
				.unlockedBy("has_red_brightstone", has(GemItems.RED_BRIGHTSTONE))
				.save(out);

		shaped(TOOLS, ICY_CONFIGURATION_WAND)
				.pattern(" 12")
				.pattern(" 11")
				.pattern("1  ")
				.define('1', ItemTags.STONE_BRICKS)
				.define('2', GemItems.ICY_BRIGHTSTONE)
				.unlockedBy("has_icy_brightstone", has(GemItems.ICY_BRIGHTSTONE))
				.save(out);

		shaped(TOOLS, AMBER_TORCH)
				.pattern(" 12")
				.pattern(" 11")
				.pattern("1  ")
				.define('1', ItemTags.LOGS)
				.define('2', gem(Gem.AMBER))
				.unlockedBy("has_amber", hasGem(Gem.AMBER))
				.save(out);

		shaped(COMBAT, LESSER_ICE_STAFF)
				.pattern(" 12")
				.pattern(" 11")
				.pattern("1  ")
				.define('1', ItemTags.LOGS)
				.define('2', GemItems.ICY_BRIGHTSTONE)
				.unlockedBy("has_icy_brightstone", has(GemItems.ICY_BRIGHTSTONE))
				.save(out);

		artisanry(RECALL_STAFF)
				.pattern(" 32")
				.pattern("4C3")
				.pattern("14 ")
				.define('C', LUMINOUS_RESONATOR)
				.define('1', ModItemTags.ROSE_GOLD_INGOTS)
				.define('2', gem(Gem.AQUAMARINE))
				.define('3', Tags.Items.LEATHERS)
				.define('4', Tags.Items.DYES_RED)
				.processTicks(WAND_RECIPE_TICKS)
				.luxInput(b -> b.minG(15).minB(25))
				.save(out);

		artisanry(HEAL_WAND)
				.pattern(" 12")
				.pattern(" C1")
				.pattern("1  ")
				.define('C', LUMINOUS_RESONATOR)
				.define('1', Tags.Items.INGOTS_GOLD)
				.define('2', gem(Gem.PEARL))
				.processTicks(WAND_RECIPE_TICKS)
				.luxInput(b -> b.minR(60).minG(50).minB(60))
				.save(out);

		artisanry(ENDER_WAND)
				.pattern(" 12")
				.pattern(" C1")
				.pattern("1  ")
				.define('C', LUMINOUS_RESONATOR)
				.define('1', ModItemTags.SILVER_INGOTS)
				.define('2', gem(Gem.ENDER_PEARL))
				.processTicks(WAND_RECIPE_TICKS)
				.luxInput(b -> b.minG(30).minB(15))
				.save(out);

		artisanry(LAPIS_SHIELD)
				.pattern("121")
				.pattern("1C1")
				.pattern(" 1 ")
				.define('C', LUMINOUS_RESONATOR)
				.define('1', ModItemTags.ROSE_GOLD_INGOTS)
				.define('2', GemItems.POLISHED_LAPIS_LAZULI)
				.processTicks(WAND_RECIPE_TICKS)
				.luxInput(b -> b.minB(300))
				.save(out);

		artisanry(DIAMOND_MACE)
				.pattern("  2")
				.pattern(" C ")
				.pattern("1  ")
				.define('C', LUMINOUS_RESONATOR)
				.define('1', Tags.Items.INGOTS_IRON)
				.define('2', gem(Gem.DIAMOND))
				.processTicks(WAND_RECIPE_TICKS)
				.luxInput(b -> b.minSum(2000))
				.save(out);
	}
}
