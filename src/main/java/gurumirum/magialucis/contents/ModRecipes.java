package gurumirum.magialucis.contents;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.recipe.ancientlight.AncientLightRecipe;
import gurumirum.magialucis.contents.recipe.ancientlight.SimpleAncientLightRecipe;
import gurumirum.magialucis.contents.recipe.artisanry.ArtisanryRecipe;
import gurumirum.magialucis.contents.recipe.artisanry.SimpleArtisanryRecipe;
import gurumirum.magialucis.contents.recipe.transfusion.LightBasinRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModRecipes {
	private ModRecipes() {}

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SimpleAncientLightRecipe>> ANCIENT_LIGHT_SERIALIZER =
			serializer("ancient_light", new SimpleAncientLightRecipe.Serializer());

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LightBasinRecipe>> LIGHT_BASIN_SERIALIZER =
			serializer("light_basin", new LightBasinRecipe.Serializer());

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SimpleArtisanryRecipe>> ARTISANRY_SERIALIZER =
			serializer("artisanry", new SimpleArtisanryRecipe.Serializer());

	public static final DeferredHolder<RecipeType<?>, RecipeType<AncientLightRecipe>> ANCIENT_LIGHT_TYPE = type("ancient_light");
	public static final DeferredHolder<RecipeType<?>, RecipeType<LightBasinRecipe>> LIGHT_BASIN_TYPE = type("light_basin");
	public static final DeferredHolder<RecipeType<?>, RecipeType<ArtisanryRecipe>> ARTISANRY_TYPE = type("artisanry");

	private static <R extends Recipe<?>> DeferredHolder<RecipeSerializer<?>, RecipeSerializer<R>> serializer(String name, RecipeSerializer<R> serializer) {
		return Contents.RECIPE_SERIALIZERS.register(name, () -> serializer);
	}

	private static <R extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<R>> type(String name) {
		return Contents.RECIPE_TYPES.register(name, () -> RecipeType.simple(MagiaLucisMod.id(name)));
	}

	public static void init() {}
}
