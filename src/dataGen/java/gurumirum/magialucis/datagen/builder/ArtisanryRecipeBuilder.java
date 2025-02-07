package gurumirum.magialucis.datagen.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gurumirum.magialucis.contents.recipe.artisanry.SimpleArtisanryRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ArtisanryRecipeBuilder extends ModRecipeBuilder<SimpleArtisanryRecipe> {
	private final ItemStack result;
	private final List<String> rows = Lists.newArrayList();
	private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
	protected int processTicks = -1;
	protected final LuxInputConditionBuilder luxInput = new LuxInputConditionBuilder();

	public ArtisanryRecipeBuilder(ItemStack result) {
		this.result = result;
	}

	public ArtisanryRecipeBuilder define(Character symbol, TagKey<Item> tag) {
		return this.define(symbol, Ingredient.of(tag));
	}

	public ArtisanryRecipeBuilder define(Character symbol, ItemLike item) {
		return this.define(symbol, Ingredient.of(item));
	}

	public ArtisanryRecipeBuilder define(Character symbol, Ingredient ingredient) {
		if (this.key.containsKey(symbol)) {
			throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
		} else if (symbol == ' ') {
			throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		} else {
			this.key.put(symbol, ingredient);
			return this;
		}
	}

	public ArtisanryRecipeBuilder pattern(String pattern) {
		if (!this.rows.isEmpty() && pattern.length() != this.rows.getFirst().length()) {
			throw new IllegalArgumentException("Pattern must be the same width on every line!");
		} else {
			this.rows.add(pattern);
			return this;
		}
	}

	public ArtisanryRecipeBuilder instant() {
		this.processTicks = 0;
		return this;
	}

	public ArtisanryRecipeBuilder processTicks(int processTicks) {
		this.processTicks = processTicks;
		return this;
	}

	public ArtisanryRecipeBuilder luxInput(Consumer<LuxInputConditionBuilder> consumer) {
		consumer.accept(this.luxInput);
		return this;
	}

	@Override
	protected SimpleArtisanryRecipe createRecipeInstance() {
		return new SimpleArtisanryRecipe(
				ShapedRecipePattern.of(this.key, this.rows), this.result,
				this.processTicks, this.luxInput.build());
	}

	@Override
	protected @Nullable ResourceLocation defaultRecipeId() {
		return getId(this.result.getItem()).withPrefix(defaultRecipePrefix());
	}

	@Override
	protected @NotNull String defaultRecipePrefix() {
		return "artisanry/";
	}

	@Override
	protected void ensureValid(ResourceLocation id) {
		if (this.result.isEmpty()) throw new IllegalStateException("Transfusion recipe " + id + " has no result");
		if (this.processTicks < 0)
			throw new IllegalStateException("Transfusion recipe " + id + " has no process ticks");
		this.luxInput.validate(id, null);
	}
}
