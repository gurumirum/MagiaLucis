package gurumirum.magialucis.jei;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.Accessories;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.ModRecipes;
import gurumirum.magialucis.contents.Wands;
import gurumirum.magialucis.contents.block.artisanrytable.ArtisanryTableScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@JeiPlugin
public class MagiaLucisJeiPlugin implements IModPlugin {
	@Override
	public @NotNull ResourceLocation getPluginUid() {
		return MagiaLucisMod.id("jei_plugin");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IJeiHelpers jeiHelpers = registration.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registration.addRecipeCategories(
				new AncientLightRecipeCategory(guiHelper),
				new LightBasinRecipeCategory(guiHelper),
				new ArtisanryRecipeCategory(guiHelper)
		);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		var recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

		registration.addRecipes(AncientLightRecipeCategory.RECIPE_TYPE, recipeManager
				.getAllRecipesFor(ModRecipes.ANCIENT_LIGHT_TYPE.get()).stream()
				.map(RecipeHolder::value)
				.toList());

		registration.addRecipes(LightBasinRecipeCategory.RECIPE_TYPE, recipeManager
				.getAllRecipesFor(ModRecipes.LIGHT_BASIN_TYPE.get()).stream()
				.map(RecipeHolder::value)
				.toList());

		registration.addRecipes(ArtisanryRecipeCategory.RECIPE_TYPE, recipeManager
				.getAllRecipesFor(ModRecipes.ARTISANRY_TYPE.get()).stream()
				.map(RecipeHolder::value)
				.toList());
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(Wands.ANCIENT_LIGHT, AncientLightRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(ModBlocks.LIGHT_BASIN, LightBasinRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(ModBlocks.ARTISANRY_TABLE, ArtisanryRecipeCategory.RECIPE_TYPE);
	}

	@Override
	public void registerItemSubtypes(@NotNull ISubtypeRegistration registration) {
		for (var i : Wands.values()) {
			if (i.luxContainerStat() != null) {
				registration.registerSubtypeInterpreter(i.asItem(), LuxContainerSubtypeInterpreter.INSTANCE);
			}
		}
		for (Accessories i : Accessories.values()) {
			if (i.luxContainerStat() != null) {
				registration.registerSubtypeInterpreter(i.asItem(), LuxContainerSubtypeInterpreter.INSTANCE);
			}
		}
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addRecipeClickArea(ArtisanryTableScreen.class, 140, 66, 26, 16,
				ArtisanryRecipeCategory.RECIPE_TYPE);
	}

	@Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(new ArtisanryRecipeTransferInfo());
	}
}
