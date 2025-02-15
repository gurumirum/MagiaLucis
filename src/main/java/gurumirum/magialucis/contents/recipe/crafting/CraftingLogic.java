package gurumirum.magialucis.contents.recipe.crafting;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import gurumirum.magialucis.contents.ModBlocks;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

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
}
