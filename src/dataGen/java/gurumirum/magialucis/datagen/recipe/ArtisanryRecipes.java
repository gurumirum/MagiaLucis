package gurumirum.magialucis.datagen.recipe;

import gurumirum.magialucis.contents.Gem;
import gurumirum.magialucis.contents.GemItems;
import gurumirum.magialucis.contents.ModItemTags;
import gurumirum.magialucis.contents.Wands;
import gurumirum.magialucis.contents.augment.TieredAugmentType;
import gurumirum.magialucis.contents.augment.TieredAugmentTypes;
import gurumirum.magialucis.datagen.builder.AugmentRecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.IntFunction;

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

		tieredAugmentRecipes(out, TieredAugmentTypes.OVERCHARGE, (b, i) -> b
				.pattern(" 1 ")
				.pattern("2 2")
				.pattern(" 3 ")
				.define('1', INSCRIPTION_LUX_CAPACITY, 0)
				.define('2', LUMINOUS_RESONANCE_AUGMENTOR)
				.define('3', switch (i) {
					case 0 -> gem(Gem.BRIGHTSTONE);
					case 1 -> gem(Gem.PURIFIED_QUARTZ);
					case 2 -> Ingredient.of(Items.BEDROCK);
					default -> throw new IllegalStateException();
				})
				.instant());

		tieredAugmentRecipes(out, TieredAugmentTypes.ACCELERATION, (b, i) -> b
				.pattern(" 1 ")
				.pattern("2 2")
				.pattern(" 3 ")
				.define('1', INSCRIPTION_SPEED, 0)
				.define('2', gem(Gem.CITRINE))
				.define('3', LUMINOUS_RESONANCE_AUGMENTOR)
				.instant());

		tieredAugmentRecipes(out, TieredAugmentTypes.QUICK_CAST, (b, i) -> b
				.pattern(" 1 ")
				.pattern("2 2")
				.pattern(" 3 ")
				.define('1', INSCRIPTION_CASTING_SPEED, 0)
				.define('2', gem(Gem.AQUAMARINE))
				.define('3', LUMINOUS_RESONANCE_AUGMENTOR)
				.instant());

		tieredAugmentRecipes(out, TieredAugmentTypes.EXPANSION, (b, i) -> b
						.pattern("414")
						.pattern("2#2")
						.pattern("434")
						.define('#', Wands.ENDER_WAND)
						.define('1', INSCRIPTION_SPATIAL, 0)
						.define('2', Items.ENDER_EYE)
						.define('3', LUMINOUS_RESONANCE_AUGMENTOR)
						.define('4', gem(Gem.OBSIDIAN))
						.instant(),
				i -> "ender_wand_expansion_" + (i + 1));

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
				.addAugment(ENDER_WAND_COLLECTOR)
				.pattern(" 1 ")
				.pattern("2 2")
				.pattern(" 3 ")
				.define('1', INSCRIPTION_SPATIAL, 0)
				.define('2', gem(Gem.ENDER_PEARL))
				.define('3', LUMINOUS_RESONANCE_AUGMENTOR)
				.processTicks(0)
				.save(out);
	}

	private static void tieredAugmentRecipes(
			@NotNull RecipeOutput out,
			TieredAugmentType type,
			BiFunction<@NotNull AugmentRecipeBuilder, @NotNull Integer, AugmentRecipeBuilder> function) {
		tieredAugmentRecipes(out, type, function, null);
	}

	private static void tieredAugmentRecipes(
			@NotNull RecipeOutput out,
			TieredAugmentType type,
			BiFunction<@NotNull AugmentRecipeBuilder, @NotNull Integer, @Nullable AugmentRecipeBuilder> function,
			@Nullable IntFunction<@Nullable String> recipeName) {
		for (int i = 0; i < type.tiers(); i++) {
			AugmentRecipeBuilder b = function.apply(augment(), i);
			if (b == null) continue;

			for (int j = 0; j < type.tiers(); j++) {
				if (j == i) b.addAugment(type.get(j));
				else if (j == i - 1) b.removeAugment(type.get(j));
				else b.incompatible(type.get(j));
			}

			String customName = recipeName != null ? recipeName.apply(i) : null;
			if (customName != null) b.save(out, customName);
			else b.save(out);
		}
	}
}
