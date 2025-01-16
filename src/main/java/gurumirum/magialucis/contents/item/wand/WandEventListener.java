package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.contents.Wands;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;

@EventBusSubscriber(modid = MagiaLucisMod.MODID)
public final class WandEventListener {
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onLivingShieldBlock(LivingShieldBlockEvent event) {
		ItemStack stack = event.getEntity().getUseItem();
		if (!stack.is(Wands.LAPIS_SHIELD.asItem())) return;

		long charge = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
		if (charge < LapisShieldItem.COST_PER_BLOCK) return;

		if (!isDamageSourceBlocked(event.getEntity(), event.getDamageSource())) return;

		event.setBlocked(true);
		stack.set(Contents.LUX_CHARGE, charge - LapisShieldItem.COST_PER_BLOCK);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLivingShieldBlockLast(LivingShieldBlockEvent event) {
		if (!event.getBlocked()) return;

		ItemStack stack = event.getEntity().getUseItem();
		if (!stack.is(Wands.LAPIS_SHIELD.asItem())) return;

		float blockedDamage = event.getBlockedDamage();
		Entity directEntity = event.getDamageSource().getDirectEntity();
		if (blockedDamage > 0 && directEntity instanceof LivingEntity livingEntity) {
			LivingEntity e = event.getEntity();
			livingEntity.knockback(1.5f, e.getX() - livingEntity.getX(), e.getZ() - livingEntity.getZ());
			livingEntity.hurt(e.level().damageSources().thorns(e), blockedDamage / 2);
		}
	}

	// LivingEntity#isDamageSourceBlocked
	// removes bypasses_shield tag and arrow piercing check
	private static boolean isDamageSourceBlocked(LivingEntity self, DamageSource damageSource) {
		if (self.isBlocking()) {
			Vec3 sourcePosition = damageSource.getSourcePosition();
			if (sourcePosition != null) {
				Vec3 view = self.calculateViewVector(0.0F, self.getYHeadRot());
				Vec3 dist = sourcePosition.vectorTo(self.position());
				dist = new Vec3(dist.x, 0.0, dist.z).normalize();
				return dist.dot(view) < 0.0;
			}
		}

		return false;
	}

	@SubscribeEvent
	public static void afterLivingDamage(LivingDamageEvent.Post event) {
		ItemStack stack = event.getSource().getWeaponItem();
		if (stack == null) return;

		if (stack.is(Wands.DIAMOND_MACE.asItem())) {
			long charge = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
			if (charge < DiamondMaceItem.COST_PER_ATTACK) return;

			if (event.getSource().getEntity() instanceof Player player && player.getAttackStrengthScale(0.5f) > 0.9f) {
				event.getEntity().addEffect(new MobEffectInstance(Contents.DOUBLE_MAGIC_DAMAGE, DiamondMaceItem.DEBUFF_DURATION));
				stack.set(Contents.LUX_CHARGE, charge - DiamondMaceItem.COST_PER_ATTACK);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
		if (event.getEntity().hasEffect(Contents.DOUBLE_MAGIC_DAMAGE) && event.getSource().is(Tags.DamageTypes.IS_MAGIC)) {
			event.setAmount(event.getAmount() * 2);
		}
	}
}
