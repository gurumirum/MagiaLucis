package gurumirum.magialucis.datagen.recipe;

import gurumirum.magialucis.contents.GemItems;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import static gurumirum.magialucis.contents.ModBuildingBlocks.LAPIS_MANALIS;

public abstract class AncientLightRecipes extends SharedRecipeLogic {
	public static void add(@NotNull RecipeOutput out) {
		ancientLight()
				.block(Blocks.SAND)
				.processTicks(25)
				.result(GemItems.BRIGHTSTONE)
				.save(out);

		ancientLight()
				.block(Blocks.RED_SAND)
				.processTicks(25)
				.result(GemItems.RED_BRIGHTSTONE)
				.save(out);

		ancientLight()
				.block(Blocks.ICE)
				.block(Blocks.FROSTED_ICE)
				.block(Blocks.PACKED_ICE)
				.block(Blocks.BLUE_ICE)
				.processTicks(25)
				.result(GemItems.ICY_BRIGHTSTONE)
				.save(out);

		ancientLight()
				.block(Blocks.SOUL_SAND)
				.processTicks(50)
				.result(GemItems.SOUL_BRIGHTSTONE)
				.save(out);

		ancientLight()
				.block(Blocks.STONE)
				.block(Blocks.DEEPSLATE)
				.processTicks(50)
				.result(LAPIS_MANALIS)
				.save(out);
	}
}
