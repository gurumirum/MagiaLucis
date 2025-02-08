package gurumirum.magialucis.datagen.recipe;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.contents.GemItems;
import gurumirum.magialucis.contents.ModItemTags;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import static gurumirum.magialucis.contents.ModBuildingBlocks.LAPIS_MANALIS;
import static gurumirum.magialucis.contents.ModItems.*;
import static gurumirum.magialucis.contents.ModItems.STONE_OF_PURIFICATION;

public abstract class LightBasinRecipes extends SharedRecipeLogic {
	public static void add(@NotNull RecipeOutput out) {
		lightBasin()
				.ingredient(Blocks.SAND)
				.result(GemItems.BRIGHTSTONE)
				.processTicks(40)
				.luxInput(b -> b.minSum(25))
				.save(out);

		lightBasin()
				.ingredient(Blocks.RED_SAND)
				.result(GemItems.RED_BRIGHTSTONE)
				.processTicks(40)
				.luxInput(b -> b.minSum(25))
				.save(out);

		lightBasin()
				.ingredient(Blocks.ICE)
				.result(GemItems.ICY_BRIGHTSTONE)
				.processTicks(40)
				.luxInput(b -> b.minSum(25))
				.save(out);

		lightBasin()
				.ingredient(Blocks.SOUL_SAND)
				.result(GemItems.SOUL_BRIGHTSTONE)
				.processTicks(80)
				.luxInput(b -> b.minSum(75))
				.save(out);

		lightBasin()
				.ingredient(Ingredient.of(Blocks.STONE, Blocks.DEEPSLATE))
				.result(LAPIS_MANALIS)
				.processTicks(60)
				.luxInput(b -> b.minSum(50))
				.save(out);

		lightBasin()
				.ingredient(Tags.Items.NUGGETS_GOLD)
				.ingredient(ModItemTags.SILVER_NUGGETS)
				.result(ELECTRUM_NUGGET, 2)
				.processTicks(20)
				.luxInput(b -> b.minSum(100))
				.save(out);

		lightBasin()
				.ingredient(Tags.Items.NUGGETS_GOLD)
				.ingredient(ModItemTags.COPPER_NUGGETS)
				.result(ROSE_GOLD_NUGGET, 2)
				.processTicks(20)
				.luxInput(b -> b.minSum(100))
				.save(out);

		lightBasin()
				.ingredient(ModItemTags.SILVER_NUGGETS)
				.ingredient(ModItemTags.COPPER_NUGGETS)
				.result(STERLING_SILVER_NUGGET, 2)
				.processTicks(20)
				.luxInput(b -> b.minSum(100))
				.save(out);

		lightBasin()
				.ingredient(ModItemTags.ELECTRUM_NUGGETS)
				.ingredient(ModItemTags.ROSE_GOLD_NUGGETS)
				.ingredient(ModItemTags.STERLING_SILVER_NUGGETS)
				.ingredient(Tags.Items.NUGGETS_IRON)
				.result(LUMINOUS_ALLOY_NUGGET, 3)
				.processTicks(30)
				.luxInput(b -> b.minR(80).minG(80))
				.save(out);

		lightBasin()
				.ingredient(Items.OBSIDIAN)
				.result(GemItems.OBSIDIAN)
				.processTicks(80)
				.luxInput(b -> b.minB(80))
				.save(out);

		lightBasin()
				.ingredient(Items.BLAZE_POWDER)
				.ingredient(gem(GemStats.CITRINE))
				.result(SUNLIGHT_INFUSED_POWDER, 3)
				.processTicks(100)
				.luxInput(b -> b.minR(100).minG(100))
				.save(out);

		lightBasin()
				.ingredient(Items.BLAZE_POWDER)
				.ingredient(gem(GemStats.IOLITE))
				.result(MOONLIGHT_INFUSED_POWDER, 3)
				.processTicks(100)
				.luxInput(b -> b.minB(100))
				.save(out);

		lightBasin()
				.ingredient(SUNLIGHT_INFUSED_POWDER)
				.ingredient(MOONLIGHT_INFUSED_POWDER)
				.result(STONE_OF_PURIFICATION)
				.processTicks(500)
				.luxInput(b -> b.minSum(100))
				.save(out);

		lightBasin()
				.ingredient(STONE_OF_PURIFICATION)
				.ingredient(Tags.Items.GEMS_QUARTZ, 8)
				.result(GemItems.PURIFIED_QUARTZ, 6)
				.processTicks(100)
				.luxInput(b -> b.minSum(150))
				.save(out);

		lightBasin()
				.ingredient(STONE_OF_PURIFICATION)
				.ingredient(Items.REDSTONE_BLOCK)
				.result(GemItems.CRYSTALLIZED_REDSTONE)
				.processTicks(200)
				.luxInput(b -> b.minSum(150))
				.save(out);

		lightBasin()
				.ingredient(STONE_OF_PURIFICATION)
				.ingredient(Items.LAPIS_BLOCK)
				.result(GemItems.POLISHED_LAPIS_LAZULI)
				.processTicks(200)
				.luxInput(b -> b.minSum(150))
				.save(out);
	}
}
