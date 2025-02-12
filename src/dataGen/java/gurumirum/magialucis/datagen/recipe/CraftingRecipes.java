package gurumirum.magialucis.datagen.recipe;

import gurumirum.magialucis.contents.Gem;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.ModItemTags;
import gurumirum.magialucis.contents.recipe.crafting.RelayGemSwapRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import static gurumirum.magialucis.MagiaLucisMod.id;
import static gurumirum.magialucis.contents.Accessories.*;
import static gurumirum.magialucis.contents.ModBuildingBlocks.LAPIS_MANALIS_BRICK_SLAB;
import static gurumirum.magialucis.contents.ModBuildingBlocks.LAPIS_MANALIS_SLAB;
import static gurumirum.magialucis.contents.ModItems.*;
import static gurumirum.magialucis.datagen.builder.ShapedRelayCraftingRecipeBuilder.shapedRelayCrafting;
import static net.minecraft.data.recipes.RecipeCategory.MISC;
import static net.minecraft.data.recipes.RecipeCategory.TOOLS;
import static net.minecraft.data.recipes.ShapedRecipeBuilder.shaped;
import static net.minecraft.data.recipes.ShapelessRecipeBuilder.shapeless;
import static net.minecraft.data.recipes.SpecialRecipeBuilder.special;

