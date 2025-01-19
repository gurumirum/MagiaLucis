package gurumirum.magialucis.contents.item.accessory;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.Accessories;
import gurumirum.magialucis.contents.ModCurioSlots;
import gurumirum.magialucis.contents.ModDataComponents;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

@EventBusSubscriber(modid = MagiaLucisMod.MODID)
public final class AccessoryEventListener {
	public static final int COST_PER_FIRE_RESISTANCE = 5;

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onEntityInvulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {
		if (event.isInvulnerable()) return;
		if (!(event.getEntity() instanceof Player player)) return;

		if ((event.getSource().is(DamageTypes.ON_FIRE) || event.getSource().is(DamageTypes.IN_FIRE)) &&
				!player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
			CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
				ICurioStacksHandler curios = handler.getCurios().get(ModCurioSlots.BRACELET);

				for (int i = 0; i < curios.getSlots(); ++i) {
					ItemStack stack = curios.getStacks().getStackInSlot(i);
					if (!stack.is(Accessories.FIRE_IMMUNE_BRACELET.asItem())) continue;

					long lux = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
					if (lux >= COST_PER_FIRE_RESISTANCE) {
						stack.set(ModDataComponents.LUX_CHARGE, lux - COST_PER_FIRE_RESISTANCE);
						event.setInvulnerable(true);
						break;
					}
				}
			});
		}
	}
}
