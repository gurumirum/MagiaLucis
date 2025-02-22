package gurumirum.magialucis.jei;

import gurumirum.magialucis.contents.ModDataComponents;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GemContainerSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
	public static final GemContainerSubtypeInterpreter INSTANCE = new GemContainerSubtypeInterpreter();

	@Override
	public @Nullable Object getSubtypeData(@NotNull ItemStack ingredient, @NotNull UidContext context) {
		return context == UidContext.Ingredient ? ingredient.get(ModDataComponents.GEM_ITEM) : null;
	}

	@Override
	public @NotNull String getLegacyStringSubtypeInfo(@NotNull ItemStack ingredient, @NotNull UidContext context) {
		return getSubtypeData(ingredient, context) + "";
	}
}
