package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.client.render.WandEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import static gurumirum.magialucis.api.MagiaLucisApi.id;

public class RecallStaffWandEffect extends WandEffect.SpinningTipEffect {
	public static final RecallStaffWandEffect INSTANCE = new RecallStaffWandEffect();

	private static final ResourceLocation TEXTURE = id("textures/wand_effect/recall.png");

	@Override
	protected void offset(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective, Vector3f dest) {
		dest.set(13.5f, 13.5f, 8f);
	}

	@Override
	protected ResourceLocation texture(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective) {
		return TEXTURE;
	}
}
