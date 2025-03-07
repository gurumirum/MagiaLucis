package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.client.render.WandEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import static gurumirum.magialucis.api.MagiaLucisApi.id;

public class AncientLightWandEffect extends WandEffect.SpinningTipEffect {
	public static final AncientLightWandEffect INSTANCE = new AncientLightWandEffect();

	private static final ResourceLocation TEXTURE = id("textures/wand_effect/ancient_light.png");

	@Override
	protected void offset(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective, Vector3f dest) {
		dest.set(14, 14, 8);
	}

	@Override
	protected float scale(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective) {
		return .5f;
	}

	@Override
	protected ResourceLocation texture(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective) {
		return TEXTURE;
	}
}
