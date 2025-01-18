package gurumirum.magialucis.contents.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gurumirum.magialucis.contents.ModRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LightBasinRecipe extends BaseTransfusionRecipe {
	public LightBasinRecipe(ItemStack result, List<IngredientStack> ingredients, int processTicks,
	                        double minLuxInputR, double minLuxInputG, double minLuxInputB, double minLuxInputSum,
	                        double maxLuxInputR, double maxLuxInputG, double maxLuxInputB, double maxLuxInputSum) {
		super(result, ingredients, processTicks,
				minLuxInputR, minLuxInputG, minLuxInputB, minLuxInputSum,
				maxLuxInputR, maxLuxInputG, maxLuxInputB, maxLuxInputSum);
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer() {
		return ModRecipes.LIGHT_BASIN_SERIALIZER.get();
	}

	@Override
	public @NotNull RecipeType<?> getType() {
		return ModRecipes.LIGHT_BASIN_TYPE.get();
	}

	public static final class Serializer implements RecipeSerializer<LightBasinRecipe> {
		private static final MapCodec<LightBasinRecipe> CODEC = RecordCodecBuilder.mapCodec(b ->
				commonFields(b).apply(b, LightBasinRecipe::new));
		private static final StreamCodec<RegistryFriendlyByteBuf, LightBasinRecipe> STREAM_CODEC =
				StreamCodec.of(commonEncoder(), commonDecoder(LightBasinRecipe::new));

		@Override
		public @NotNull MapCodec<LightBasinRecipe> codec() {
			return CODEC;
		}

		@Override
		public @NotNull StreamCodec<RegistryFriendlyByteBuf, LightBasinRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
