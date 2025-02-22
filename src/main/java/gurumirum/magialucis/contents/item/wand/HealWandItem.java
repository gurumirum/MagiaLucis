package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.api.capability.LuxContainerStat;
import gurumirum.magialucis.api.item.WandEffectSource;
import gurumirum.magialucis.client.render.WandEffect;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.augment.TieredAugmentTypes;
import gurumirum.magialucis.contents.item.LuxContainerItem;
import gurumirum.magialucis.impl.LuxStatTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HealWandItem extends LuxContainerItem implements WandEffectSource {
	private static final int HEAL_AMOUNT = 10;
	private static final int REGENERATION_EFFECT_DURATION = 100;
	private static final int COOLDOWN = 20;

	public static final int COST = 1000;

	public static final double COST_MULTIPLIER_QC1 = 2.5;
	public static final double COST_MULTIPLIER_QC2 = 5;
	public static final double COST_MULTIPLIER_QC3 = 10;

	public static final int USE_DURATION = 80;
	public static final int USE_DURATION_QC1 = 60;
	public static final int USE_DURATION_QC2 = 40;
	public static final int USE_DURATION_QC3 = 20;

	public HealWandItem(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		long charge = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
		if (charge < cost(stack)) return InteractionResultHolder.fail(stack);
		player.startUsingItem(usedHand);
		return InteractionResultHolder.consume(player.getItemInHand(usedHand));
	}

	@Override
	public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity) {
		if (!level.isClientSide) {
			long charge = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
			int cost = cost(stack);
			if (charge >= cost) {
				for (LivingEntity entity : level.getEntities(EntityTypeTest.forClass(LivingEntity.class),
						livingEntity.getBoundingBox().inflate(5f), e -> !(e instanceof Enemy))) {
					entity.heal(HEAL_AMOUNT);
					entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, REGENERATION_EFFECT_DURATION));
				}
				stack.set(ModDataComponents.LUX_CHARGE, charge - cost);
			}
		}

		if (livingEntity instanceof Player player) {
			player.getCooldowns().addCooldown(this, COOLDOWN);
		}

		return stack;
	}

	@Override
	public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
		return switch (TieredAugmentTypes.QUICK_CAST.getTier(stack)) {
			case 0 -> USE_DURATION_QC1;
			case 1 -> USE_DURATION_QC2;
			case 2 -> USE_DURATION_QC3;
			default -> USE_DURATION;
		};
	}

	@Override
	public @Nullable WandEffect getWandEffect(Player player, ItemStack stack, InteractionHand hand) {
		return player.isUsingItem() && player.getUsedItemHand() == hand ? HealWandEffect.INSTANCE : null;
	}

	@Override
	protected void appendLuxContainerDescription(@NotNull ItemStack stack, @NotNull TooltipContext context,
	                                             @NotNull List<Component> tooltip, @NotNull TooltipFlag flag,
	                                             @NotNull LuxContainerStat luxContainerStat) {
		super.appendLuxContainerDescription(stack, context, tooltip, flag, luxContainerStat);
		tooltip.add(LuxStatTooltip.luxConsumptionPerUse(cost(stack), luxContainerStat.maxCharge()));
	}

	@SuppressWarnings("lossy-conversions")
	public static int cost(@NotNull ItemStack stack) {
		int cost = COST;
		switch (TieredAugmentTypes.QUICK_CAST.getTier(stack)) {
			case 0 -> cost *= COST_MULTIPLIER_QC1;
			case 1 -> cost *= COST_MULTIPLIER_QC2;
			case 2 -> cost *= COST_MULTIPLIER_QC3;
		}
		return cost;
	}
}
