package gurumirum.magialucis.contents.recipe.artisanry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gurumirum.magialucis.contents.ModRecipes;
import gurumirum.magialucis.contents.recipe.ConsumptionRecord;
import gurumirum.magialucis.contents.recipe.LuxRecipeEvaluation;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.jetbrains.annotations.NotNull;

public class SimpleArtisanryRecipe implements ArtisanryRecipe {
	private final ShapedRecipePattern pattern;
	private final ItemStack result;
	private final int processTicks;
	private final double minLuxInputR;
	private final double minLuxInputG;
	private final double minLuxInputB;
	private final double minLuxInputSum;
	private final double maxLuxInputR;
	private final double maxLuxInputG;
	private final double maxLuxInputB;
	private final double maxLuxInputSum;

	public SimpleArtisanryRecipe(ShapedRecipePattern pattern, ItemStack result, int processTicks,
	                             double minLuxInputR, double minLuxInputG, double minLuxInputB, double minLuxInputSum,
	                             double maxLuxInputR, double maxLuxInputG, double maxLuxInputB, double maxLuxInputSum) {
		this.pattern = pattern;
		this.result = result;
		this.processTicks = processTicks;
		this.minLuxInputR = minLuxInputR;
		this.minLuxInputG = minLuxInputG;
		this.minLuxInputB = minLuxInputB;
		this.minLuxInputSum = minLuxInputSum;
		this.maxLuxInputR = maxLuxInputR;
		this.maxLuxInputG = maxLuxInputG;
		this.maxLuxInputB = maxLuxInputB;
		this.maxLuxInputSum = maxLuxInputSum;
	}

	public ShapedRecipePattern pattern() {
		return this.pattern;
	}
	public int processTicks() {
		return this.processTicks;
	}
	public double minLuxInputR() {
		return this.minLuxInputR;
	}
	public double minLuxInputG() {
		return this.minLuxInputG;
	}
	public double minLuxInputB() {
		return this.minLuxInputB;
	}
	public double minLuxInputSum() {
		return this.minLuxInputSum;
	}
	public double maxLuxInputR() {
		return this.maxLuxInputR;
	}
	public double maxLuxInputG() {
		return this.maxLuxInputG;
	}
	public double maxLuxInputB() {
		return this.maxLuxInputB;
	}
	public double maxLuxInputSum() {
		return this.maxLuxInputSum;
	}

	@Override
	public @NotNull LuxRecipeEvaluation evaluate(@NotNull ArtisanryRecipeInput input) {
		if (this.pattern.matches(input.asCraftingInput())) {
			return new LuxRecipeEvaluation(this.result::copy, processTicks(), ConsumptionRecord.consumeAllByOne(input),
					minLuxInputR(), minLuxInputG(), minLuxInputB(), minLuxInputSum(),
					maxLuxInputR(), maxLuxInputG(), maxLuxInputB(), maxLuxInputSum());
		}
		return LuxRecipeEvaluation.fail();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return this.pattern.width() <= width && this.pattern.height() <= height;
	}

	@Override
	public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
		return this.result;
	}

	@Override
	public @NotNull NonNullList<Ingredient> getIngredients() {
		return this.pattern.ingredients();
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer() {
		return ModRecipes.ARTISANRY_SERIALIZER.get();
	}

	@Override
	public @NotNull RecipeType<?> getType() {
		return ModRecipes.ARTISANRY_TYPE.get();
	}

	public static final class Serializer implements RecipeSerializer<SimpleArtisanryRecipe> {
		private static final MapCodec<SimpleArtisanryRecipe> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
				ShapedRecipePattern.MAP_CODEC.fieldOf("pattern").forGetter(r -> r.pattern),
				ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result),
				Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("processTicks", 0).forGetter(SimpleArtisanryRecipe::processTicks),
				Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("minLuxInputR", 0.0).forGetter(SimpleArtisanryRecipe::minLuxInputR),
				Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("minLuxInputG", 0.0).forGetter(SimpleArtisanryRecipe::minLuxInputG),
				Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("minLuxInputB", 0.0).forGetter(SimpleArtisanryRecipe::minLuxInputB),
				Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("minLuxInputSum", 0.0).forGetter(SimpleArtisanryRecipe::minLuxInputSum),
				Codec.doubleRange(0, Double.POSITIVE_INFINITY).optionalFieldOf("maxLuxInputR", Double.POSITIVE_INFINITY).forGetter(SimpleArtisanryRecipe::maxLuxInputR),
				Codec.doubleRange(0, Double.POSITIVE_INFINITY).optionalFieldOf("maxLuxInputG", Double.POSITIVE_INFINITY).forGetter(SimpleArtisanryRecipe::maxLuxInputG),
				Codec.doubleRange(0, Double.POSITIVE_INFINITY).optionalFieldOf("maxLuxInputB", Double.POSITIVE_INFINITY).forGetter(SimpleArtisanryRecipe::maxLuxInputB),
				Codec.doubleRange(0, Double.POSITIVE_INFINITY).optionalFieldOf("maxLuxInputSum", Double.POSITIVE_INFINITY).forGetter(SimpleArtisanryRecipe::maxLuxInputSum)
		).apply(b, SimpleArtisanryRecipe::new));

		private static final StreamCodec<RegistryFriendlyByteBuf, SimpleArtisanryRecipe> STREAM_CODEC = StreamCodec.of(
				(buffer, recipe) -> {
					ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
					ItemStack.STREAM_CODEC.encode(buffer, recipe.result);

					buffer.writeVarInt(recipe.processTicks());

					buffer.writeDouble(recipe.minLuxInputR());
					buffer.writeDouble(recipe.minLuxInputG());
					buffer.writeDouble(recipe.minLuxInputB());
					buffer.writeDouble(recipe.minLuxInputSum());
					buffer.writeDouble(recipe.maxLuxInputR());
					buffer.writeDouble(recipe.maxLuxInputG());
					buffer.writeDouble(recipe.maxLuxInputB());
					buffer.writeDouble(recipe.maxLuxInputSum());
				}, buffer -> {
					ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
					ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);

					int processTicks = buffer.readVarInt();

					double minLuxInputR = buffer.readDouble();
					double minLuxInputG = buffer.readDouble();
					double minLuxInputB = buffer.readDouble();
					double minLuxInputSum = buffer.readDouble();
					double maxLuxInputR = buffer.readDouble();
					double maxLuxInputG = buffer.readDouble();
					double maxLuxInputB = buffer.readDouble();
					double maxLuxInputSum = buffer.readDouble();

					return new SimpleArtisanryRecipe(pattern, result, processTicks,
							minLuxInputR, minLuxInputG, minLuxInputB, minLuxInputSum,
							maxLuxInputR, maxLuxInputG, maxLuxInputB, maxLuxInputSum);
				}
		);

		@Override
		public @NotNull MapCodec<SimpleArtisanryRecipe> codec() {
			return CODEC;
		}

		@Override
		public @NotNull StreamCodec<RegistryFriendlyByteBuf, SimpleArtisanryRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
