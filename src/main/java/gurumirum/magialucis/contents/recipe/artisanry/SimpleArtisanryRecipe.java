package gurumirum.magialucis.contents.recipe.artisanry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gurumirum.magialucis.contents.ModRecipes;
import gurumirum.magialucis.contents.recipe.ConsumptionRecord;
import gurumirum.magialucis.contents.recipe.LuxInputCondition;
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
	private final LuxInputCondition luxInputCondition;

	public SimpleArtisanryRecipe(@NotNull ShapedRecipePattern pattern, @NotNull ItemStack result,
	                             int processTicks, @NotNull LuxInputCondition luxInputCondition) {
		this.pattern = pattern;
		this.result = result;
		this.processTicks = processTicks;
		this.luxInputCondition = luxInputCondition;
	}

	public ShapedRecipePattern pattern() {
		return this.pattern;
	}
	public int processTicks() {
		return this.processTicks;
	}
	public LuxInputCondition luxInputCondition() {
		return luxInputCondition;
	}

	@Override
	public @NotNull LuxRecipeEvaluation evaluate(@NotNull ArtisanryRecipeInput input) {
		if (this.pattern.matches(input.asCraftingInput())) {
			return new LuxRecipeEvaluation(this.result::copy, processTicks(),
					ConsumptionRecord.consumeAllByOne(input), luxInputCondition());
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
				LuxInputCondition.CODEC.codec().optionalFieldOf("luxInput", LuxInputCondition.none()).forGetter(SimpleArtisanryRecipe::luxInputCondition)
		).apply(b, SimpleArtisanryRecipe::new));

		private static final StreamCodec<RegistryFriendlyByteBuf, SimpleArtisanryRecipe> STREAM_CODEC = StreamCodec.of(
				(buffer, recipe) -> {
					ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
					ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
					buffer.writeVarInt(recipe.processTicks);
					LuxInputCondition.STREAM_CODEC.encode(buffer, recipe.luxInputCondition);
				}, buffer -> {
					ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
					ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
					int processTicks = buffer.readVarInt();
					LuxInputCondition luxInputCondition = LuxInputCondition.STREAM_CODEC.decode(buffer);
					return new SimpleArtisanryRecipe(pattern, result, processTicks, luxInputCondition);
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
