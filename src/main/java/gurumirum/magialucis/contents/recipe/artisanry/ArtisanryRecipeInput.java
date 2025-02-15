package gurumirum.magialucis.contents.recipe.artisanry;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public interface ArtisanryRecipeInput extends RecipeInput {
	@NotNull ItemStack getCenterStack();

	@NotNull CraftingInput asCraftingInput();
	@NotNull CraftingInput asAugmentInput();
}
