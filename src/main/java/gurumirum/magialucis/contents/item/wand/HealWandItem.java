package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.client.WandEffect;
import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.contents.item.LuxBatteryItem;
import gurumirum.magialucis.contents.item.WandEffectSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HealWandItem extends LuxBatteryItem implements WandEffectSource {
	public static final int COST_PER_CAST = 20;

	private static final int HEAL_AMOUNT = 10;
	private static final int REGENERATION_EFFECT_DURATION = 100;
	private static final int COOLDOWN = 20;

	public HealWandItem(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		long charge = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
		if (charge < COST_PER_CAST) return InteractionResultHolder.fail(stack);
		player.startUsingItem(usedHand);
		return InteractionResultHolder.consume(player.getItemInHand(usedHand));
	}

	@Override
	public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity) {
		if (!level.isClientSide) {
			long charge = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
			if (charge >= COST_PER_CAST) {
				for (LivingEntity entity : level.getEntities(EntityTypeTest.forClass(LivingEntity.class),
						livingEntity.getBoundingBox().inflate(5f), e -> !(e instanceof Enemy))) {
					entity.heal(HEAL_AMOUNT);
					entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, REGENERATION_EFFECT_DURATION));
				}
				stack.set(Contents.LUX_CHARGE, charge - COST_PER_CAST);
			}
		}

		if (livingEntity instanceof Player player) {
			player.getCooldowns().addCooldown(this, COOLDOWN);
		}

		return stack;
	}

	@Override
	public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
		return 80;
	}

	@Override
	public @Nullable WandEffect getWandEffect(Player player, ItemStack stack) {
		return HealWandEffect.INSTANCE;
	}
}
