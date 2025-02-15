package gurumirum.magialucis.contents.recipe.crafting;

import com.mojang.serialization.MapCodec;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.ModRecipes;
import gurumirum.magialucis.contents.data.GemItemData;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ShapedRelayCraftingRecipe extends ShapedRecipe {
	@SuppressWarnings("DataFlowIssue")
	public ShapedRelayCraftingRecipe(ShapedRecipe copyFrom) {
		super(copyFrom.getGroup(), copyFrom.category(), copyFrom.pattern,
				copyFrom.getResultItem(null), copyFrom.showNotification());
	}

	@Override
	public @NotNull ItemStack assemble(@NotNull CraftingInput input, HolderLookup.@NotNull Provider registries) {
		ItemStack result = super.assemble(input, registries);
		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) continue;

			if (CraftingLogic.isRelayItem(stack)) {
				GemItemData gemItemData = stack.get(ModDataComponents.GEM_ITEM);
				if (gemItemData != null) {
					result.set(ModDataComponents.GEM_ITEM, gemItemData);
					break;
				}
			}
		}
		return result;
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer() {
		return ModRecipes.SHAPED_RELAY_CRAFTING.get();
	}

	public static class Serializer implements RecipeSerializer<ShapedRelayCraftingRecipe> {
		public static final MapCodec<ShapedRelayCraftingRecipe> CODEC = ShapedRecipe.Serializer.CODEC.xmap(
				ShapedRelayCraftingRecipe::new, Function.identity());
		public static final StreamCodec<RegistryFriendlyByteBuf, ShapedRelayCraftingRecipe> STREAM_CODEC = ShapedRecipe.Serializer.STREAM_CODEC.map(
				ShapedRelayCraftingRecipe::new, Function.identity());

		@Override
		public @NotNull MapCodec<ShapedRelayCraftingRecipe> codec() {
			return CODEC;
		}

		@Override
		public @NotNull StreamCodec<RegistryFriendlyByteBuf, ShapedRelayCraftingRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
