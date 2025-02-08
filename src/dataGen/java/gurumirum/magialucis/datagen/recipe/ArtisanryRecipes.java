package gurumirum.magialucis.datagen.recipe;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.contents.GemItems;
import gurumirum.magialucis.contents.ModItemTags;
import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import static gurumirum.magialucis.contents.Accessories.*;
import static gurumirum.magialucis.contents.ModItems.LUMINOUS_RESONATOR;

public abstract class ArtisanryRecipes extends SharedRecipeLogic {
	public static final int RING_RECIPE_TICKS = 200;
	public static final int BRACELET_RECIPE_TICKS = 500;
	public static final int NECKLACE_RECIPE_TICKS = 2000;
	public static final int HEADWEAR_RECIPE_TICKS = 4000;

	public static void add(@NotNull RecipeOutput out) {
		artisanry(FIRE_ARROW_RING)
				.pattern("12 ")
				.pattern("2 2")
				.pattern(" 2 ")
				.define('1', GemItems.RED_BRIGHTSTONE)
				.define('2', ModItemTags.STERLING_SILVER_NUGGETS)
				.processTicks(RING_RECIPE_TICKS)
				.save(out);

		artisanry(SOUL_CROWN)
				.pattern("111")
				.pattern("1 1")
				.define('1', GemItems.SOUL_BRIGHTSTONE)
				.processTicks(HEADWEAR_RECIPE_TICKS)
				.luxInput(b -> b.minR(50))
				.save(out);

		artisanry(SPEED_RING)
				.pattern("12 ")
				.pattern("2 2")
				.pattern(" 2 ")
				.define('1', gem(GemStats.CITRINE))
				.define('2', ModItemTags.ROSE_GOLD_NUGGETS)
				.processTicks(RING_RECIPE_TICKS)
				.luxInput(b -> b.minR(20).minG(20))
				.save(out);

		artisanry(OBSIDIAN_BRACELET)
				.pattern("121")
				.pattern("232")
				.pattern("121")
				.define('1', gem(GemStats.OBSIDIAN))
				.define('2', Tags.Items.STRINGS)
				.define('3', LUMINOUS_RESONATOR)
				.processTicks(BRACELET_RECIPE_TICKS)
				.luxInput(b -> b.minB(50))
				.save(out);

		artisanry(SHIELD_NECKLACE)
				.pattern("2 2")
				.pattern("232")
				.pattern(" 1 ")
				.define('1', gem(GemStats.POLISHED_LAPIS_LAZULI))
				.define('2', ModItemTags.ROSE_GOLD_NUGGETS)
				.define('3', LUMINOUS_RESONATOR)
				.processTicks(NECKLACE_RECIPE_TICKS)
				.luxInput(b -> b.minB(300))
				.save(out);
	}
}
