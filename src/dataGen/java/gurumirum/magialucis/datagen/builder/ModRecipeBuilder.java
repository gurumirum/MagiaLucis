package gurumirum.magialucis.datagen.builder;

import gurumirum.magialucis.MagiaLucisMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ModRecipeBuilder<R extends Recipe<?>> {
	protected abstract R createRecipeInstance();
	protected abstract @Nullable ResourceLocation defaultRecipeId();
	protected abstract @NotNull String defaultRecipePrefix();

	public void save(@NotNull RecipeOutput recipeOutput) {
		ResourceLocation defaultRecipeId = defaultRecipeId();
		if (defaultRecipeId == null) {
			throw new IllegalStateException("Cannot infer default recipe ID, specify the ID");
		}
		save(recipeOutput, defaultRecipeId);
	}

	public void save(@NotNull RecipeOutput recipeOutput, @NotNull String id) {
		save(recipeOutput, MagiaLucisMod.id(defaultRecipePrefix() + id));
	}

	public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceLocation id) {
		ensureValid(id);
		recipeOutput.accept(id, createRecipeInstance(), null);
	}

	protected abstract void ensureValid(ResourceLocation id);

	protected static @NotNull ResourceLocation getId(ItemLike item) {
		return BuiltInRegistries.ITEM.getKey(item.asItem());
	}
}
