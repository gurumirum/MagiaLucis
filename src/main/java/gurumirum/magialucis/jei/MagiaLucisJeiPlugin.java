package gurumirum.magialucis.jei;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.Wands;
import gurumirum.magialucis.impl.InWorldBeamCraftingManager;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class MagiaLucisJeiPlugin implements IModPlugin{
	public static final RecipeType<BeamCraftingRecipeCategory.BeamCraftingRecipe> BEAM_RECIPE_TYPE = new RecipeType<>(MagiaLucisMod.id("beam_crafting"), BeamCraftingRecipeCategory.BeamCraftingRecipe.class);


	@Override public @NotNull ResourceLocation getPluginUid(){
		return MagiaLucisMod.id("jei_plugin");
	}

	@Override public void registerRecipes(IRecipeRegistration registration){
		List<BeamCraftingRecipeCategory.BeamCraftingRecipe> recipes = new ArrayList<>();
		InWorldBeamCraftingManager.getRecipes().forEach((state, recipe) -> {
			if (state.getBlock().asItem() != Items.AIR)
				recipes.add(new BeamCraftingRecipeCategory.BeamCraftingRecipe(state, recipe));
		});
		registration.addRecipes(
				BEAM_RECIPE_TYPE, recipes
		);
	}

	@Override public void registerCategories(IRecipeCategoryRegistration registration){
		IJeiHelpers jeiHelpers = registration.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		registration.addRecipeCategories(
				new BeamCraftingRecipeCategory(guiHelper)
		);
	}

	@Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration){
		registration.addRecipeCatalyst(Wands.ANCIENT_LIGHT, BEAM_RECIPE_TYPE);
	}
}
