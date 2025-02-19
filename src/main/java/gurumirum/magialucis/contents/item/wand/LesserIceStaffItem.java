package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.api.item.WandEffectSource;
import gurumirum.magialucis.client.render.WandEffect;
import gurumirum.magialucis.contents.Augments;
import gurumirum.magialucis.contents.data.AugmentLogic;
import gurumirum.magialucis.contents.entity.LesserIceProjectile;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LesserIceStaffItem extends Item implements WandEffectSource {
	public static final int CHARGE_DURATION = 40;
	public static final int CHARGE_DURATION_QC = 20;

	public LesserIceStaffItem(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
		player.startUsingItem(usedHand);
		return InteractionResultHolder.consume(player.getItemInHand(usedHand));
	}

	@Override
	public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity, int timeCharged) {
		if (level.isClientSide || stack.getUseDuration(livingEntity) - timeCharged < chargeDuration(stack)) return;

		Vec3 lookAngle = livingEntity.getLookAngle();

		LesserIceProjectile projectile = new LesserIceProjectile(livingEntity, level);
		projectile.setPos(
				projectile.getX() + lookAngle.x * 0.2,
				projectile.getY() + lookAngle.y * 0.2,
				projectile.getZ() + lookAngle.z * 0.2);
		projectile.shootFromRotation(livingEntity,
				livingEntity.getXRot(), livingEntity.getYRot(), 0,
				1.5f, 1.0f);
		level.addFreshEntity(projectile);
	}

	@Override
	public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
		return 72000;
	}

	@Override
	public @Nullable WandEffect getWandEffect(Player player, ItemStack stack, InteractionHand hand) {
		return player.isUsingItem() && player.getUsedItemHand() == hand ? LesserIceStaffEffect.INSTANCE : null;
	}

	public static int chargeDuration(@NotNull ItemStack stack) {
		return AugmentLogic.getAugments(stack).has(Augments.QUICK_CAST_1) ? CHARGE_DURATION_QC : CHARGE_DURATION;
	}
}
