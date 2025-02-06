package gurumirum.magialucis.jei;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.recipe.artisanry.ArtisanryRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
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

public class ArtisanryRecipeCategory implements IRecipeCategory<ArtisanryRecipe> {
	public static final RecipeType<ArtisanryRecipe> RECIPE_TYPE = new RecipeType<>(
			MagiaLucisMod.id("artisanry"), ArtisanryRecipe.class);

	private final IDrawable icon;
	private final ICraftingGridHelper craftingGridHelper;

	public ArtisanryRecipeCategory(IGuiHelper guiHelper) {
		this.icon = guiHelper.createDrawableItemLike(ModBlocks.ARTISANRY_TABLE);
		this.craftingGridHelper = guiHelper.createCraftingGridHelper();
	}

	@Override
	public @NotNull RecipeType<ArtisanryRecipe> getRecipeType() {
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
	public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull ArtisanryRecipe recipe, @NotNull IFocusGroup focuses) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		if (level == null) throw new NullPointerException("level must not be null.");
		this.craftingGridHelper.createAndSetOutputs(builder, List.of(recipe.getResultItem(level.registryAccess())));
		this.craftingGridHelper.createAndSetIngredients(builder, recipe.getIngredients(), 3, 3);
	}
}
