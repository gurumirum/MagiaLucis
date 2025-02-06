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

public class ArtisanryRecipeBuilder extends ModRecipeBuilder<SimpleArtisanryRecipe> {
	private final ItemStack result;
	private final List<String> rows = Lists.newArrayList();
	private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();

	protected int processTicks = -1;
	protected double minLuxInputR = 0;
	protected double minLuxInputG = 0;
	protected double minLuxInputB = 0;
	protected double minLuxInputSum = 0;
	protected double maxLuxInputR = Double.POSITIVE_INFINITY;
	protected double maxLuxInputG = Double.POSITIVE_INFINITY;
	protected double maxLuxInputB = Double.POSITIVE_INFINITY;
	protected double maxLuxInputSum = Double.POSITIVE_INFINITY;

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

	public ArtisanryRecipeBuilder minLuxInputs(double r, double g, double b) {
		this.minLuxInputR = r;
		this.minLuxInputG = g;
		this.minLuxInputB = b;
		return this;
	}

	public ArtisanryRecipeBuilder maxLuxInputs(double r, double g, double b) {
		this.maxLuxInputR = r;
		this.maxLuxInputG = g;
		this.maxLuxInputB = b;
		return this;
	}

	public ArtisanryRecipeBuilder minLuxInputR(double value) {
		this.minLuxInputR = value;
		return this;
	}

	public ArtisanryRecipeBuilder minLuxInputG(double value) {
		this.minLuxInputG = value;
		return this;
	}

	public ArtisanryRecipeBuilder minLuxInputB(double value) {
		this.minLuxInputB = value;
		return this;
	}

	public ArtisanryRecipeBuilder minLuxInputSum(double value) {
		this.minLuxInputSum = value;
		return this;
	}

	public ArtisanryRecipeBuilder maxLuxInputR(double value) {
		this.maxLuxInputR = value;
		return this;
	}

	public ArtisanryRecipeBuilder maxLuxInputG(double value) {
		this.maxLuxInputG = value;
		return this;
	}

	public ArtisanryRecipeBuilder maxLuxInputB(double value) {
		this.maxLuxInputB = value;
		return this;
	}

	public ArtisanryRecipeBuilder maxLuxInputSum(double value) {
		this.maxLuxInputSum = value;
		return this;
	}

	@Override
	protected SimpleArtisanryRecipe createRecipeInstance() {
		return new SimpleArtisanryRecipe(
				ShapedRecipePattern.of(this.key, this.rows), this.result, this.processTicks,
				this.minLuxInputR, this.minLuxInputG, this.minLuxInputB, this.minLuxInputSum,
				this.maxLuxInputR, this.maxLuxInputG, this.maxLuxInputB, this.maxLuxInputSum);
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
		validateDoubleValue(id, this.minLuxInputR, this.maxLuxInputR, "R");
		validateDoubleValue(id, this.minLuxInputG, this.maxLuxInputG, "G");
		validateDoubleValue(id, this.minLuxInputB, this.maxLuxInputB, "B");
		validateDoubleValue(id, this.minLuxInputSum, this.maxLuxInputSum, "Sum");
	}

	protected void validateDoubleValue(ResourceLocation id, double min, double max, String componentName) {
		if (min < 0 || Double.isNaN(min))
			throw new IllegalStateException("Transfusion recipe " + id + " has invalid minLuxInput" + componentName + " value");
		if (max < 0 || Double.isNaN(max))
			throw new IllegalStateException("Transfusion recipe " + id + " has invalid minLuxInput" + componentName + " value");
		if (min > max)
			throw new IllegalStateException("Transfusion recipe " + id + " has minLuxInput" + componentName + " value greater than maxLuxInput" + componentName);
	}
}
