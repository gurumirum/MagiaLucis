package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.api.augment.Augment;
import gurumirum.magialucis.api.item.AugmentTooltipProvider;
import gurumirum.magialucis.client.render.WandEffect;
import gurumirum.magialucis.client.render.beam.BeamRender;
import gurumirum.magialucis.api.item.BeamSource;
import gurumirum.magialucis.api.item.WandEffectSource;
import gurumirum.magialucis.contents.Augments;
import gurumirum.magialucis.contents.data.AugmentLogic;
import gurumirum.magialucis.impl.ancientlight.AncientLightCrafting;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import gurumirum.magialucis.utils.NumberFormats;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AncientLightWandItem extends Item implements BeamSource, WandEffectSource, AugmentTooltipProvider {
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
				!(entity instanceof Player player) ||
				!canProduceBeam(player, stack, player.getUsedItemHand())) return;

		Vec3 start = player.getEyePosition();
		Vec3 end = start.add(player.getLookAngle().scale(DISTANCE));
		BlockHitResult hitResult = BeamSource.trace(player, start, end);
		AncientLightCrafting.getLocalManager()
				.setFocus(player, hitResult.getType() == HitResult.Type.BLOCK ? hitResult.getBlockPos() : null);

		BeamRender.addBeamEffect(player, stack, this);

		if (hitResult.getType() == HitResult.Type.BLOCK) {
			Vec3 l = hitResult.getLocation();
			for (int i = 0; i < 2; i++)
				LuxUtils.addSpreadingLightParticle(level, l.x, l.y, l.z, 0.1, 0.1f);
		}
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

	@Override
	public boolean appendHoverTextForAugment(@NotNull TooltipContext context, @Nullable Player player,
	                                         @NotNull ItemStack stack, @NotNull List<Component> tooltip,
	                                         @NotNull TooltipFlag flag, @NotNull Holder<Augment> augment) {
		if (Augments.SPEED_1.is(augment)) {
			tooltip.add(AugmentLogic.augmentDesc(Component.translatable(
					"item.magialucis.ancient_light.tooltip.augment.speed",
					NumberFormats.pct(.5, ChatFormatting.YELLOW))));
			return true;
		} else return false;
	}
}
