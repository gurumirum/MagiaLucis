package gurumirum.magialucis.jei;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.Accessories;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.ModRecipes;
import gurumirum.magialucis.contents.Wands;
import gurumirum.magialucis.impl.ancientlight.AncientLightCrafting;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
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
				new LightBasinRecipeCategory(guiHelper)
		);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		List<AncientLightRecipeCategory.Recipe> ancientLightRecipes = new ArrayList<>();

		AncientLightCrafting.getRecipes().forEach((state, recipe) -> {
			if (state.getBlock().asItem() != Items.AIR)
				ancientLightRecipes.add(new AncientLightRecipeCategory.Recipe(state, recipe));
		});

		registration.addRecipes(AncientLightRecipeCategory.RECIPE_TYPE, ancientLightRecipes);


		var recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

		registration.addRecipes(LightBasinRecipeCategory.RECIPE_TYPE, recipeManager
				.getAllRecipesFor(ModRecipes.LIGHT_BASIN_TYPE.get()).stream()
				.map(RecipeHolder::value)
				.toList());
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(Wands.ANCIENT_LIGHT, AncientLightRecipeCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(ModBlocks.LIGHT_BASIN, LightBasinRecipeCategory.RECIPE_TYPE);
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
}
