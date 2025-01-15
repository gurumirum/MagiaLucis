package gurumirum.magialucis.impl.ancientlight;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public record AncientLightRecipe(int processTicks, @NotNull @Unmodifiable List<ItemStack> output) {
	public AncientLightRecipe(int processTicks, ItemLike itemLike) {
		this(processTicks, List.of(new ItemStack(itemLike)));
	}
}
