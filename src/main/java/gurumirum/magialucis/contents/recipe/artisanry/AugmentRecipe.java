package gurumirum.magialucis.contents.recipe.artisanry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gurumirum.magialucis.contents.data.Augment;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.ModDataMaps;
import gurumirum.magialucis.contents.ModRecipes;
import gurumirum.magialucis.contents.data.ItemAugment;
import gurumirum.magialucis.contents.data.AugmentLogic;
import gurumirum.magialucis.contents.recipe.ConsumptionRecord;
import gurumirum.magialucis.contents.recipe.LuxInputCondition;
import gurumirum.magialucis.contents.recipe.LuxRecipeEvaluation;
import gurumirum.magialucis.contents.recipe.crafting.CraftingLogic;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.jetbrains.annotations.NotNull;

public class AugmentRecipe implements ArtisanryRecipe {
	private final ShapedRecipePattern pattern;
	private final Holder<Augment> augment;
	private final int processTicks;
	private final LuxInputCondition luxInputCondition;

	public AugmentRecipe(ShapedRecipePattern pattern, Holder<Augment> augment,
	                     int processTicks, LuxInputCondition luxInputCondition) {
		this.pattern = pattern;
		this.augment = augment;
		this.processTicks = processTicks;
		this.luxInputCondition = luxInputCondition;
	}

	public ShapedRecipePattern pattern() {
		return this.pattern;
	}
	public Holder<Augment> augment() {
		return this.augment;
	}
	public int processTicks() {
		return this.processTicks;
	}
	public LuxInputCondition luxInputCondition() {
		return this.luxInputCondition;
	}

	@Override
	public @NotNull LuxRecipeEvaluation evaluate(@NotNull ArtisanryRecipeInput input) {
		ItemStack stack = input.getCenterStack();
		if (stack.isEmpty()) return LuxRecipeEvaluation.fail();
		if (!AugmentLogic.canApply(stack, this.augment)) return LuxRecipeEvaluation.fail();
		if (!this.pattern.matches(input.asAugmentInput())) return LuxRecipeEvaluation.fail();

		return new LuxRecipeEvaluation(
				() -> {
					ItemStack copy = stack.copy();
					ItemAugment itemAugment = AugmentLogic.getAugments(stack);
					copy.set(ModDataComponents.AUGMENTS, itemAugment.with(this.augment));
					return copy;
				},
				this.processTicks,
				ConsumptionRecord.consumeAllByOne(input),
				this.luxInputCondition
		);
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer() {
		return ModRecipes.AUGMENT_SERIALIZER.get();
	}

	public static final class Serializer implements RecipeSerializer<AugmentRecipe> {
		private static final MapCodec<AugmentRecipe> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
				CraftingLogic.UNOPTIMIZED_PATTERN_CODEC.fieldOf("pattern").forGetter(r -> r.pattern),
				ModDataMaps.AUGMENT_CODEC.fieldOf("augment").forGetter(r -> r.augment),
				Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("processTicks", 0).forGetter(r -> r.processTicks),
				LuxInputCondition.CODEC.codec().optionalFieldOf("luxInput", LuxInputCondition.none()).forGetter(r -> r.luxInputCondition)
		).apply(b, AugmentRecipe::new));

		private static final StreamCodec<RegistryFriendlyByteBuf, AugmentRecipe> STREAM_CODEC = StreamCodec.of(
				(buffer, recipe) -> {
					ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
					ModDataMaps.AUGMENT_STREAM_CODEC.encode(buffer, recipe.augment);
					buffer.writeVarInt(recipe.processTicks);
					LuxInputCondition.STREAM_CODEC.encode(buffer, recipe.luxInputCondition);
				}, buffer -> {
					ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
					Holder<Augment> augment = ModDataMaps.AUGMENT_STREAM_CODEC.decode(buffer);
					int processTicks = buffer.readVarInt();
					LuxInputCondition luxInputCondition = LuxInputCondition.STREAM_CODEC.decode(buffer);
					return new AugmentRecipe(pattern, augment, processTicks, luxInputCondition);
				}
		);

		@Override
		public @NotNull MapCodec<AugmentRecipe> codec() {
			return CODEC;
		}

		@Override
		public @NotNull StreamCodec<RegistryFriendlyByteBuf, AugmentRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
