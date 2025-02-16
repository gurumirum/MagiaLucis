package gurumirum.magialucis.jei;

import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.recipe.LuxInputCondition;
import gurumirum.magialucis.contents.recipe.transfusion.LightBasinRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LightBasinRecipeCategory implements IRecipeCategory<LightBasinRecipe> {
	public static final RecipeType<LightBasinRecipe> RECIPE_TYPE = new RecipeType<>(MagiaLucisApi.id("light_basin"), LightBasinRecipe.class);

	private final IDrawable icon;

	public LightBasinRecipeCategory(IGuiHelper guiHelper) {
		this.icon = guiHelper.createDrawableItemLike(ModBlocks.LIGHT_BASIN);
	}

	@Override
	public @NotNull RecipeType<LightBasinRecipe> getRecipeType() {
		return RECIPE_TYPE;
	}

	@Override
	public @NotNull Component getTitle() {
		return Component.translatable("jei.magialucis.light_basin");
	}

	@Override
	public @Nullable IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull LightBasinRecipe recipe, @NotNull IFocusGroup focuses) {
		for (int i = 0; i < 4; i++) {
			IRecipeSlotBuilder inputSlot = builder.addInputSlot(1 + 18 * (i % 2), 1 + 18 * (i / 2))
					.setStandardSlotBackground();

			if (i < recipe.ingredients().size()) {
				inputSlot.addIngredients(VanillaTypes.ITEM_STACK, recipe.ingredients().get(i).toItemList());
			}
		}

		builder.addOutputSlot(79, 9)
				.setOutputSlotBackground()
				.addItemStack(recipe.result());
	}

	@Override
	public void createRecipeExtras(@NotNull IRecipeExtrasBuilder builder, LightBasinRecipe recipe, @NotNull IFocusGroup focuses) {
		JeiLogic.processTimeWidget(this, builder, recipe.processTicks());

		LuxInputCondition luxInputCondition = recipe.luxInputCondition();
		boolean usesLux = !luxInputCondition.equals(LuxInputCondition.none());
		if (usesLux) {
			builder.addWidget(new LuxInputWidget(luxInputCondition, 47, 2));
		}

		builder.addAnimatedRecipeArrow(recipe.processTicks())
				.setPosition(44, 19);
	}

	@Override
	public int getWidth() {
		return 100;
	}

	@Override
	public int getHeight() {
		return 48;
	}
}
