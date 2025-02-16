package gurumirum.magialucis.contents.recipe.artisanry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gurumirum.magialucis.contents.ModRecipes;
import gurumirum.magialucis.contents.recipe.*;
import gurumirum.magialucis.contents.recipe.crafting.CraftingLogic;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public class SimpleArtisanryRecipe implements ArtisanryRecipe {
	private final InputPattern<IngredientStack> pattern;
	private final ItemStack result;
	private final int processTicks;
	private final LuxInputCondition luxInputCondition;

	public SimpleArtisanryRecipe(@NotNull InputPattern<IngredientStack> pattern, @NotNull ItemStack result,
	                             int processTicks, @NotNull LuxInputCondition luxInputCondition) {
		this.pattern = pattern;
		this.result = result;
		this.processTicks = processTicks;
		this.luxInputCondition = luxInputCondition;
	}

	@Override
	public @NotNull InputPattern<IngredientStack> pattern() {
		return this.pattern;
	}
	@Override
	public int processTicks() {
		return this.processTicks;
	}
	@Override
	public @NotNull LuxInputCondition luxInputCondition() {
		return this.luxInputCondition;
	}

	@Override
	public @NotNull LuxRecipeEvaluation evaluate(@NotNull ArtisanryRecipeInput input) {
		ConsumptionRecord consumptions = CraftingLogic.test(this.pattern, input.asCraftingInput());
		if (consumptions != null) {
			return new LuxRecipeEvaluation(this.result::copy, processTicks(), consumptions, luxInputCondition());
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
	public @NotNull RecipeSerializer<?> getSerializer() {
		return ModRecipes.ARTISANRY_SERIALIZER.get();
	}

	public static final class Serializer implements RecipeSerializer<SimpleArtisanryRecipe> {
		private static final MapCodec<SimpleArtisanryRecipe> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
				ArtisanryRecipe.GRID_SPEC.codec().fieldOf("pattern").forGetter(r -> r.pattern),
				ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result),
				Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("processTicks", 0).forGetter(r -> r.processTicks),
				LuxInputCondition.CODEC.codec().optionalFieldOf("luxInput", LuxInputCondition.none()).forGetter(r -> r.luxInputCondition)
		).apply(b, SimpleArtisanryRecipe::new));

		private static final StreamCodec<RegistryFriendlyByteBuf, SimpleArtisanryRecipe> STREAM_CODEC = StreamCodec.of(
				(buffer, recipe) -> {
					ArtisanryRecipe.GRID_SPEC.streamCodec().encode(buffer, recipe.pattern);
					ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
					buffer.writeVarInt(recipe.processTicks);
					LuxInputCondition.STREAM_CODEC.encode(buffer, recipe.luxInputCondition);
				}, buffer -> {
					InputPattern<IngredientStack> pattern = ArtisanryRecipe.GRID_SPEC.streamCodec().decode(buffer);
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
