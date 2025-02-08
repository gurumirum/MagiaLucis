package gurumirum.magialucis.datagen.recipe;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.contents.ModItemTags;
import gurumirum.magialucis.contents.Ore;
import gurumirum.magialucis.datagen.builder.AncientLightRecipeBuilder;
import gurumirum.magialucis.datagen.builder.ArtisanryRecipeBuilder;
import gurumirum.magialucis.datagen.builder.LightBasinRecipeBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static gurumirum.magialucis.MagiaLucisMod.MODID;
import static gurumirum.magialucis.contents.ModItems.RAW_SILVER;

public abstract class SharedRecipeLogic extends RecipeProvider {
	@SuppressWarnings("DataFlowIssue")
	public SharedRecipeLogic() {
		super(null, null);
		throw new IllegalStateException("Don't instantiate this :P");
	}

	protected static Ingredient gem(GemStats gem) {
		if (gem == GemStats.BRIGHTSTONE) return Ingredient.of(ModItemTags.BRIGHTSTONES);
		else if (gem.hasTag()) return Ingredient.of(gem.tag());
		else return Ingredient.of(gem.item());
	}

	protected static Criterion<?> hasGem(GemStats gem) {
		if (gem == GemStats.BRIGHTSTONE) return has(ModItemTags.BRIGHTSTONES);
		else if (gem.hasTag()) return has(gem.tag());
		else return has(gem.item());
	}

	protected static void oreSmelting(
			@NotNull RecipeOutput recipeOutput, List<ItemLike> ingredients, @NotNull RecipeCategory category,
			@NotNull ItemLike result, float experience, int cookingTime, @NotNull String group) {
		oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, ingredients, category, result,
				experience, cookingTime, group, "_from_smelting");
	}

	protected static void oreBlasting(
			@NotNull RecipeOutput recipeOutput, List<ItemLike> ingredients, @NotNull RecipeCategory category,
			@NotNull ItemLike result, float experience, int cookingTime, @NotNull String group) {
		oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, ingredients, category, result,
				experience, cookingTime, group, "_from_blasting");
	}

	protected static <T extends AbstractCookingRecipe> void oreCooking(
			@NotNull RecipeOutput recipeOutput, RecipeSerializer<T> serializer,
			AbstractCookingRecipe.@NotNull Factory<T> recipeFactory, List<ItemLike> ingredients,
			@NotNull RecipeCategory category, @NotNull ItemLike result, float experience, int cookingTime,
			@NotNull String group, String suffix) {
		for (ItemLike itemlike : ingredients) {
			SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), category, result, experience, cookingTime,
							serializer, recipeFactory)
					.group(group)
					.unlockedBy(getHasName(itemlike), has(itemlike))
					.save(recipeOutput, MODID + ":" + getItemName(result) + suffix + "_" + getItemName(itemlike));
		}
	}

	protected static void nineBlockStorageRecipes(
			@NotNull RecipeOutput recipeOutput, @NotNull RecipeCategory unpackedCategory, ItemLike unpacked,
			@NotNull RecipeCategory packedCategory, ItemLike packed) {
		nineBlockStorageRecipes(recipeOutput, unpackedCategory, unpacked, packedCategory, packed,
				MODID + ":" + getSimpleRecipeName(packed), null,
				MODID + ":" + getSimpleRecipeName(unpacked), null
		);
	}

	protected static void nineBlockStorageRecipesWithCustomPacking(
			@NotNull RecipeOutput recipeOutput, @NotNull RecipeCategory unpackedCategory, ItemLike unpacked,
			@NotNull RecipeCategory packedCategory, ItemLike packed, String packedName, @NotNull String packedGroup) {
		nineBlockStorageRecipes(recipeOutput, unpackedCategory, unpacked, packedCategory, packed,
				MODID + ":" + packedName, packedGroup,
				MODID + ":" + getSimpleRecipeName(unpacked), null);
	}

	protected static List<ItemLike> ingredients(Ore ore) {
		List<ItemLike> list = new ArrayList<>();
		if (ore == Ore.SILVER) list.add(RAW_SILVER);

		ore.allOreItems().forEach(list::add);

		return list;
	}

	protected static void stonecutterResultFromBase(
			@NotNull RecipeOutput recipeOutput, @NotNull RecipeCategory category, ItemLike result, ItemLike material) {
		stonecutterResultFromBase(recipeOutput, category, result, material, 1);
	}

	protected static void stonecutterResultFromBase(
			@NotNull RecipeOutput recipeOutput, @NotNull RecipeCategory category, ItemLike result,
			ItemLike material, int resultCount) {
		SingleItemRecipeBuilder.stonecutting(Ingredient.of(material), category, result, resultCount)
				.unlockedBy(getHasName(material), has(material))
				.save(recipeOutput, MODID + ":" + getConversionRecipeName(result, material) + "_stonecutting");
	}

	protected static AncientLightRecipeBuilder ancientLight() {
		return new AncientLightRecipeBuilder();
	}

	protected static LightBasinRecipeBuilder lightBasin() {
		return new LightBasinRecipeBuilder();
	}

	protected static ArtisanryRecipeBuilder artisanry(ItemLike item) {
		return new ArtisanryRecipeBuilder(new ItemStack(item));
	}

	protected static ArtisanryRecipeBuilder artisanry(ItemLike item, int count) {
		return new ArtisanryRecipeBuilder(new ItemStack(item, count));
	}

	protected static ArtisanryRecipeBuilder artisanry(ItemStack stack) {
		return new ArtisanryRecipeBuilder(stack);
	}
}
