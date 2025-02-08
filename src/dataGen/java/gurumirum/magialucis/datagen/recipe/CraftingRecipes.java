package gurumirum.magialucis.datagen.recipe;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.contents.ModItemTags;
import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import static gurumirum.magialucis.contents.ModItems.*;
import static gurumirum.magialucis.contents.ModItems.LUMINOUS_MECHANICAL_COMPONENT;
import static net.minecraft.data.recipes.RecipeCategory.MISC;
import static net.minecraft.data.recipes.ShapedRecipeBuilder.shaped;

public abstract class CraftingRecipes extends SharedRecipeLogic {
	public static void add(@NotNull RecipeOutput out) {
		shaped(MISC, LUMINOUS_RESONATOR)
				.pattern(" 12")
				.pattern("131")
				.pattern("21 ")
				.define('1', ModItemTags.BASIC_ALLOY_NUGGETS)
				.define('2', ModItemTags.LUMINOUS_ALLOY_INGOTS)
				.define('3', gem(GemStats.BRIGHTSTONE))
				.unlockedBy("has_luminous_alloy", has(ModItemTags.LUMINOUS_ALLOY_INGOTS))
				.save(out);

		shaped(MISC, LUMINOUS_RESONANCE_CORE)
				.pattern("132")
				.pattern("343")
				.pattern("231")
				.define('1', ModItemTags.BASIC_ALLOY_INGOTS)
				.define('2', LUMINOUS_RESONATOR)
				.define('3', LUMINOUS_MECHANICAL_COMPONENT)
				.define('4', gem(GemStats.BRIGHTSTONE))
				.unlockedBy("has_luminous_resonator", has(LUMINOUS_RESONATOR))
				.save(out);

		shaped(MISC, MECHANICAL_COMPONENT, 2)
				.pattern(" 1 ")
				.pattern("121")
				.pattern(" 1 ")
				.define('1', Tags.Items.INGOTS_IRON)
				.define('2', gem(GemStats.BRIGHTSTONE))
				.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
				.save(out);

		shaped(MISC, LUMINOUS_MECHANICAL_COMPONENT, 2)
				.pattern(" 1 ")
				.pattern("121")
				.pattern(" 1 ")
				.define('1', ModItemTags.LUMINOUS_ALLOY_INGOTS)
				.define('2', gem(GemStats.BRIGHTSTONE))
				.unlockedBy("has_luminous_alloy", has(ModItemTags.LUMINOUS_ALLOY_INGOTS))
				.save(out);

		shaped(MISC, CITRINE_MATRIX)
				.pattern("121")
				.pattern("232")
				.pattern("121")
				.define('1', gem(GemStats.BRIGHTSTONE))
				.define('2', gem(GemStats.CITRINE))
				.define('3', MECHANICAL_COMPONENT)
				.unlockedBy("has_citrine", hasGem(GemStats.CITRINE))
				.save(out);

		shaped(MISC, IOLITE_MATRIX)
				.pattern("121")
				.pattern("232")
				.pattern("121")
				.define('1', gem(GemStats.BRIGHTSTONE))
				.define('2', gem(GemStats.IOLITE))
				.define('3', LUMINOUS_MECHANICAL_COMPONENT)
				.unlockedBy("has_iolite", hasGem(GemStats.IOLITE))
				.save(out);

	}
}
