package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.client.WandEffect;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.ModMobEffects;
import gurumirum.magialucis.contents.item.LuxBatteryItem;
import gurumirum.magialucis.contents.item.WandEffectSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RecallStaffWandItem extends LuxBatteryItem implements WandEffectSource {
	public static final int COST_PER_RECALL = 100;
	public static final int RECALL_FATIGUE_DURATION = 20 * 75;

	public RecallStaffWandItem(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);

		if (player.hasEffect(ModMobEffects.RECALL_FATIGUE) || stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L) < COST_PER_RECALL)
			return InteractionResultHolder.fail(stack);

		player.startUsingItem(usedHand);
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int remainingUseDuration) {
		if (!level.isClientSide && (livingEntity.hasEffect(ModMobEffects.RECALL_FATIGUE) ||
				stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L) < COST_PER_RECALL)) {
			livingEntity.stopUsingItem();
		}
	}

	@Override
	public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
		if (entity instanceof ServerPlayer player) {
			long luxCharge = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
			if (luxCharge < COST_PER_RECALL) return stack;

			DimensionTransition transition = player.findRespawnPositionAndUseSpawnBlock(true, DimensionTransition.DO_NOTHING);
			player.teleportTo(transition.newLevel(), transition.pos().x, transition.pos().y, transition.pos().z, transition.yRot(), transition.xRot());
			player.addEffect(new MobEffectInstance(ModMobEffects.RECALL_FATIGUE, RECALL_FATIGUE_DURATION, 0, false, false));
			stack.set(ModDataComponents.LUX_CHARGE, luxCharge - COST_PER_RECALL);
		}
		return stack;
	}

	@Override
	public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
		return UseAnim.NONE;
	}

	@Override
	public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
		return 160;
	}

	@Override
	public @Nullable WandEffect getWandEffect(Player player, ItemStack stack, InteractionHand hand) {
		return player.isUsingItem() && player.getUsedItemHand() == hand ? RecallStaffWandEffect.INSTANCE : null;
	}
}
