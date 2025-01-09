package gurumirum.magialucis.jei;

import gurumirum.magialucis.contents.Wands;
import gurumirum.magialucis.impl.InWorldBeamCraftingManager;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BeamCraftingRecipeCategory implements IRecipeCategory<BeamCraftingRecipeCategory.BeamCraftingRecipe> {
	private final IDrawable icon;

	public BeamCraftingRecipeCategory(IGuiHelper guiHelper){
		icon = guiHelper.createDrawableItemLike(Wands.ANCIENT_LIGHT);
	}

	@Override public @NotNull RecipeType<BeamCraftingRecipe> getRecipeType(){
		return MagiaLucisJeiPlugin.BEAM_RECIPE_TYPE;
	}

	@Override public @NotNull Component getTitle(){
		return Component.translatable("jei.magialucis.beam_crafting");
	}

	@Override public @Nullable IDrawable getIcon(){
		return icon;
	}

	@Override public void setRecipe(IRecipeLayoutBuilder builder, BeamCraftingRecipe recipe, @NotNull IFocusGroup focuses){
		builder.addInputSlot(1, 9)
				.setStandardSlotBackground()
				.addItemLike(recipe.input.getBlock());

		builder.addOutputSlot(61,  9)
				.setOutputSlotBackground()
				.addItemStacks(recipe.recipe.output());
	}

	@Override public void createRecipeExtras(IRecipeExtrasBuilder builder, BeamCraftingRecipe recipe, @NotNull IFocusGroup focuses){
		builder.addRecipeArrow().setPosition(26, 9);
		double cookTimeSeconds = Math.floor((recipe.recipe.processTicks() / 20.0) * 100)/100;

		Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", cookTimeSeconds);
		builder.addText(timeString, getWidth() - 20, 10)
				.setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM)
				.setTextAlignment(HorizontalAlignment.RIGHT)
				.setTextAlignment(VerticalAlignment.BOTTOM)
				.setColor(0xFF808080);
	}
	@Override public int getWidth(){
		return 82;
	}

	@Override public int getHeight(){
		return 45;
	}
	public record BeamCraftingRecipe(BlockState input, InWorldBeamCraftingManager.Recipe recipe){}
}
