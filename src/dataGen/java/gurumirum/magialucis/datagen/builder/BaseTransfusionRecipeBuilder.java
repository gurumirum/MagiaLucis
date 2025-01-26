package gurumirum.magialucis.datagen.builder;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.recipe.IngredientStack;
import gurumirum.magialucis.contents.recipe.transfusion.TransfusionRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseTransfusionRecipeBuilder<B extends BaseTransfusionRecipeBuilder<B>> {
	protected final List<IngredientStack> ingredients = new ArrayList<>();

	protected ItemStack result = ItemStack.EMPTY;

	protected int processTicks;
	protected double minLuxInputR = 0;
	protected double minLuxInputG = 0;
	protected double minLuxInputB = 0;
	protected double minLuxInputSum = 0;
	protected double maxLuxInputR = Double.POSITIVE_INFINITY;
	protected double maxLuxInputG = Double.POSITIVE_INFINITY;
	protected double maxLuxInputB = Double.POSITIVE_INFINITY;
	protected double maxLuxInputSum = Double.POSITIVE_INFINITY;

	@SuppressWarnings("unchecked")
	private B self() {
		return (B)this;
	}

	public B result(ItemLike item) {
		this.result = new ItemStack(item);
		return self();
	}

	public B result(ItemLike item, int count) {
		this.result = new ItemStack(item, count);
		return self();
	}

	public B result(ItemStack stack) {
		this.result = stack;
		return self();
	}

	public B ingredient(ItemLike item) {
		return ingredient(Ingredient.of(item));
	}

	public B ingredient(ItemLike item, int count) {
		return ingredient(Ingredient.of(item), count);
	}

	public B ingredient(TagKey<Item> tag) {
		return ingredient(Ingredient.of(tag));
	}

	public B ingredient(TagKey<Item> tag, int count) {
		return ingredient(Ingredient.of(tag), count);
	}

	public B ingredient(Ingredient ingredient) {
		this.ingredients.add(new IngredientStack(ingredient, 1));
		return self();
	}

	public B ingredient(Ingredient ingredient, int count) {
		if (count <= 0) throw new IllegalArgumentException("count < 0");
		this.ingredients.add(new IngredientStack(ingredient, count));
		return self();
	}

	public B processTicks(int processTicks) {
		this.processTicks = processTicks;
		return self();
	}

	public B minLuxInputs(double r, double g, double b) {
		this.minLuxInputR = r;
		this.minLuxInputG = g;
		this.minLuxInputB = b;
		return self();
	}

	public B maxLuxInputs(double r, double g, double b) {
		this.maxLuxInputR = r;
		this.maxLuxInputG = g;
		this.maxLuxInputB = b;
		return self();
	}

	public B minLuxInputR(double value) {
		this.minLuxInputR = value;
		return self();
	}

	public B minLuxInputG(double value) {
		this.minLuxInputG = value;
		return self();
	}

	public B minLuxInputB(double value) {
		this.minLuxInputB = value;
		return self();
	}

	public B minLuxInputSum(double value) {
		this.minLuxInputSum = value;
		return self();
	}

	public B maxLuxInputR(double value) {
		this.maxLuxInputR = value;
		return self();
	}

	public B maxLuxInputG(double value) {
		this.maxLuxInputG = value;
		return self();
	}

	public B maxLuxInputB(double value) {
		this.maxLuxInputB = value;
		return self();
	}

	public B maxLuxInputSum(double value) {
		this.maxLuxInputSum = value;
		return self();
	}

	public void save(@NotNull RecipeOutput recipeOutput) {
		save(recipeOutput, getDefaultRecipeId(this.result.getItem()));
	}

	public void save(@NotNull RecipeOutput recipeOutput, @NotNull String id) {
		save(recipeOutput, MagiaLucisMod.id(defaultRecipePrefix() + id));
	}

	public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceLocation id) {
		ensureValid(id);
		recipeOutput.accept(id, createInstance(), null);
	}

	protected abstract TransfusionRecipe createInstance();

	protected void ensureValid(ResourceLocation id) {
		if (this.result.isEmpty()) throw new IllegalStateException("Transfusion recipe " + id + " has no result");
		if (this.ingredients.isEmpty())
			throw new IllegalStateException("Transfusion recipe " + id + " has no ingredients");
		if (this.processTicks <= 0)
			throw new IllegalStateException("Transfusion recipe " + id + " has no process ticks");
		validateDoubleValue(id, this.minLuxInputR, this.maxLuxInputR, "R", absoluteMaxLuxInputR());
		validateDoubleValue(id, this.minLuxInputG, this.maxLuxInputG, "G", absoluteMaxLuxInputG());
		validateDoubleValue(id, this.minLuxInputB, this.maxLuxInputB, "B", absoluteMaxLuxInputB());
		validateDoubleValue(id, this.minLuxInputSum, this.maxLuxInputSum, "Sum", absoluteMaxLuxInputSum());
	}

	protected void validateDoubleValue(ResourceLocation id, double min, double max, String componentName, double absMax) {
		if (min < 0 || Double.isNaN(min))
			throw new IllegalStateException("Transfusion recipe " + id + " has invalid minLuxInput" + componentName + " value");
		if (max < 0 || Double.isNaN(max))
			throw new IllegalStateException("Transfusion recipe " + id + " has invalid minLuxInput" + componentName + " value");
		if (min > max)
			throw new IllegalStateException("Transfusion recipe " + id + " has minLuxInput" + componentName + " value greater than maxLuxInput" + componentName);
		if (min > absMax)
			throw new IllegalStateException("Transfusion recipe " + id + " has minLuxInput" + componentName + " value greater than absolute max value of " + absMax);
		if (max != Double.POSITIVE_INFINITY && max > absMax)
			throw new IllegalStateException("Transfusion recipe " + id + " has maxLuxInput" + componentName + " value greater than absolute max value of " + absMax);
	}

	private ResourceLocation getDefaultRecipeId(ItemLike item) {
		return BuiltInRegistries.ITEM.getKey(item.asItem()).withPrefix(defaultRecipePrefix());
	}

	protected abstract String defaultRecipePrefix();

	protected abstract double absoluteMaxLuxInputR();
	protected abstract double absoluteMaxLuxInputG();
	protected abstract double absoluteMaxLuxInputB();

	protected double absoluteMaxLuxInputSum() {
		return absoluteMaxLuxInputR() + absoluteMaxLuxInputG() + absoluteMaxLuxInputB();
	}
}
