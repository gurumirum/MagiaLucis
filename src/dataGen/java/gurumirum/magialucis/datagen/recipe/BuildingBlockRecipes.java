package gurumirum.magialucis.datagen.recipe;

import gurumirum.magialucis.contents.ModItemTags;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import static gurumirum.magialucis.MagiaLucisMod.id;
import static gurumirum.magialucis.contents.ModBuildingBlocks.*;
import static net.minecraft.data.recipes.RecipeCategory.BUILDING_BLOCKS;
import static net.minecraft.data.recipes.ShapedRecipeBuilder.shaped;
import static net.minecraft.data.recipes.ShapelessRecipeBuilder.shapeless;

public abstract class BuildingBlockRecipes extends SharedRecipeLogic {
	public static void add(@NotNull RecipeOutput out) {
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
				.save(out, id("lapis_manalis_revert"));

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
}
