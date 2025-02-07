package gurumirum.magialucis.contents.recipe.transfusion;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gurumirum.magialucis.contents.recipe.ConsumptionRecord;
import gurumirum.magialucis.contents.recipe.IngredientStack;
import gurumirum.magialucis.contents.recipe.LuxInputCondition;
import gurumirum.magialucis.contents.recipe.LuxRecipeEvaluation;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseTransfusionRecipe implements TransfusionRecipe {
	private final ItemStack result;
	private final List<IngredientStack> ingredients;
	private final int processTicks;
	private final LuxInputCondition luxInputCondition;

	public BaseTransfusionRecipe(@NotNull ItemStack result, @NotNull List<IngredientStack> ingredients,
	                             int processTicks, @NotNull LuxInputCondition luxInputCondition) {
		this.result = result;
		this.ingredients = ingredients;
		this.processTicks = processTicks;
		this.luxInputCondition = luxInputCondition;
	}

	public @NotNull ItemStack result() {
		return this.result;
	}
	public @NotNull @UnmodifiableView List<IngredientStack> ingredients() {
		return Collections.unmodifiableList(this.ingredients);
	}
	public int processTicks() {
		return this.processTicks;
	}
	public @NotNull LuxInputCondition luxInputCondition() {
		return luxInputCondition;
	}

	@Override
	public @NotNull LuxRecipeEvaluation evaluate(@NotNull TransfusionRecipeInput input) {
		ConsumptionRecord.Mutable consumption = ConsumptionRecord.create();

		for (IngredientStack is : ingredients()) {
			int countLeft = is.count();
			for (int i = 0; countLeft > 0 && i < input.size(); i++) {
				ItemStack stack = input.getItem(i);
				if (!is.ingredient().test(stack)) continue;

				int count = stack.getCount();
				if (count < countLeft) {
					countLeft -= count;
					consumption.add(i, count);
				} else {
					consumption.add(i, countLeft);
					countLeft = 0;
				}
			}
			if (countLeft > 0) return LuxRecipeEvaluation.fail();
		}

		return new LuxRecipeEvaluation(() -> result().copy(), processTicks(), consumption, luxInputCondition());
	}

	@Override
	public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
		return this.result;
	}

	protected static <T extends BaseTransfusionRecipe> Products.P4<
			RecordCodecBuilder.Mu<T>, ItemStack, List<IngredientStack>,
			Integer, LuxInputCondition> commonFields(RecordCodecBuilder.Instance<T> instance) {
		return instance.group(
				ItemStack.STRICT_CODEC.fieldOf("result").forGetter(BaseTransfusionRecipe::result),
				IngredientStack.CODEC.listOf().fieldOf("ingredients").forGetter(BaseTransfusionRecipe::ingredients),
				Codec.intRange(1, Integer.MAX_VALUE).fieldOf("processTicks").forGetter(BaseTransfusionRecipe::processTicks),
				LuxInputCondition.CODEC.codec().optionalFieldOf("luxInput", LuxInputCondition.none()).forGetter(BaseTransfusionRecipe::luxInputCondition)
		);
	}

	private static final StreamEncoder<RegistryFriendlyByteBuf, BaseTransfusionRecipe> _commonEncoder = (buffer, recipe) -> {
		ItemStack.STREAM_CODEC.encode(buffer, recipe.result);

		List<IngredientStack> ingredients = recipe.ingredients;
		buffer.writeVarInt(ingredients.size());
		for (IngredientStack is : ingredients) IngredientStack.STREAM_CODEC.encode(buffer, is);

		buffer.writeVarInt(recipe.processTicks);
		LuxInputCondition.STREAM_CODEC.encode(buffer, recipe.luxInputCondition);
	};

	@SuppressWarnings("unchecked")
	protected static <R extends BaseTransfusionRecipe> StreamEncoder<RegistryFriendlyByteBuf, R> commonEncoder() {
		return (StreamEncoder<RegistryFriendlyByteBuf, R>)_commonEncoder;
	}

	protected static <R extends BaseTransfusionRecipe> StreamDecoder<RegistryFriendlyByteBuf, R> commonDecoder(Ctor<R> ctor) {
		return buffer -> {
			ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);

			List<IngredientStack> ingredients = new ArrayList<>();
			for (int i = buffer.readVarInt(); i > 0; i--) {
				ingredients.add(IngredientStack.STREAM_CODEC.decode(buffer));
			}

			int processTicks = buffer.readVarInt();
			LuxInputCondition luxInputCondition = LuxInputCondition.STREAM_CODEC.decode(buffer);

			return ctor.createInstance(result, ingredients, processTicks, luxInputCondition);
		};
	}

	@FunctionalInterface
	public interface Ctor<R extends BaseTransfusionRecipe> {
		R createInstance(ItemStack result, List<IngredientStack> ingredients, int processTicks,
		                 LuxInputCondition luxInputCondition);
	}
}
