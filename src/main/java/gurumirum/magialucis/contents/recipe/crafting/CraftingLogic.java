package gurumirum.magialucis.contents.recipe.crafting;

import gurumirum.magialucis.contents.ModBlocks;
import net.minecraft.world.item.ItemStack;

public final class CraftingLogic {
	private CraftingLogic() {}

	public static boolean isRelayItem(ItemStack stack) {
		return stack.is(ModBlocks.RELAY.asItem()) ||
				stack.is(ModBlocks.SPLITTER.asItem()) ||
				stack.is(ModBlocks.CONNECTOR.asItem());
	}
}
