package gurumirum.magialucis.mixin;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerMenu.class)
public interface AbstractContainerMenuInvoker {
	/**
	 * @see AbstractContainerMenu#moveItemStackTo(ItemStack, int, int, boolean)
	 */
	@Invoker("moveItemStackTo")
	boolean invokeMoveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection);
}
