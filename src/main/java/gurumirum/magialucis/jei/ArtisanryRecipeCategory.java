package gurumirum.magialucis.jei;

import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.recipe.artisanry.SimpleArtisanryRecipe;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArtisanryRecipeCategory extends BaseArtisanryRecipeCategory<SimpleArtisanryRecipe> {
	public static final RecipeType<SimpleArtisanryRecipe> RECIPE_TYPE = new RecipeType<>(
			MagiaLucisApi.id("artisanry"), SimpleArtisanryRecipe.class);

	private final IDrawable icon;

	public ArtisanryRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		this.icon = guiHelper.createDrawableItemLike(ModBlocks.ARTISANRY_TABLE);
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
}
