package gurumirum.magialucis.contents.recipe.artisanry;

import gurumirum.magialucis.contents.recipe.LuxRecipeEvaluation;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface ArtisanryRecipe extends Recipe<ArtisanryRecipeInput> {
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
}