public abstract class CraftingRecipes extends SharedRecipeLogic {
	public static void add(@NotNull RecipeOutput out) {
		shaped(MISC, LUMINOUS_RESONATOR)
				.pattern(" 12")
				.pattern("131")
				.pattern("21 ")
				.define('1', ModItemTags.BASIC_ALLOY_NUGGETS)
				.define('2', ModItemTags.LUMINOUS_ALLOY_INGOTS)
				.define('3', gem(Gem.BRIGHTSTONE))
				.unlockedBy("has_luminous_alloy", has(ModItemTags.LUMINOUS_ALLOY_INGOTS))
				.save(out);

		shaped(MISC, LUMINOUS_RESONANCE_CORE)
				.pattern("132")
				.pattern("343")
				.pattern("231")
				.define('1', ModItemTags.BASIC_ALLOY_INGOTS)
				.define('2', LUMINOUS_RESONATOR)
				.define('3', LUMINOUS_MECHANICAL_COMPONENT)
				.define('4', gem(Gem.BRIGHTSTONE))
				.unlockedBy("has_luminous_resonator", has(LUMINOUS_RESONATOR))
				.save(out);

		shaped(MISC, MECHANICAL_COMPONENT, 2)
				.pattern(" 1 ")
				.pattern("121")
				.pattern(" 1 ")
				.define('1', Tags.Items.INGOTS_IRON)
				.define('2', gem(Gem.BRIGHTSTONE))
				.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
				.save(out);

		shaped(MISC, LUMINOUS_MECHANICAL_COMPONENT, 2)
				.pattern(" 1 ")
				.pattern("121")
				.pattern(" 1 ")
				.define('1', ModItemTags.LUMINOUS_ALLOY_INGOTS)
				.define('2', gem(Gem.BRIGHTSTONE))
				.unlockedBy("has_luminous_alloy", has(ModItemTags.LUMINOUS_ALLOY_INGOTS))
				.save(out);

		shaped(MISC, CITRINE_MATRIX)
				.pattern("121")
				.pattern("232")
				.pattern("121")
				.define('1', gem(Gem.BRIGHTSTONE))
				.define('2', gem(Gem.CITRINE))
				.define('3', MECHANICAL_COMPONENT)
				.unlockedBy("has_citrine", hasGem(Gem.CITRINE))
				.save(out);

		shaped(MISC, IOLITE_MATRIX)
				.pattern("121")
				.pattern("232")
				.pattern("121")
				.define('1', gem(Gem.BRIGHTSTONE))
				.define('2', gem(Gem.IOLITE))
				.define('3', LUMINOUS_MECHANICAL_COMPONENT)
				.unlockedBy("has_iolite", hasGem(Gem.IOLITE))
				.save(out);


		shaped(MISC, ModBlocks.RELAY)
				.pattern(" 1 ")
				.pattern("1 1")
				.pattern("232")
				.define('1', gem(Gem.BRIGHTSTONE))
				.define('2', Tags.Items.NUGGETS_GOLD)
				.define('3', ItemTags.WOODEN_SLABS)
				.unlockedBy("has_brightstones", hasGem(Gem.BRIGHTSTONE))
				.save(out);

		shapedRelayCrafting(MISC, ModBlocks.SPLITTER)
				.pattern("121")
				.pattern("232")
				.pattern("141")
				.define('1', ItemTags.LOGS)
				.define('2', Tags.Items.NUGGETS_GOLD)
				.define('3', MECHANICAL_COMPONENT)
				.define('4', ModBlocks.RELAY)
				.unlockedBy("has_brightstones", hasGem(Gem.BRIGHTSTONE))
				.save(out);

		shapedRelayCrafting(MISC, ModBlocks.CONNECTOR)
				.pattern("131")
				.pattern("242")
				.define('1', ItemTags.LOGS)
				.define('2', Tags.Items.NUGGETS_GOLD)
				.define('3', ModBlocks.RELAY)
				.define('4', LUMINOUS_RESONATOR)
				.unlockedBy("has_luminous_resonator", has(LUMINOUS_RESONATOR))
				.save(out);

		shaped(MISC, ModBlocks.AMBER_CORE)
				.pattern("121")
				.pattern("232")
				.pattern("121")
				.define('1', ItemTags.LOGS)
				.define('2', gem(Gem.AMBER))
				.define('3', Items.HONEYCOMB)
				.unlockedBy("has_amber", hasGem(Gem.AMBER))
				.save(out);

		shaped(MISC, ModBlocks.AMBER_CHARGER)
				.pattern("121")
				.define('1', ItemTags.LOGS)
				.define('2', gem(Gem.AMBER))
				.unlockedBy("has_amber", hasGem(Gem.AMBER))
				.save(out);

		shaped(MISC, ModBlocks.AMBER_LANTERN)
				.pattern(" 1 ")
				.pattern("232")
				.pattern(" 1 ")
				.define('1', ItemTags.LOGS)
				.define('2', gem(Gem.AMBER))
				.define('3', LUMINOUS_RESONATOR)
				.unlockedBy("has_luminous_resonator", has(LUMINOUS_RESONATOR))
				.save(out);

		shaped(MISC, ModBlocks.SUNLIGHT_CORE)
				.pattern("111")
				.pattern("121")
				.pattern("343")
				.define('1', gem(Gem.BRIGHTSTONE))
				.define('2', CITRINE_MATRIX)
				.define('3', Tags.Items.NUGGETS_GOLD)
				.define('4', ItemTags.PLANKS)
				.unlockedBy("has_citrine", hasGem(Gem.CITRINE))
				.save(out);

		shaped(MISC, ModBlocks.MOONLIGHT_CORE)
				.pattern("111")
				.pattern("121")
				.pattern("343")
				.define('1', gem(Gem.BRIGHTSTONE))
				.define('2', IOLITE_MATRIX)
				.define('3', ModItemTags.SILVER_NUGGETS)
				.define('4', ItemTags.PLANKS)
				.unlockedBy("has_iolite", hasGem(Gem.IOLITE))
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

		shaped(MISC, ModBlocks.ARTISANRY_TABLE)
				.pattern("121")
				.pattern("131")
				.define('1', ItemTags.PLANKS)
				.define('2', Items.CRAFTING_TABLE)
				.define('3', Items.CHEST)
				.unlockedBy("has_planks", has(ItemTags.PLANKS))
				.save(out);

		shaped(MISC, ModBlocks.LIGHTLOOM_BASE)
				.pattern("1 1")
				.pattern("121")
				.pattern("111")
				.define('1', Tags.Items.INGOTS_IRON)
				.define('2', gem(Gem.BRIGHTSTONE))
				.unlockedBy("has_brightstone", hasGem(Gem.BRIGHTSTONE))
				.save(out);

		shapeless(MISC, ModBlocks.CITRINE_LIGHTLOOM)
				.requires(ModBlocks.LIGHTLOOM_BASE)
				.requires(CITRINE_MATRIX)
				.unlockedBy("has_citrine", hasGem(Gem.CITRINE))
				.save(out);

		shapeless(MISC, ModBlocks.IOLITE_LIGHTLOOM)
				.requires(ModBlocks.LIGHTLOOM_BASE)
				.requires(IOLITE_MATRIX)
				.unlockedBy("has_iolite", hasGem(Gem.IOLITE))
				.save(out);

		shaped(MISC, ModBlocks.LIGHT_BASIN)
				.pattern("121")
				.pattern("333")
				.define('1', ModItemTags.ROSE_GOLD_INGOTS)
				.define('2', ModItemTags.SILVER_INGOTS)
				.define('3', ModItemTags.LAPIDES_MANALIS)
				.unlockedBy("has_silver_ingots", has(ModItemTags.SILVER_INGOTS))
				.save(out);

		shaped(MISC, ModBlocks.LUMINOUS_CHARGER)
				.pattern("121")
				.define('1', ModItemTags.LAPIDES_MANALIS)
				.define('2', LUMINOUS_RESONATOR)
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
				.define('1', gem(Gem.BRIGHTSTONE))
				.define('C', ModBlocks.LUMINOUS_LANTERN_BASE)
				.unlockedBy("has_luminous_resonator", has(LUMINOUS_RESONATOR))
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
				.define('1', gem(Gem.AMBER))
				.define('2', ItemTags.LEAVES)
				.unlockedBy("has_amber", hasGem(Gem.AMBER))
				.save(out);

		shaped(TOOLS, DRYAD_WREATH)
				.pattern("212")
				.pattern("2 2")
				.define('1', gem(Gem.AMBER))
				.define('2', ItemTags.SMALL_FLOWERS)
				.unlockedBy("has_amber", hasGem(Gem.AMBER))
				.save(out);

		special(RelayGemSwapRecipe::new).save(out, id("relay_gem_swap"));
	}
}
