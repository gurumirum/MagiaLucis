package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.Contents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;

@EventBusSubscriber(modid = MagiaLucisMod.MODID)
public final class WandEventListener {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onBlockEvent(LivingShieldBlockEvent e) {
		if (e.getEntity() instanceof Player player) {
			if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ShieldWandItem && player.isUsingItem()) {
				ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
				long lux = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
				if (lux >= ShieldWandItem.COST_PER_SHIELD) {
					e.setBlocked(true);
					stack.set(Contents.LUX_CHARGE, lux - ShieldWandItem.COST_PER_BLOCKING);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onEntityHurt(LivingDamageEvent.Post event) {
		ItemStack stack = event.getSource().getWeaponItem();
		if (stack != null && event.getSource().getEntity() instanceof Player player
				&& stack.getItem() instanceof DiamondMaceItem
				&& player.getAttackStrengthScale(0.0f) == 1f
				&& stack.getOrDefault(Contents.LUX_CHARGE, 0L) >= DiamondMaceItem.COST_PER_ATTACK) {
			long current = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
			event.getEntity().addEffect(new MobEffectInstance(Contents.DOUBLE_MAGIC_DAMAGE, DiamondMaceItem.DEBUFF_DURATION));
			stack.set(Contents.LUX_CHARGE, current - DiamondMaceItem.COST_PER_ATTACK);
		}
	}

	@SubscribeEvent
	public static void onLivingIncomeDamage(LivingIncomingDamageEvent event) {
		if (event.getEntity().hasEffect(Contents.DOUBLE_MAGIC_DAMAGE)) {
			DamageSource source = event.getSource();
			if (source.is(DamageTypes.MAGIC) || source.is(DamageTypes.INDIRECT_MAGIC)) {
				event.setAmount(event.getAmount() * 2);
			}
		}
	}
}
