package gurumirum.magialucis.contents.item.accessory;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.Accessories;
import gurumirum.magialucis.contents.ModDataComponents;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.function.Consumer;
import java.util.function.Function;

@EventBusSubscriber(modid = MagiaLucisMod.MODID)
public final class AccessoryEventListener {
	public static final int COST_PER_FIRE_RESISTANCE = 5;

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onEntityInvulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {
		if (event.isInvulnerable()) return;
		if (!(event.getEntity() instanceof Player player)) return;

		if ((event.getSource().is(DamageTypes.ON_FIRE) || event.getSource().is(DamageTypes.IN_FIRE)) &&
				!player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
			findCurioItemAndDo(Accessories.FIRE_IMMUNE_BRACELET, player, stack -> COST_PER_FIRE_RESISTANCE,
					stack -> event.setInvulnerable(true));
		}
	}

	@SubscribeEvent
	public static void onEntityHurt(LivingDamageEvent.Pre event) {
		if (!(event.getEntity() instanceof Player player)) return;

		findCurioItemAndDo(Accessories.DAMAGE_ABSORB_NECKLACE, player, stack -> {
			double absorbedDamage = stack.getOrDefault(ModDataComponents.ABSORBED_DAMAGE, .0);
			return absorbedDamage >= DamageAbsorbNecklaceItem.ABSORBABLE_DAMAGE_TOTAL ? DamageAbsorbNecklaceItem.COST_PER_IMPACT : DamageAbsorbNecklaceItem.COST_PER_ABSORB;
		}, stack -> {
			double absorbedDamage = stack.getOrDefault(ModDataComponents.ABSORBED_DAMAGE, .0);
			if (absorbedDamage >= DamageAbsorbNecklaceItem.ABSORBABLE_DAMAGE_TOTAL) {
				stack.set(ModDataComponents.ABSORBED_DAMAGE, .0);
				player.level().getEntities(EntityTypeTest.forClass(Mob.class), player.getBoundingBox().inflate(4), LivingEntity::isAlive).forEach(entity -> {
					entity.hurt(player.damageSources().source(DamageTypes.MAGIC, player), DamageAbsorbNecklaceItem.IMPACT_DAMAGE);
					// Do visual? sth? idk
				});
			} else {
				double damage = event.getOriginalDamage() < DamageAbsorbNecklaceItem.ABSORBABLE_DAMAGE_PER_ATTACK ? event.getOriginalDamage() : DamageAbsorbNecklaceItem.ABSORBABLE_DAMAGE_PER_ATTACK;
				event.setNewDamage((float)(event.getOriginalDamage() - damage));
				stack.set(ModDataComponents.ABSORBED_DAMAGE, absorbedDamage + damage);
			}
		});

	}

	private static void findCurioItemAndDo(@NotNull Accessories item, @NotNull Player player, Function<ItemStack, Integer> luxFunction, @NotNull Consumer<ItemStack> consumer) {
		CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
			ICurioStacksHandler curios = handler.getCurios().get(item.curioSlot());

			for (int i = 0; i < curios.getSlots(); ++i) {
				ItemStack stack = curios.getStacks().getStackInSlot(i);
				if (!stack.is(item.asItem())) continue;
				long lux = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
				int luxConsume = luxFunction.apply(stack);
				if (lux >= luxConsume) {
					consumer.accept(stack);
					stack.set(ModDataComponents.LUX_CHARGE, lux - luxConsume);
					break;
				}
			}
		});
	}
}
