package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.client.RotationLogic;
import gurumirum.magialucis.client.WandEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static gurumirum.magialucis.MagiaLucisMod.id;

public class HealWandEffect extends WandEffect.SpinningTipEffect {
	public static final HealWandEffect INSTANCE = new HealWandEffect();

	private static final long ROTATION_PERIOD = 90;
	private static final ResourceLocation TEXTURE = id("textures/wand_effect/heal.png");

	@Override
	protected void offset(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective, Vector3f dest) {
		dest.set(15, 15, 8f);
	}

	@Override
	protected void getRotation(Player player, ItemStack stack, boolean firstPersonPerspective, float partialTicks,
	                           Quaternionf dest) {
		dest.rotateX(-RotationLogic.rotation(player.getTicksUsingItem(), ROTATION_PERIOD, partialTicks) * deg2rad);
	}

	@Override
	protected ResourceLocation texture(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective) {
		return TEXTURE;
	}
}
