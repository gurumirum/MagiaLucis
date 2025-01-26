package gurumirum.magialucis.jei;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.recipe.transfusion.LightBasinRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LightBasinRecipeCategory implements IRecipeCategory<LightBasinRecipe> {
	public static final RecipeType<LightBasinRecipe> RECIPE_TYPE = new RecipeType<>(MagiaLucisMod.id("light_basin"), LightBasinRecipe.class);

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
			IRecipeSlotBuilder inputSlot = builder.addInputSlot(1 + 18 * (i % 2), 9 + 18 * (i / 2))
					.setStandardSlotBackground();

			if (i < recipe.ingredients().size()) {
				inputSlot.addIngredients(VanillaTypes.ITEM_STACK, recipe.ingredients().get(i).toItemList());
			}
		}

		builder.addOutputSlot(61, 9)
				.setOutputSlotBackground()
				.addItemStack(recipe.result());
	}

	@Override
	public void createRecipeExtras(IRecipeExtrasBuilder builder, LightBasinRecipe recipe, @NotNull IFocusGroup focuses) {
		builder.addRecipeArrow()
				.setPosition(26, 9);

		double cookTimeSeconds = Math.floor((recipe.processTicks() / 20.0) * 100) / 100;

		Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", cookTimeSeconds);
		builder.addText(timeString, getWidth() - 20, 10)
				.setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM)
				.setTextAlignment(HorizontalAlignment.RIGHT)
				.setTextAlignment(VerticalAlignment.BOTTOM)
				.setColor(0xFF808080);

		List<FormattedText> text = format(null,
				recipe.minLuxInputR(),
				recipe.minLuxInputG(),
				recipe.minLuxInputB(),
				recipe.minLuxInputSum(),
				true);
		text = format(text,
				recipe.maxLuxInputR(),
				recipe.maxLuxInputG(),
				recipe.maxLuxInputB(),
				recipe.maxLuxInputSum(),
				false);

		if (text != null) {
			builder.addText(text, getWidth(), getHeight())
					.setTextAlignment(HorizontalAlignment.LEFT)
					.setTextAlignment(VerticalAlignment.BOTTOM)
					.setColor(0xFF808080);
		}
	}

	private @Nullable List<FormattedText> format(@Nullable List<FormattedText> text,
	                                             double red, double green, double blue, double sum, boolean min) {
		boolean rb = min ? red > 0 : red < Double.POSITIVE_INFINITY;
		boolean gb = min ? green > 0 : green < Double.POSITIVE_INFINITY;
		boolean bb = min ? blue > 0 : blue < Double.POSITIVE_INFINITY;
		boolean sb = min ? sum > 0 : sum < Double.POSITIVE_INFINITY;

		if (rb || gb || bb || sb) {
			if (text == null) text = new ArrayList<>();
			text.add(Component.translatable(min ? "Min. LUX Input:" : "Max. LUX Input:"));
			if (rb) text.add(Component.translatable(" R: " + red));
			if (gb) text.add(Component.translatable(" G: " + green));
			if (bb) text.add(Component.translatable(" B: " + blue));
			if (sb) text.add(Component.translatable(" Sum: " + sum));
		}

		return text;
	}

	@Override
	public int getWidth() {
		return 140;
	}

	@Override
	public int getHeight() {
		return 102;
	}
}
