package gurumirum.magialucis.contents.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public final class NoRecipeInput implements RecipeInput {
	private static final NoRecipeInput instance = new NoRecipeInput();

	public static NoRecipeInput of() {
		return instance;
	}

	@Override
	public @NotNull ItemStack getItem(int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public String toString() {
		return "NoRecipeInput";
	}
}
