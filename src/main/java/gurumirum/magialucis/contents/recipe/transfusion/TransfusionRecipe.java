package gurumirum.magialucis.contents.recipe.transfusion;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface TransfusionRecipe extends Recipe<TransfusionRecipeInput> {
	@NotNull TransfusionRecipeEvaluation evaluate(@NotNull TransfusionRecipeInput input);

	@Deprecated
	@Override
	default boolean matches(@NotNull TransfusionRecipeInput input, @NotNull Level level) {
		return evaluate(input).isSuccess();
	}

	@Deprecated
	@Override
	default @NotNull ItemStack assemble(@NotNull TransfusionRecipeInput input, HolderLookup.@NotNull Provider registries) {
		return evaluate(input).result();
	}

	@Deprecated
	@Override
	default boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Deprecated
	@Override
	default @NotNull NonNullList<Ingredient> getIngredients() {
		return NonNullList.create();
	}
}
