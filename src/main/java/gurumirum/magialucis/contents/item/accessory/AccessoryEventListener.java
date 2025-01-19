package gurumirum.magialucis.contents.item.accessory;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.ModCurioSlots;
import gurumirum.magialucis.contents.ModDataComponents;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

@EventBusSubscriber(modid = MagiaLucisMod.MODID)
public final class AccessoryEventListener {

	@SubscribeEvent
	public static void onEntityHurt(EntityInvulnerabilityCheckEvent event) {
		if (event.getEntity() instanceof Player player) {
			if (event.getSource().is(DamageTypes.ON_FIRE) || event.getSource().is(DamageTypes.IN_FIRE)) {
				CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
					ICurioStacksHandler curios = handler.getCurios().get(ModCurioSlots.BRACELET);
					for (int i = 0; i < curios.getSlots(); ++i) {
						ItemStack stack = curios.getStacks().getStackInSlot(i);
						if (stack.isEmpty() || !(stack.getItem() instanceof FireImmuneRubyBracelet)) continue;
						long lux = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
						if (lux >= FireImmuneRubyBracelet.COST_PER_FIRE_RESISTANCE) {
							stack.set(ModDataComponents.LUX_CHARGE, lux - FireImmuneRubyBracelet.COST_PER_FIRE_RESISTANCE);
							event.setInvulnerable(true);
							break;
						}
					}
				});
			}
		}
	}

}
