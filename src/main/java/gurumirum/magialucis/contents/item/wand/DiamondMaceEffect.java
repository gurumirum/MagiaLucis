package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.client.RotationLogic;
import gurumirum.magialucis.client.WandEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static gurumirum.magialucis.MagiaLucisMod.id;

public class DiamondMaceEffect extends WandEffect.SpinningTipEffect {
	public static final DiamondMaceEffect INSTANCE = new DiamondMaceEffect();

	private static final long ROTATION_PERIOD = 400;
	private static final ResourceLocation TEXTURE = id("textures/wand_effect/diamond_mace.png");

	@Override
	protected void getRotation(Player player, ItemStack stack, boolean firstPersonPerspective, float partialTicks,
	                           Quaternionf dest) {
		dest.rotateY((float)Math.PI / 2);
		dest.rotateX(RotationLogic.rotation(player.level().getGameTime(), ROTATION_PERIOD, partialTicks));
	}

	@Override
	protected void offset(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective, Vector3f dest) {
		dest.set(12.5, 12.5, 8f);
	}

	@Override
	protected ResourceLocation texture(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective) {
		return TEXTURE;
	}

	@Override
	protected float scale(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective) {
		return firstPersonPerspective ?  31 / 32f :19 / 32f;
	}
}
