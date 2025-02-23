package gurumirum.magialucis.contents.item.accessory;

import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.contents.Accessories;
import gurumirum.magialucis.contents.Augments;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.data.AugmentLogic;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.function.Predicate;

import static gurumirum.magialucis.contents.ModDataComponents.*;

@EventBusSubscriber(modid = MagiaLucisApi.MODID)
public final class AccessoryEventListener {

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onEntityInvulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {
		if (event.isInvulnerable()) return;
		if (!(event.getEntity() instanceof Player player)) return;

		DamageSource source = event.getSource();
		if ((source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) ||
				source.is(DamageTypes.CAMPFIRE) || source.is(DamageTypes.HOT_FLOOR)) &&
				!player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
			findCurioItemAndDo(Accessories.OBSIDIAN_BRACELET, player, stack -> {
				long charge = stack.getOrDefault(LUX_CHARGE, 0L);
				if (charge < ObsidianBraceletItem.COST) return false;

				event.setInvulnerable(true);
				stack.set(LUX_CHARGE, charge - ObsidianBraceletItem.COST);
				return true;
			});
		}
	}

	@SubscribeEvent
	public static void onEntityHurt(LivingDamageEvent.Pre event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (event.getNewDamage() <= 0) return;

		findCurioItemAndDo(Accessories.SHIELD_NECKLACE, player, stack -> {
			if (stack.getOrDefault(ModDataComponents.DEPLETED, false)) return false;

			float shieldCharge = stack.getOrDefault(SHIELD_CHARGE, 0f);
			float damageReduction = Math.max(event.getNewDamage(), ShieldCurioItem.DAMAGE_ABSORPTION_LIMIT);

			if (shieldCharge < damageReduction) {
				damageReduction = shieldCharge;

				stack.set(SHIELD_CHARGE, 0f);
				stack.set(DEPLETED, true);

				if (AugmentLogic.getAugments(stack).has(Augments.SHIELD_NECKLACE_EXPLOSION)) {
					long luxCharge = stack.getOrDefault(LUX_CHARGE, 0L);
					if (luxCharge >= ShieldCurioItem.EXPLOSION_COST) {
						// TODO Do visual? sth? idk
						for (Monster entity : player.level().getEntities(
								EntityTypeTest.forClass(Monster.class),
								player.getBoundingBox().inflate(4),
								LivingEntity::isAlive)) {
							entity.knockback(1.5f, player.getX() - entity.getX(), player.getZ() - entity.getZ());
							entity.hurt(player.damageSources().source(DamageTypes.INDIRECT_MAGIC, player),
									ShieldCurioItem.EXPLOSION_DAMAGE);
						}
						stack.set(LUX_CHARGE, luxCharge - ShieldCurioItem.EXPLOSION_COST);
					}
				}
			} else {
				shieldCharge -= damageReduction;
				stack.set(SHIELD_CHARGE, shieldCharge);
			}

			event.setNewDamage(event.getNewDamage() - damageReduction);
			return true;
		});
	}

	@SubscribeEvent
	public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
		if (!event.getLevel().isClientSide
				&& event.getEntity() instanceof AbstractArrow arrow
				&& arrow.getOwner() instanceof Player player
				&& (arrow instanceof Arrow || arrow instanceof SpectralArrow)) {
			findCurioItemAndDo(Accessories.FIRE_ARROW_RING, player, stack -> {
				arrow.igniteForSeconds(100);
				arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
				return true;
			});
		}
	}

	@SubscribeEvent
	public static void livingVisibility(LivingEvent.LivingVisibilityEvent event) {
		findCurioItemAndDo(Accessories.INVISIBILITY_RING, event.getEntity(), stack -> {
			if (!stack.getOrDefault(ACTIVE, false)) return false;
			event.modifyVisibility(event.getVisibilityModifier() * 0.25);
			return true;
		});
	}

	private static boolean findCurioItemAndDo(@NotNull Accessories item, @NotNull LivingEntity entity,
	                                          @NotNull Predicate<ItemStack> action) {
		ICuriosItemHandler h = CuriosApi.getCuriosInventory(entity).orElse(null);
		if (h == null) return false;

		ICurioStacksHandler curios = h.getCurios().get(item.curioSlot());
		if (curios == null) return false;

		IDynamicStackHandler stacks = curios.getStacks();
		for (int i = 0; i < stacks.getSlots(); ++i) {
			ItemStack stack = stacks.getStackInSlot(i);
			if (stack.is(item.asItem()) && action.test(stack)) return true;
		}

		return false;
	}
}
