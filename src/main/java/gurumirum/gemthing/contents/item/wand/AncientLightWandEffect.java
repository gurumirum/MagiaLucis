package gurumirum.gemthing.contents.item.wand;

import gurumirum.gemthing.client.WandEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static gurumirum.gemthing.GemthingMod.id;

public class AncientLightWandEffect extends WandEffect.SpinningTipEffect {
	public static final AncientLightWandEffect INSTANCE = new AncientLightWandEffect();

	private static final ResourceLocation TEXTURE = id("textures/effect/wand_beam_start.png");

	@Override
	protected ResourceLocation texture(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective) {
		return TEXTURE;
	}
}
