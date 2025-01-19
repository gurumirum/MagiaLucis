package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.client.WandEffect;
import gurumirum.magialucis.client.render.BeamRender;
import gurumirum.magialucis.contents.item.BeamSource;
import gurumirum.magialucis.contents.item.WandEffectSource;
import gurumirum.magialucis.impl.ancientlight.AncientLightCrafting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AncientLightWandItem extends Item implements BeamSource, WandEffectSource {
	public static final double DISTANCE = 10;

	public AncientLightWandItem(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		player.startUsingItem(hand);
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public void onUseTick(@NotNull Level level, @NotNull LivingEntity entity, @NotNull ItemStack stack, int remainingUseDuration) {
		if (!level.isClientSide ||
				!(entity instanceof LocalPlayer player) ||
				!canProduceBeam(player, stack, player.getUsedItemHand())) return;

		Vec3 start = player.getEyePosition();
		Vec3 end = start.add(player.getLookAngle().scale(DISTANCE));
		BlockHitResult hitResult = BeamSource.trace(player, start, end);
		AncientLightCrafting.getLocalManager()
				.setFocus(player, hitResult.getType() == HitResult.Type.BLOCK ? hitResult.getBlockPos() : null);

		BeamRender.addBeamEffect(player, stack, this);
	}

	@Override
	public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
		if (entity instanceof Player player && level.isClientSide)
			AncientLightCrafting.getLocalManager().removeFocus(player);
		return stack;
	}

	@Override
	public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity, int timeCharged) {
		if (entity instanceof Player player && level.isClientSide)
			AncientLightCrafting.getLocalManager().removeFocus(player);
	}

	@Override
	public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
		return UseAnim.NONE;
	}

	@Override
	public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
		return 72000;
	}

	@Override
	public int beamColor(Player player, ItemStack stack, boolean firstPersonPerspective, float partialTicks) {
		return 0x80FFFFFF;
	}

	@Override
	public float beamDiameter(Player player, ItemStack stack, boolean firstPersonPerspective, float partialTicks) {
		return .25f;
	}

	@Override
	public int beamStartDelay(Player player, ItemStack stack) {
		return 10;
	}

	@Override
	public @Nullable WandEffect getWandEffect(Player player, ItemStack stack, InteractionHand hand) {
		return player.isUsingItem() && player.getUsedItemHand() == hand ? AncientLightWandEffect.INSTANCE : null;
	}
}
