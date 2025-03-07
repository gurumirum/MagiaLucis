package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.client.RotationLogic;
import gurumirum.magialucis.client.render.WandEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

public class LesserIceStaffEffect extends WandEffect.SpinningTipEffect {
	public static final LesserIceStaffEffect INSTANCE = new LesserIceStaffEffect();

	private static final ResourceLocation TEXTURE_CHARGING = MagiaLucisApi.id("textures/wand_effect/lesser_ice_charging.png");
	private static final ResourceLocation TEXTURE_CHARGED_BLINKING = MagiaLucisApi.id("textures/wand_effect/lesser_ice_charged_blinking.png");
	private static final ResourceLocation TEXTURE_CHARGED = MagiaLucisApi.id("textures/wand_effect/lesser_ice_charged.png");

	private static final long CHARGING_ROTATION_PERIOD = 10;
	private static final long CHARGED_ROTATION_PERIOD = 15;

	@Override
	protected float scale(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective) {
		return 15 / 32f;
	}

	@Override
	protected void getRotation(Player player, ItemStack stack, boolean firstPersonPerspective, float partialTicks, Quaternionf dest) {
		int ticksUsingItem = player.getTicksUsingItem();
		dest.rotateX(-RotationLogic.rotation(ticksUsingItem,
				ticksUsingItem < LesserIceStaffItem.chargeDuration(stack) ? CHARGING_ROTATION_PERIOD : CHARGED_ROTATION_PERIOD,
				partialTicks));
	}

	@Override
	protected ResourceLocation texture(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective) {
		int t = player.getTicksUsingItem() - LesserIceStaffItem.chargeDuration(stack);
		return t < 0 ? TEXTURE_CHARGING : t < 4 ? TEXTURE_CHARGED_BLINKING : TEXTURE_CHARGED;
	}
}
