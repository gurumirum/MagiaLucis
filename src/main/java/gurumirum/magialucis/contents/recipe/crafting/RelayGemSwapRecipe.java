package gurumirum.magialucis.contents.recipe.crafting;

import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.ModRecipes;
import gurumirum.magialucis.contents.block.lux.relay.GemItemData;
import gurumirum.magialucis.impl.GemStatLogic;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class RelayGemSwapRecipe implements CraftingRecipe {
	private final CraftingBookCategory category;

	public RelayGemSwapRecipe(CraftingBookCategory category) {
		this.category = category;
	}

	@Override
	public @NotNull CraftingBookCategory category() {
		return this.category;
	}

	@Override
	public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
		return test(input) != null;
	}

	@Override
	public @NotNull ItemStack assemble(@NotNull CraftingInput input, HolderLookup.@NotNull Provider registries) {
		Supplier<ItemStack> result = test(input);
		return result != null ? result.get() : ItemStack.EMPTY;
	}

	private static @Nullable Supplier<ItemStack> test(@NotNull CraftingInput input) {
		ItemStack relayItem = null;
		ItemStack gemItem = null;

		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) continue;

			if (relayItem == null) {
				if (isRelayItem(stack)) {
					relayItem = stack;
					continue;
				}
			}
			if (gemItem == null) {
				if (GemStatLogic.get(stack) != null) {
					gemItem = stack;
					continue;
				}
			}
			return null;
		}

		if (relayItem == null) return null;

		ItemStack relayItem0 = relayItem;
		ItemStack gemItem0 = gemItem != null ? gemItem : ItemStack.EMPTY;
		GemItemData gemItemData = relayItem.get(ModDataComponents.GEM_ITEM);
		ItemStack existingGemItem = gemItemData != null ? gemItemData.stack() : ItemStack.EMPTY;

		if (ItemStack.isSameItemSameComponents(existingGemItem, gemItem0)) return null;

		return () -> {
			ItemStack copy = relayItem0.copyWithCount(1);
			if (gemItem0.isEmpty()) {
				copy.remove(ModDataComponents.GEM_ITEM);
			} else {
				copy.set(ModDataComponents.GEM_ITEM, new GemItemData(gemItem0.copyWithCount(1)));
			}
			return copy;
		};
	}

	private static boolean isRelayItem(ItemStack stack) {
		return stack.is(ModBlocks.RELAY.asItem()) ||
				stack.is(ModBlocks.SPLITTER.asItem()) ||
				stack.is(ModBlocks.CONNECTOR.asItem());
	}

	@Override
	public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInput input) {
		NonNullList<ItemStack> list = NonNullList.withSize(input.size(), ItemStack.EMPTY);

		for (int i = 0; i < list.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (isRelayItem(stack)) {
				GemItemData gemItemData = stack.get(ModDataComponents.GEM_ITEM);
				if (gemItemData != null) {
					list.set(i, gemItemData.stack().copy());
				}
			}
		}

		return list;
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
	public boolean isSpecial() {
		return true;
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer() {
		return ModRecipes.RELAY_GEM_SWAP_SERIALIZER.get();
	}
}
