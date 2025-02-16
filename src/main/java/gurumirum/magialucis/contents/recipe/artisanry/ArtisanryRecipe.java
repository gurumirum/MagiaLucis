package gurumirum.magialucis.contents.recipe.artisanry;

import gurumirum.magialucis.contents.ModRecipes;
import gurumirum.magialucis.contents.recipe.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface ArtisanryRecipe extends Recipe<ArtisanryRecipeInput> {
	InputPatternSpec<RegistryFriendlyByteBuf, IngredientStack> GRID_SPEC = new InputPatternSpec<>(
			3, 3, IngredientStack.EMPTY, true,
			() -> IngredientStack.CODEC, () -> IngredientStack.STREAM_CODEC);

	InputPatternSpec<RegistryFriendlyByteBuf, IngredientStack> GRID_UNOPTIMIZED_SPEC = new InputPatternSpec<>(
			3, 3, IngredientStack.EMPTY, false,
			() -> IngredientStack.CODEC, () -> IngredientStack.STREAM_CODEC);

	@NotNull InputPattern<IngredientStack> pattern();
	int processTicks();
	@NotNull LuxInputCondition luxInputCondition();

	@NotNull LuxRecipeEvaluation evaluate(@NotNull ArtisanryRecipeInput input);

	@Deprecated
	@Override
	default boolean matches(@NotNull ArtisanryRecipeInput input, @NotNull Level level) {
		return evaluate(input).isSuccess();
	}

	@Deprecated
	@Override
	default @NotNull ItemStack assemble(@NotNull ArtisanryRecipeInput input, HolderLookup.@NotNull Provider registries) {
		return evaluate(input).result();
	}

	@Override
	default @NotNull RecipeType<?> getType() {
		return ModRecipes.ARTISANRY_TYPE.get();
	}
}
