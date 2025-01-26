package gurumirum.magialucis.contents.recipe.transfusion;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gurumirum.magialucis.contents.recipe.ConsumptionRecord;
import gurumirum.magialucis.contents.recipe.IngredientStack;
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
	private final double minLuxInputR;
	private final double minLuxInputG;
	private final double minLuxInputB;
	private final double minLuxInputSum;
	private final double maxLuxInputR;
	private final double maxLuxInputG;
	private final double maxLuxInputB;
	private final double maxLuxInputSum;

	public BaseTransfusionRecipe(ItemStack result, List<IngredientStack> ingredients, int processTicks,
	                             double minLuxInputR, double minLuxInputG, double minLuxInputB, double minLuxInputSum,
	                             double maxLuxInputR, double maxLuxInputG, double maxLuxInputB, double maxLuxInputSum) {
		this.result = result;
		this.ingredients = ingredients;
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

	public ItemStack result() {
		return this.result;
	}
	public @NotNull @UnmodifiableView List<IngredientStack> ingredients() {
		return Collections.unmodifiableList(this.ingredients);
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
	public @NotNull TransfusionRecipeEvaluation evaluate(@NotNull TransfusionRecipeInput input) {
		ConsumptionRecord consumption = new ConsumptionRecord();

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
			if (countLeft > 0) return TransfusionRecipeEvaluation.fail();
		}

		return new TransfusionRecipeEvaluation(() -> result().copy(), processTicks(), consumption,
				minLuxInputR(), minLuxInputG(), minLuxInputB(), minLuxInputSum(),
				maxLuxInputR(), maxLuxInputG(), maxLuxInputB(), maxLuxInputSum());
	}

	@Override
	public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
		return this.result;
	}

	protected static <T extends BaseTransfusionRecipe> Products.P11<RecordCodecBuilder.Mu<T>,
			ItemStack, List<IngredientStack>, Integer,
			Double, Double, Double, Double,
			Double, Double, Double, Double> commonFields(RecordCodecBuilder.Instance<T> instance) {
		return instance.group(
				ItemStack.STRICT_CODEC.fieldOf("result").forGetter(BaseTransfusionRecipe::result),
				IngredientStack.CODEC.listOf().fieldOf("ingredients").forGetter(BaseTransfusionRecipe::ingredients),
				Codec.intRange(1, Integer.MAX_VALUE).fieldOf("processTicks").forGetter(BaseTransfusionRecipe::processTicks),
				Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("minLuxInputR", 0.0).forGetter(BaseTransfusionRecipe::minLuxInputR),
				Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("minLuxInputG", 0.0).forGetter(BaseTransfusionRecipe::minLuxInputG),
				Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("minLuxInputB", 0.0).forGetter(BaseTransfusionRecipe::minLuxInputB),
				Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("minLuxInputSum", 0.0).forGetter(BaseTransfusionRecipe::minLuxInputSum),
				Codec.doubleRange(0, Double.POSITIVE_INFINITY).optionalFieldOf("maxLuxInputR", Double.POSITIVE_INFINITY).forGetter(BaseTransfusionRecipe::maxLuxInputR),
				Codec.doubleRange(0, Double.POSITIVE_INFINITY).optionalFieldOf("maxLuxInputG", Double.POSITIVE_INFINITY).forGetter(BaseTransfusionRecipe::maxLuxInputG),
				Codec.doubleRange(0, Double.POSITIVE_INFINITY).optionalFieldOf("maxLuxInputB", Double.POSITIVE_INFINITY).forGetter(BaseTransfusionRecipe::maxLuxInputB),
				Codec.doubleRange(0, Double.POSITIVE_INFINITY).optionalFieldOf("maxLuxInputSum", Double.POSITIVE_INFINITY).forGetter(BaseTransfusionRecipe::maxLuxInputSum)
		);
	}

	private static final StreamEncoder<RegistryFriendlyByteBuf, BaseTransfusionRecipe> _commonEncoder = (buffer, recipe) -> {
		ItemStack.STREAM_CODEC.encode(buffer, recipe.result());

		List<IngredientStack> ingredients = recipe.ingredients();
		buffer.writeVarInt(ingredients.size());
		for (IngredientStack is : ingredients) IngredientStack.STREAM_CODEC.encode(buffer, is);

		buffer.writeVarInt(recipe.processTicks());

		buffer.writeDouble(recipe.minLuxInputR());
		buffer.writeDouble(recipe.minLuxInputG());
		buffer.writeDouble(recipe.minLuxInputB());
		buffer.writeDouble(recipe.minLuxInputSum());
		buffer.writeDouble(recipe.maxLuxInputR());
		buffer.writeDouble(recipe.maxLuxInputG());
		buffer.writeDouble(recipe.maxLuxInputB());
		buffer.writeDouble(recipe.maxLuxInputSum());
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

			double minLuxInputR = buffer.readDouble();
			double minLuxInputG = buffer.readDouble();
			double minLuxInputB = buffer.readDouble();
			double minLuxInputSum = buffer.readDouble();
			double maxLuxInputR = buffer.readDouble();
			double maxLuxInputG = buffer.readDouble();
			double maxLuxInputB = buffer.readDouble();
			double maxLuxInputSum = buffer.readDouble();

			return ctor.createInstance(result, ingredients, processTicks,
					minLuxInputR, minLuxInputG, minLuxInputB, minLuxInputSum,
					maxLuxInputR, maxLuxInputG, maxLuxInputB, maxLuxInputSum);
		};
	}

	@FunctionalInterface
	public interface Ctor<R extends BaseTransfusionRecipe> {
		R createInstance(ItemStack result, List<IngredientStack> ingredients, int processTicks,
		                 double minLuxInputR, double minLuxInputG, double minLuxInputB, double minLuxInputSum,
		                 double maxLuxInputR, double maxLuxInputG, double maxLuxInputB, double maxLuxInputSum);
	}
}
