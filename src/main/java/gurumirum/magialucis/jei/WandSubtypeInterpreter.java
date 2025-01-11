package gurumirum.magialucis.jei;

import gurumirum.magialucis.contents.Contents;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WandSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
    public static final WandSubtypeInterpreter INSTANCE = new WandSubtypeInterpreter();

    private WandSubtypeInterpreter() {}

    @Override
    @Nullable
    public Object getSubtypeData(ItemStack ingredient, @NotNull UidContext context) {
        return ingredient.getOrDefault(Contents.LUX_CHARGE, 0L);
    }

    @Override
    public @NotNull String getLegacyStringSubtypeInfo(@NotNull ItemStack ingredient, @NotNull UidContext context) {
        return "";
    }
}
