package gurumirum.magialucis.datagen.recipe;

import gurumirum.magialucis.contents.Gem;
import gurumirum.magialucis.contents.GemItems;
import gurumirum.magialucis.contents.ModItemTags;
import gurumirum.magialucis.contents.Wands;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import static gurumirum.magialucis.contents.Accessories.*;
import static gurumirum.magialucis.contents.Augments.*;
import static gurumirum.magialucis.contents.ModItems.*;

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
				.define('1', gem(Gem.CITRINE))
				.define('2', ModItemTags.ROSE_GOLD_NUGGETS)
				.processTicks(RING_RECIPE_TICKS)
				.luxInput(b -> b.minR(20).minG(20))
				.save(out);

		artisanry(OBSIDIAN_BRACELET)
				.pattern("121")
				.pattern("232")
				.pattern("121")
				.define('1', gem(Gem.OBSIDIAN))
				.define('2', Tags.Items.STRINGS)
				.define('3', LUMINOUS_RESONATOR)
				.processTicks(BRACELET_RECIPE_TICKS)
				.luxInput(b -> b.minB(50))
				.save(out);

		artisanry(SHIELD_NECKLACE)
				.pattern("2 2")
				.pattern("232")
				.pattern(" 1 ")
				.define('1', gem(Gem.POLISHED_LAPIS_LAZULI))
				.define('2', ModItemTags.ROSE_GOLD_NUGGETS)
				.define('3', LUMINOUS_RESONATOR)
				.processTicks(NECKLACE_RECIPE_TICKS)
				.luxInput(b -> b.minB(300))
				.save(out);

		augment()
				.addAugment(OVERCHARGE_1)
				.incompatible(OVERCHARGE_2, OVERCHARGE_3)
				.pattern(" 1 ")
				.pattern("2 2")
				.pattern(" 3 ")
				.define('1', INSCRIPTION_LUX_CAPACITY, 0)
				.define('2', LUMINOUS_RESONATOR)
				.define('3', gem(Gem.BRIGHTSTONE))
				.processTicks(0)
				.save(out);

		augment()
				.addAugment(OVERCHARGE_2)
				.removeAugment(OVERCHARGE_1)
				.precursor(OVERCHARGE_1)
				.incompatible(OVERCHARGE_3)
				.pattern(" 1 ")
				.pattern("2 2")
				.pattern(" 3 ")
				.define('1', INSCRIPTION_LUX_CAPACITY, 0)
				.define('2', LUMINOUS_RESONANCE_CORE)
				.define('3', gem(Gem.PURIFIED_QUARTZ))
				.processTicks(0)
				.save(out);

		augment()
				.addAugment(OVERCHARGE_3)
				.removeAugment(OVERCHARGE_2)
				.precursor(OVERCHARGE_2)
				.pattern(" 1 ")
				.pattern("2 2")
				.pattern(" 3 ")
				.define('1', INSCRIPTION_LUX_CAPACITY, 0)
				.define('2', LUMINOUS_RESONANCE_CORE, 2)
				.define('3', Items.BEDROCK, 64)
				.processTicks(0)
				.save(out);

		augment()
				.addAugment(CONFIGURATION_WAND_DEBUG_VIEW)
				.pattern(" 1 ")
				.pattern("   ")
				.pattern("   ")
				.define('1', INSCRIPTION_CONFIGURATION, 0)
				.processTicks(0)
				.save(out);

		augment()
				.addAugment(AMBER_TORCH_CONCEALED_FLAME)
				.pattern(" 1 ")
				.pattern("   ")
				.pattern("   ")
				.define('1', INSCRIPTION_CONCEALMENT, 0)
				.processTicks(0)
				.save(out);

		augment()
				.addAugment(ACCELERATION_1)
				.pattern(" 1 ")
				.pattern("2 2")
				.pattern(" 3 ")
				.define('1', INSCRIPTION_SPEED, 0)
				.define('2', gem(Gem.CITRINE))
				.define('3', LUMINOUS_RESONANCE_CORE)
				.processTicks(0)
				.save(out);

		augment()
				.addAugment(QUICK_CAST_1)
				.pattern(" 1 ")
				.pattern("2 2")
				.pattern(" 3 ")
				.define('1', INSCRIPTION_CASTING_SPEED, 0)
				.define('2', gem(Gem.AQUAMARINE))
				.define('3', LUMINOUS_RESONANCE_CORE)
				.processTicks(0)
				.save(out);

		augment()
				.addAugment(EXPANSION_1)
				.incompatible(EXPANSION_2, EXPANSION_3)
				.pattern("414")
				.pattern("2#2")
				.pattern("434")
				.define('#', Wands.ENDER_WAND)
				.define('1', INSCRIPTION_SPATIAL, 0)
				.define('2', Items.ENDER_EYE)
				.define('3', LUMINOUS_RESONANCE_CORE)
				.define('4', gem(Gem.OBSIDIAN))
				.processTicks(0)
				.save(out, "ender_wand_expansion_1");

		augment()
				.addAugment(EXPANSION_2)
				.removeAugment(EXPANSION_1)
				.precursor(EXPANSION_1)
				.incompatible(EXPANSION_3)
				.pattern("414")
				.pattern("2#2")
				.pattern("434")
				.define('#', Wands.ENDER_WAND)
				.define('1', INSCRIPTION_SPATIAL, 0)
				.define('2', Items.ENDER_EYE)
				.define('3', LUMINOUS_RESONANCE_CORE)
				.define('4', gem(Gem.OBSIDIAN))
				.processTicks(0)
				.save(out, "ender_wand_expansion_2");

		augment()
				.addAugment(EXPANSION_3)
				.removeAugment(EXPANSION_2)
				.precursor(EXPANSION_2)
				.pattern("414")
				.pattern("2#2")
				.pattern("434")
				.define('#', Wands.ENDER_WAND)
				.define('1', INSCRIPTION_SPATIAL, 0)
				.define('2', Items.ENDER_EYE)
				.define('3', LUMINOUS_RESONANCE_CORE)
				.define('4', gem(Gem.OBSIDIAN))
				.processTicks(0)
				.save(out, "ender_wand_expansion_3");

		augment()
				.addAugment(ENDER_WAND_COLLECTOR)
				.pattern(" 1 ")
				.pattern("2 2")
				.pattern(" 3 ")
				.define('1', INSCRIPTION_SPATIAL, 0)
				.define('2', gem(Gem.ENDER_PEARL))
				.define('3', LUMINOUS_RESONANCE_CORE)
				.processTicks(0)
				.save(out);
	}
}
