package gurumirum.magialucis.datagen.builder;

import gurumirum.magialucis.contents.recipe.crafting.ShapedRelayCraftingRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShapedRelayCraftingRecipeBuilder extends ShapedRecipeBuilder {
	public ShapedRelayCraftingRecipeBuilder(RecipeCategory category, ItemStack result) {
		super(category, result);
	}
	public ShapedRelayCraftingRecipeBuilder(RecipeCategory category, ItemLike result, int count) {
		super(category, result, count);
	}

	public static ShapedRelayCraftingRecipeBuilder shapedRelayCrafting(RecipeCategory category, ItemLike result) {
		return shapedRelayCrafting(category, result, 1);
	}

	public static ShapedRelayCraftingRecipeBuilder shapedRelayCrafting(RecipeCategory category, ItemLike result, int count) {
		return new ShapedRelayCraftingRecipeBuilder(category, result, count);
	}

	public static ShapedRelayCraftingRecipeBuilder shapedRelayCrafting(RecipeCategory category, ItemStack result) {
		return new ShapedRelayCraftingRecipeBuilder(category, result);
	}

	@Override
	public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceLocation id) {
		super.save(new RecipeOutput() {
			@Override public Advancement.@NotNull Builder advancement() {
				return recipeOutput.advancement();
			}
			@Override
			public void accept(@NotNull ResourceLocation id, @NotNull Recipe<?> recipe,
			                   @Nullable AdvancementHolder advancement, ICondition @NotNull ... conditions) {
				recipeOutput.accept(id, new ShapedRelayCraftingRecipe((ShapedRecipe)recipe), advancement, conditions);
			}
		}, id);
	}
}
