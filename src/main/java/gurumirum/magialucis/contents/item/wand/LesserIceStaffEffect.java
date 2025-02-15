package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.client.RotationLogic;
import gurumirum.magialucis.client.render.WandEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

public class LesserIceStaffEffect extends WandEffect.SpinningTipEffect {
	public static final LesserIceStaffEffect INSTANCE = new LesserIceStaffEffect();

	private static final ResourceLocation TEXTURE_CHARGING = MagiaLucisMod.id("textures/wand_effect/lesser_ice_charging.png");
	private static final ResourceLocation TEXTURE_CHARGED_BLINKING = MagiaLucisMod.id("textures/wand_effect/lesser_ice_charged_blinking.png");
	private static final ResourceLocation TEXTURE_CHARGED = MagiaLucisMod.id("textures/wand_effect/lesser_ice_charged.png");

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
				ticksUsingItem < LesserIceStaffItem.CHARGE_DURATION ? CHARGING_ROTATION_PERIOD : CHARGED_ROTATION_PERIOD,
				partialTicks));
	}

	@Override
	protected ResourceLocation texture(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective) {
		int ticksUsingItem = player.getTicksUsingItem();
		return ticksUsingItem < LesserIceStaffItem.CHARGE_DURATION ? TEXTURE_CHARGING :
				ticksUsingItem - LesserIceStaffItem.CHARGE_DURATION < 4 ? TEXTURE_CHARGED_BLINKING :
						TEXTURE_CHARGED;
	}
}
