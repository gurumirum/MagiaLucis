package gurumirum.magialucis.contents.item.wandbelt;

import gurumirum.magialucis.contents.Accessories;
import gurumirum.magialucis.contents.ModCurioSlots;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

public final class WandBelt {
	private WandBelt() {}

	public static int SLOTS = 18;

	public static @NotNull ItemStack get(Player player) {
		return CuriosApi.getCuriosInventory(player)
				.flatMap(h -> h.findCurio(ModCurioSlots.WAND_BELT, 0)
						.map(SlotResult::stack)
						.filter(s -> s.is(Accessories.WAND_BELT.asItem())))
				.orElse(ItemStack.EMPTY);
	}
}
