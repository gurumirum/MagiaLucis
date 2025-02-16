package gurumirum.magialucis.contents.recipe.crafting;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.recipe.ConsumptionRecord;
import gurumirum.magialucis.contents.recipe.IngredientStack;
import gurumirum.magialucis.contents.recipe.InputPattern;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class CraftingLogic {
	private CraftingLogic() {}

	/**
	 * @see ShapedRecipePattern#MAP_CODEC
	 */
	public static final MapCodec<ShapedRecipePattern> UNOPTIMIZED_PATTERN_CODEC = ShapedRecipePattern.Data.MAP_CODEC
			.flatXmap(CraftingLogic::unpackUnoptimized, p -> p.data.map(DataResult::success)
					.orElseGet(() -> DataResult.error(() -> "Cannot encode unpacked recipe")));

	/**
	 * @see ShapedRecipePattern#unpack(ShapedRecipePattern.Data)
	 */
	private static DataResult<ShapedRecipePattern> unpackUnoptimized(ShapedRecipePattern.Data data) {
		String[] pattern = data.pattern().toArray(new String[0]);
		int width = pattern[0].length();
		int height = pattern.length;
		NonNullList<Ingredient> list = NonNullList.withSize(width * height, Ingredient.EMPTY);
		CharSet chars = new CharArraySet(data.key().keySet());

		for (int y = 0; y < pattern.length; y++) {
			String s = pattern[y];

			for (int x = 0; x < s.length(); x++) {
				char c = s.charAt(x);
				Ingredient ingredient = c == ' ' ? Ingredient.EMPTY : data.key().get(c);
				if (ingredient == null) {
					return DataResult.error(() -> "Pattern references symbol '" + c + "' but it's not defined in the key");
				}

				chars.remove(c);
				list.set(x + width * y, ingredient);
			}
		}

		return !chars.isEmpty()
				? DataResult.error(() -> "Key defines symbols that aren't used in pattern: " + chars)
				: DataResult.success(new ShapedRecipePattern(width, height, list, Optional.of(data)));
	}

	public static boolean isRelayItem(ItemStack stack) {
		return stack.is(ModBlocks.RELAY.asItem()) ||
				stack.is(ModBlocks.SPLITTER.asItem()) ||
				stack.is(ModBlocks.CONNECTOR.asItem());
	}

	public static @Nullable ConsumptionRecord test(InputPattern<IngredientStack> pattern, CraftingInput input) {
		if (input.width() == pattern.width() && input.height() == pattern.height()) {
			if (!pattern.symmetrical()) {
				ConsumptionRecord r = test(pattern, input, true);
				if (r != null) return r;
			}

			return test(pattern, input, false);
		}

		return null;
	}

	private static @Nullable ConsumptionRecord test(InputPattern<IngredientStack> pattern, CraftingInput input,
	                                                boolean mirrored) {
		for (int y = 0; y < pattern.height(); y++) {
			for (int x = 0; x < pattern.width(); x++) {
				IngredientStack ingredient = pattern.get(x, y, mirrored);

				ItemStack stack = input.getItem(x, y);
				if (!ingredient.ingredient().test(stack) || ingredient.count() > stack.getCount()) {
					return null;
				}
			}
		}

		ConsumptionRecord.Mutable consumptionRecord = new ConsumptionRecord.Mutable();

		for (int y = 0; y < pattern.height(); y++) {
			for (int x = 0; x < pattern.width(); x++) {
				IngredientStack ingredient = pattern.get(x, y, mirrored);

				consumptionRecord.add(pattern.getIndex(x, y, mirrored), ingredient.count());
			}
		}

		return consumptionRecord;
	}
}
