package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.api.capability.LuxContainerStat;
import gurumirum.magialucis.api.item.WandEffectSource;
import gurumirum.magialucis.client.render.WandEffect;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.ModMobEffects;
import gurumirum.magialucis.contents.augment.TieredAugmentTypes;
import gurumirum.magialucis.contents.item.LuxContainerItem;
import gurumirum.magialucis.impl.LuxStatTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RecallStaffItem extends LuxContainerItem implements WandEffectSource {
	public static final int RECALL_FATIGUE_DURATION = 20 * 75;

	public static final int COST = 800;

	public static final double COST_MULTIPLIER_QC1 = 1.5;
	public static final double COST_MULTIPLIER_QC2 = 2;
	public static final double COST_MULTIPLIER_QC3 = 3;

	public static final int USE_DURATION = 160;
	public static final int USE_DURATION_QC1 = 120;
	public static final int USE_DURATION_QC2 = 80;
	public static final int USE_DURATION_QC3 = 40;

	public RecallStaffItem(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		if (!canUse(stack, player)) return InteractionResultHolder.fail(stack);
		player.startUsingItem(usedHand);
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int remainingUseDuration) {
		if (!level.isClientSide && !canUse(stack, livingEntity)) livingEntity.stopUsingItem();
	}

	@Override
	public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
		if (entity instanceof ServerPlayer player) {
			if (!canUse(stack, entity)) return stack;

			DimensionTransition transition = player.findRespawnPositionAndUseSpawnBlock(true, DimensionTransition.DO_NOTHING);
			player.teleportTo(transition.newLevel(), transition.pos().x, transition.pos().y, transition.pos().z, transition.yRot(), transition.xRot());
			player.addEffect(new MobEffectInstance(ModMobEffects.RECALL_FATIGUE, RECALL_FATIGUE_DURATION, 0, false, false));

			long luxCharge = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
			stack.set(ModDataComponents.LUX_CHARGE, luxCharge - cost(stack));
		}
		return stack;
	}

	@Override
	public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
		return UseAnim.NONE;
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
		return player.isUsingItem() && player.getUsedItemHand() == hand ? RecallStaffEffect.INSTANCE : null;
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

	public static boolean canUse(@NotNull ItemStack stack, @Nullable LivingEntity entity) {
		return stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L) >= cost(stack) &&
				(entity == null || !entity.hasEffect(ModMobEffects.RECALL_FATIGUE));
	}
}
