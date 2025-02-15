package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.client.render.WandEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class LapisShieldEffect extends WandEffect.SpinningTipEffect {
	public static final LapisShieldEffect INSTANCE = new LapisShieldEffect();

	private static final ResourceLocation[] TEXTURES = new ResourceLocation[6];

	static {
		for (int i = 0; i < TEXTURES.length; i++) {
			TEXTURES[i] = MagiaLucisMod.id("textures/wand_effect/shield_" + i + ".png");
		}
	}

	@Override
	protected void offset(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective, Vector3f dest) {
		dest.set(8, 8, 4);
	}

	@Override
	protected void getRotation(Player player, ItemStack stack, boolean firstPersonPerspective, float partialTicks, Quaternionf dest) {
		dest.rotateY((float)Math.PI / 2);
		dest.rotateX((float)-Math.PI * (5 / 4.0f));
	}

	@Override
	protected float scale(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective) {
		return firstPersonPerspective ? 1 : 1.5f;
	}

	@Override
	protected ResourceLocation texture(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective) {
		int tick = player.getTicksUsingItem();
		int i = (tick / 5) % 4;

		if (tick > 10 && i < 2) i += 4;

		return TEXTURES[i];
	}
}
