package gurumirum.magialucis.jei;

import gurumirum.magialucis.contents.ModDataComponents;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LuxContainerSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
	public static final LuxContainerSubtypeInterpreter INSTANCE = new LuxContainerSubtypeInterpreter();

	private LuxContainerSubtypeInterpreter() {}

	@Override
	public @Nullable Object getSubtypeData(@NotNull ItemStack ingredient, @NotNull UidContext context) {
		return context == UidContext.Ingredient ? ingredient.getOrDefault(ModDataComponents.LUX_CHARGE, 0L) : null;
	}

	@Override
	public @NotNull String getLegacyStringSubtypeInfo(@NotNull ItemStack ingredient, @NotNull UidContext context) {
		return "";
	}
}
