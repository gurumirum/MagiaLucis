package gurumirum.magialucis.datagen.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gurumirum.magialucis.api.augment.Augment;
import gurumirum.magialucis.contents.recipe.IngredientStack;
import gurumirum.magialucis.contents.recipe.artisanry.ArtisanryRecipe;
import gurumirum.magialucis.contents.recipe.artisanry.AugmentRecipe;
import gurumirum.magialucis.utils.AugmentProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class AugmentRecipeBuilder extends ModRecipeBuilder<AugmentRecipe> {
	private final List<String> rows = Lists.newArrayList();
	private final Map<Character, IngredientStack> key = Maps.newLinkedHashMap();
	private final List<AugmentRecipe.AugmentOp> augments = new ArrayList<>();
	private HolderSet<Augment> precursor = HolderSet.empty();
	private HolderSet<Augment> incompatible = HolderSet.empty();
	private int processTicks = -1;
	private final LuxInputConditionBuilder luxInput = new LuxInputConditionBuilder();

	public AugmentRecipeBuilder define(Character symbol, TagKey<Item> tag) {
		return define(symbol, tag, 1);
	}

	public AugmentRecipeBuilder define(Character symbol, ItemLike item) {
		return define(symbol, item, 1);
	}

	public AugmentRecipeBuilder define(Character symbol, Ingredient ingredient) {
		return define(symbol, ingredient, 1);
	}

	public AugmentRecipeBuilder define(Character symbol, TagKey<Item> tag, int count) {
		return define(symbol, Ingredient.of(tag), count);
	}

	public AugmentRecipeBuilder define(Character symbol, ItemLike item, int count) {
		return define(symbol, Ingredient.of(item), count);
	}

	public AugmentRecipeBuilder define(Character symbol, Ingredient ingredient, int count) {
		if (this.key.containsKey(symbol)) {
			throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
		} else if (symbol == ' ') {
			throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		} else {
			this.key.put(symbol, new IngredientStack(ingredient, count));
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

	public AugmentRecipeBuilder addAugment(AugmentProvider augmentProvider) {
		return addAugment(augmentProvider.augment());
	}

	public AugmentRecipeBuilder addAugment(Holder<Augment> augment) {
		return addAugment(augment, false);
	}

	public AugmentRecipeBuilder addAugment(AugmentProvider augmentProvider, boolean optional) {
		return addAugment(augmentProvider.augment(), optional);
	}

	public AugmentRecipeBuilder addAugment(Holder<Augment> augment, boolean optional) {
		this.augments.add(new AugmentRecipe.AugmentOp(augment, false, optional));
		return this;
	}

	public AugmentRecipeBuilder removeAugment(AugmentProvider augmentProvider) {
		return removeAugment(augmentProvider.augment());
	}

	public AugmentRecipeBuilder removeAugment(Holder<Augment> augment) {
		return removeAugment(augment, false);
	}

	public AugmentRecipeBuilder removeAugment(AugmentProvider augmentProvider, boolean optional) {
		return removeAugment(augmentProvider.augment(), optional);
	}

	public AugmentRecipeBuilder removeAugment(Holder<Augment> augment, boolean optional) {
		this.augments.add(new AugmentRecipe.AugmentOp(augment, true, optional));
		return this;
	}

	public AugmentRecipeBuilder precursor(AugmentProvider... augmentProviders) {
		return precursor(HolderSet.direct(AugmentProvider::augment, augmentProviders));
	}

	@SafeVarargs
	public final AugmentRecipeBuilder precursor(Holder<Augment>... augments) {
		return precursor(HolderSet.direct(augments));
	}

	public AugmentRecipeBuilder precursor(HolderSet<Augment> precursor) {
		this.precursor = precursor;
		return this;
	}

	public AugmentRecipeBuilder incompatible(AugmentProvider... augmentProviders) {
		return incompatible(HolderSet.direct(AugmentProvider::augment, augmentProviders));
	}

	@SafeVarargs
	public final AugmentRecipeBuilder incompatible(Holder<Augment>... augments) {
		return incompatible(HolderSet.direct(augments));
	}

	public AugmentRecipeBuilder incompatible(HolderSet<Augment> incompatible) {
		this.incompatible = incompatible;
		return this;
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
				ArtisanryRecipe.GRID_UNOPTIMIZED_SPEC.unpack(this.key, this.rows), this.augments,
				this.precursor, this.incompatible,
				this.processTicks, this.luxInput.build());
	}

	@Override
	protected @Nullable ResourceLocation defaultRecipeId() {
		AugmentRecipe.AugmentOp augmentAdded = null;
		for (AugmentRecipe.AugmentOp augment : this.augments) {
			if (!augment.optional() && !augment.remove()) {
				if (augmentAdded != null) return null;
				augmentAdded = augment;
			}
		}
		return augmentAdded != null ?
				Objects.requireNonNull(augmentAdded.augment().getKey()).location().withPrefix(defaultRecipePrefix()) :
				null;
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
