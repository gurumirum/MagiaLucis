package gurumirum.magialucis.jei;

import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.contents.*;
import gurumirum.magialucis.contents.block.artisanrytable.ArtisanryTableScreen;
import gurumirum.magialucis.contents.block.lux.lightloom.LightLoomType;
import gurumirum.magialucis.contents.recipe.artisanry.AugmentRecipe;
import gurumirum.magialucis.contents.recipe.artisanry.SimpleArtisanryRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JeiPlugin
public class MagiaLucisJeiPlugin implements IModPlugin {
	@Override
	public @NotNull ResourceLocation getPluginUid() {
		return MagiaLucisApi.id("jei_plugin");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IJeiHelpers jeiHelpers = registration.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registration.addRecipeCategories(
				new AncientLightRecipeCategory(guiHelper),
				new LightBasinRecipeCategory(guiHelper),
				new ArtisanryRecipeCategory(guiHelper),
				new AugmentRecipeCategory(guiHelper)
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
				.map(r -> r instanceof SimpleArtisanryRecipe r_ ? r_ : null)
				.filter(Objects::nonNull)
				.toList());

		registration.addRecipes(AugmentRecipeCategory.RECIPE_TYPE, recipeManager
				.getAllRecipesFor(ModRecipes.ARTISANRY_TYPE.get()).stream()
				.map(RecipeHolder::value)
				.map(r -> r instanceof AugmentRecipe r_ ? r_ : null)
				.filter(Objects::nonNull)
				.toList());
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(Wands.ANCIENT_LIGHT, AncientLightRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(ModBlocks.LIGHT_BASIN, LightBasinRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(ModBlocks.ARTISANRY_TABLE, ArtisanryRecipeCategory.RECIPE_TYPE, AugmentRecipeCategory.RECIPE_TYPE);
		for (LightLoomType lightLoomType : LightLoomType.values()) {
			registration.addRecipeCatalyst(lightLoomType.item(), AugmentRecipeCategory.RECIPE_TYPE);
		}
	}

	@Override
	public void registerItemSubtypes(@NotNull ISubtypeRegistration registration) {
		registration.registerSubtypeInterpreter(ModBlocks.RELAY.asItem(), GemContainerSubtypeInterpreter.INSTANCE);
		registration.registerSubtypeInterpreter(ModBlocks.SPLITTER.asItem(), GemContainerSubtypeInterpreter.INSTANCE);
		registration.registerSubtypeInterpreter(ModBlocks.CONNECTOR.asItem(), GemContainerSubtypeInterpreter.INSTANCE);
	}

	@Override
	public void registerExtraIngredients(@NotNull IExtraIngredientRegistration registration) {
		List<ItemStack> stacks = new ArrayList<>();
		for (var i : Wands.values()) {
			if (i.luxContainerStat() != null) {
				ItemStack stack = new ItemStack(i.asItem());
				stack.set(ModDataComponents.LUX_CHARGE, i.luxContainerStat().maxCharge());
				stacks.add(stack);
			}
		}
		for (Accessories i : Accessories.values()) {
			if (i.luxContainerStat() != null) {
				ItemStack stack = new ItemStack(i.asItem());
				stack.set(ModDataComponents.LUX_CHARGE, i.luxContainerStat().maxCharge());
				stacks.add(stack);
			}
		}
		registration.addExtraItemStacks(stacks);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addRecipeClickArea(ArtisanryTableScreen.class, 140, 66, 26, 16,
				ArtisanryRecipeCategory.RECIPE_TYPE, AugmentRecipeCategory.RECIPE_TYPE);
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(new ArtisanryRecipeTransferInfo<>(ArtisanryRecipeCategory.RECIPE_TYPE));
		registration.addRecipeTransferHandler(new ArtisanryRecipeTransferInfo<>(AugmentRecipeCategory.RECIPE_TYPE));
	}
}
