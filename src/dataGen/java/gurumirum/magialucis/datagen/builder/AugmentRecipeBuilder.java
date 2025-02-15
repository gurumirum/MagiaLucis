package gurumirum.magialucis.datagen.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gurumirum.magialucis.contents.data.Augment;
import gurumirum.magialucis.contents.recipe.artisanry.AugmentRecipe;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class AugmentRecipeBuilder extends ModRecipeBuilder<AugmentRecipe> {
	private final Holder<Augment> augment;
	private final List<String> rows = Lists.newArrayList();
	private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
	protected int processTicks = -1;
	protected final LuxInputConditionBuilder luxInput = new LuxInputConditionBuilder();

	public AugmentRecipeBuilder(Holder<Augment> augment) {
		this.augment = Objects.requireNonNull(augment);
	}

	public AugmentRecipeBuilder define(Character symbol, TagKey<Item> tag) {
		return this.define(symbol, Ingredient.of(tag));
	}

	public AugmentRecipeBuilder define(Character symbol, ItemLike item) {
		return this.define(symbol, Ingredient.of(item));
	}

	public AugmentRecipeBuilder define(Character symbol, Ingredient ingredient) {
		if (this.key.containsKey(symbol)) {
			throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
		} else if (symbol == ' ') {
			throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		} else {
			this.key.put(symbol, ingredient);
			return this;
		}
	}

	public AugmentRecipeBuilder pattern(String pattern) {
		if (!this.rows.isEmpty() && pattern.length() != this.rows.getFirst().length()) {
			throw new IllegalArgumentException("Pattern must be the same width on every line!");
		} else {
			this.rows.add(pattern);
			return this;
		}
	}

	public AugmentRecipeBuilder instant() {
		this.processTicks = 0;
		return this;
	}

	public AugmentRecipeBuilder processTicks(int processTicks) {
		this.processTicks = processTicks;
		return this;
	}

	public AugmentRecipeBuilder luxInput(Consumer<LuxInputConditionBuilder> consumer) {
		consumer.accept(this.luxInput);
		return this;
	}

	@Override
	protected AugmentRecipe createRecipeInstance() {
		return new AugmentRecipe(
				ShapedRecipePattern.of(this.key, this.rows), this.augment,
				this.processTicks, this.luxInput.build());
	}

	@Override
	protected @Nullable ResourceLocation defaultRecipeId() {
		return Objects.requireNonNull(this.augment.getKey()).location().withPrefix(defaultRecipePrefix());
	}

	@Override
	protected @NotNull String defaultRecipePrefix() {
		return "artisanry/augment/";
	}

	@Override
	protected void ensureValid(ResourceLocation id) {
		if (this.processTicks < 0)
			throw new IllegalStateException("Augment recipe " + id + " has no process ticks");
		this.luxInput.validate(id, null);
	}
}
