package gurumirum.gemthing.contents.item.wandbelt;

import gurumirum.gemthing.contents.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

public final class WandBelt {
	private WandBelt() {}

	public static @NotNull ItemStack get(Player player) {
		return CuriosApi.getCuriosInventory(player)
				.flatMap(h -> h.findCurio(WandBeltItem.CURIO_SLOT, 0)
						.map(SlotResult::stack)
						.filter(s -> s.is(ModItems.WAND_BELT.asItem())))
				.orElse(ItemStack.EMPTY);
	}
}
