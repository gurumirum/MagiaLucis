package gurumirum.magialucis.contents.item.accessory;

import gurumirum.magialucis.contents.item.LuxContainerItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class LuxContainerCurioItem extends LuxContainerItem {
	public LuxContainerCurioItem(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player,
	                                                       @NotNull InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		if (!level.isClientSide) {
			if (BaseCurioItem.tryEquipCurio(player, stack)) {
				player.setItemInHand(usedHand, stack = ItemStack.EMPTY);
			}
		}
		return InteractionResultHolder.consume(stack);
	}
}
