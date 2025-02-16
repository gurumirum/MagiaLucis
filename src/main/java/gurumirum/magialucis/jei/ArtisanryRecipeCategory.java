package gurumirum.magialucis.jei;

import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.recipe.IngredientStack;
import gurumirum.magialucis.contents.recipe.InputPattern;
import gurumirum.magialucis.contents.recipe.LuxInputCondition;
import gurumirum.magialucis.contents.recipe.artisanry.SimpleArtisanryRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArtisanryRecipeCategory implements IRecipeCategory<SimpleArtisanryRecipe> {
	public static final RecipeType<SimpleArtisanryRecipe> RECIPE_TYPE = new RecipeType<>(
			MagiaLucisApi.id("artisanry"), SimpleArtisanryRecipe.class);

	private final IDrawable icon;
	private final ICraftingGridHelper craftingGridHelper;

	public ArtisanryRecipeCategory(IGuiHelper guiHelper) {
		this.icon = guiHelper.createDrawableItemLike(ModBlocks.ARTISANRY_TABLE);
		this.craftingGridHelper = guiHelper.createCraftingGridHelper();
	}

	@Override
	public @NotNull RecipeType<SimpleArtisanryRecipe> getRecipeType() {
		return RECIPE_TYPE;
	}

	@Override
	public @NotNull Component getTitle() {
		return Component.translatable("jei.magialucis.artisanry");
	}

	@Override
	public @Nullable IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public int getWidth() {
		return 116;
	}

	@Override
	public int getHeight() {
		return 54;
	}

	@Override
	public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull SimpleArtisanryRecipe recipe, @NotNull IFocusGroup focuses) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		if (level == null) throw new NullPointerException("level must not be null.");
		this.craftingGridHelper.createAndSetOutputs(builder, List.of(recipe.getResultItem(level.registryAccess())));
		InputPattern<IngredientStack> pattern = recipe.pattern();
		this.craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, pattern.inputs().stream()
				.map(IngredientStack::toItemList)
				.toList(), 3, 3);
	}

	@Override
	public void createRecipeExtras(@NotNull IRecipeExtrasBuilder builder, @NotNull SimpleArtisanryRecipe recipe, @NotNull IFocusGroup focuses) {
		if (recipe.processTicks() > 0) {
			builder.addAnimatedRecipeArrow(recipe.processTicks())
					.setPosition(61, 19);
			JeiLogic.processTimeWidget(this, builder, recipe.processTicks());
		} else {
			builder.addRecipeArrow()
					.setPosition(61, 19);
		}

		LuxInputCondition luxInputCondition = recipe.luxInputCondition();
		boolean usesLux = !luxInputCondition.equals(LuxInputCondition.none());
		if (usesLux) {
			builder.addWidget(new LuxInputWidget(luxInputCondition, 64, 2));
		}
	}
}
