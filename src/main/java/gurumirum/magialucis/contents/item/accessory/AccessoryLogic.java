package gurumirum.magialucis.contents.item.accessory;

import gurumirum.magialucis.contents.ModDataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

public final class AccessoryLogic {
	private AccessoryLogic() {}

	public static boolean updateCharge(@NotNull SlotContext slotContext, @NotNull ItemStack stack,
	                                   int chargeTicks, long luxCostPerCharge, boolean shouldConsumeCharge) {
		LivingEntity entity = slotContext.entity();
		if (entity.level().isClientSide) return false;

		int charge = stack.getOrDefault(ModDataComponents.CHARGE, 0);

		if (shouldConsumeCharge && charge > 0) {
			charge--;
		}

		if (charge <= 0) {
			long luxCharge = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
			if (luxCharge >= luxCostPerCharge) {
				stack.set(ModDataComponents.LUX_CHARGE, luxCharge - luxCostPerCharge);
				charge = chargeTicks;
			}
		}

		stack.set(ModDataComponents.CHARGE, charge);

		return charge > 0;
	}
}
