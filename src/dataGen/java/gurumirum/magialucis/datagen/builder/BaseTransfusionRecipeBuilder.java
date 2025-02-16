package gurumirum.magialucis.datagen.builder;

import gurumirum.magialucis.api.capability.LuxStat;
import gurumirum.magialucis.contents.recipe.IngredientStack;
import gurumirum.magialucis.contents.recipe.transfusion.TransfusionRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class BaseTransfusionRecipeBuilder<B extends BaseTransfusionRecipeBuilder<B>>
		extends ModRecipeBuilder<TransfusionRecipe> {
	protected final List<IngredientStack> ingredients = new ArrayList<>();
	protected ItemStack result = ItemStack.EMPTY;
	protected int processTicks;
	protected final LuxInputConditionBuilder luxInput = new LuxInputConditionBuilder();

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

	public B luxInput(Consumer<LuxInputConditionBuilder> consumer) {
		consumer.accept(this.luxInput);
		return self();
	}

	@Override
	protected @Nullable ResourceLocation defaultRecipeId() {
		return getId(this.result.getItem()).withPrefix(defaultRecipePrefix());
	}

	@Override
	protected void ensureValid(ResourceLocation id) {
		if (this.result.isEmpty()) throw new IllegalStateException("Transfusion recipe " + id + " has no result");
		if (this.ingredients.isEmpty())
			throw new IllegalStateException("Transfusion recipe " + id + " has no ingredients");
		if (this.processTicks <= 0)
			throw new IllegalStateException("Transfusion recipe " + id + " has no process ticks");
		this.luxInput.validate(id, absoluteMaxLuxInput());
	}

	protected abstract @Nullable LuxStat absoluteMaxLuxInput();
}
