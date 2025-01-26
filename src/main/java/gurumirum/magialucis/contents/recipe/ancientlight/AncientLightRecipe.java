package gurumirum.magialucis.contents.recipe.ancientlight;

import gurumirum.magialucis.contents.ModRecipes;
import gurumirum.magialucis.contents.recipe.NoRecipeInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;

public interface AncientLightRecipe extends Recipe<NoRecipeInput> {
	@NotNull @Unmodifiable Set<Holder<Block>> blocks();

	default boolean isValid(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
		return true;
	}

	int getProcessTicks(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state);

	@NotNull List<ItemStack> assemble(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state);

	@NotNull List<ItemStack> getResultItemView();

	int getProcessTicksView();

	@Deprecated
	@Override
	default boolean matches(@NotNull NoRecipeInput input, @NotNull Level level) {
		return false;
	}

	@Deprecated
	@Override
	default @NotNull ItemStack assemble(@NotNull NoRecipeInput input, HolderLookup.@NotNull Provider registries) {
		return ItemStack.EMPTY;
	}

	@Deprecated
	@Override
	default boolean canCraftInDimensions(int width, int height) {
		return false;
	}

	@Deprecated
	@Override
	@NotNull default ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override default @NotNull RecipeType<?> getType() {
		return ModRecipes.ANCIENT_LIGHT_TYPE.get();
	}
}
