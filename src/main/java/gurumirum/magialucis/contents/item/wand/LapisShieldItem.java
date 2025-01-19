package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.client.WandEffect;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.item.LuxContainerItem;
import gurumirum.magialucis.contents.item.WandEffectSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LapisShieldItem extends LuxContainerItem implements WandEffectSource {
	public static final int COST_PER_SHIELDING_TICK = 1;
	public static final int COST_PER_BLOCK = 5;
	public static final int COST_PER_SHIELD_DISABLE = 50;

	public LapisShieldItem(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		if (stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L) < COST_PER_BLOCK)
			return InteractionResultHolder.fail(stack);

		player.startUsingItem(usedHand);
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int remainingUseDuration) {
		if (level.isClientSide()) return;
		if (getUseDuration(stack, livingEntity) - remainingUseDuration < 5) return;

		long lux = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
		if (lux < COST_PER_BLOCK) livingEntity.stopUsingItem();
		else stack.set(ModDataComponents.LUX_CHARGE, lux - COST_PER_SHIELDING_TICK);
	}

	@Override
	public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
		return 72000;
	}

	@Override
	public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
		return UseAnim.BLOCK;
	}

	@Override
	public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ItemAbility itemAbility) {
		return itemAbility == ItemAbilities.SHIELD_BLOCK;
	}

	@Override
	public @Nullable WandEffect getWandEffect(Player player, ItemStack stack, InteractionHand hand) {
		return player.isUsingItem() && player.getUsedItemHand() == hand ? LapisShieldEffect.INSTANCE : null;
	}
}
