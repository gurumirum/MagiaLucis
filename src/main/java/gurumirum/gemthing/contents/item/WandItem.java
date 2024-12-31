package gurumirum.gemthing.contents.item;

import gurumirum.gemthing.impl.InWorldBeamCraftingManager;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class WandItem extends Item {
	public static final double DISTANCE = 10;

	public WandItem(Properties properties) {
		super(properties.stacksTo(1));
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		player.startUsingItem(hand);
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public void onUseTick(@NotNull Level level, @NotNull LivingEntity entity, @NotNull ItemStack stack, int remainingUseDuration) {
		if (!level.isClientSide || !(entity instanceof LocalPlayer player)) return;
		Vec3 start = player.getEyePosition();
		Vec3 end = start.add(player.getLookAngle().scale(DISTANCE));
		BlockHitResult hitResult = trace(player, start, end);
		InWorldBeamCraftingManager.setFocus(player, hitResult.getType() == HitResult.Type.BLOCK ? hitResult.getBlockPos() : null);
	}

	@Override
	public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
		if (entity instanceof Player player) InWorldBeamCraftingManager.removeFocus(player);
		return stack;
	}

	@Override
	public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity, int timeCharged) {
		if (entity instanceof Player player) InWorldBeamCraftingManager.removeFocus(player);
	}

	@Override
	public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
		return UseAnim.NONE;
	}

	@Override
	public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
		return 72000;
	}

	public static BlockHitResult trace(Player player, Vec3 start, Vec3 end) {
		return player.level().clip(new ClipContext(start, end,
				ClipContext.Block.VISUAL, ClipContext.Fluid.ANY,
				player));
	}
}
