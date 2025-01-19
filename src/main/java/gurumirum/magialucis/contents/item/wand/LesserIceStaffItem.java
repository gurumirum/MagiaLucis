package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.client.WandEffect;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.entity.LesserIceProjectile;
import gurumirum.magialucis.contents.item.LuxContainerItem;
import gurumirum.magialucis.contents.item.WandEffectSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LesserIceStaffItem extends LuxContainerItem implements WandEffectSource {
	public static final int COST_PER_ATTACK = 2;
	public static final int CHARGE_DURATION = 40;

	public LesserIceStaffItem(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		if (stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L) < COST_PER_ATTACK)
			return InteractionResultHolder.fail(stack);

		player.startUsingItem(usedHand);
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity, int timeCharged) {
		if (level.isClientSide || stack.getUseDuration(livingEntity) - timeCharged < CHARGE_DURATION) return;

		long charge = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
		if (charge < COST_PER_ATTACK) return;

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

		stack.set(ModDataComponents.LUX_CHARGE, charge - COST_PER_ATTACK);
	}

	@Override
	public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
		return 72000;
	}

	@Override
	public @Nullable WandEffect getWandEffect(Player player, ItemStack stack, InteractionHand hand) {
		return player.isUsingItem() && player.getUsedItemHand() == hand ? LesserIceStaffEffect.INSTANCE : null;
	}
}
