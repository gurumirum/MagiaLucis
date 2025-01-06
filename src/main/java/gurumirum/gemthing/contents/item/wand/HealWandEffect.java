package gurumirum.gemthing.contents.item.wand;

import gurumirum.gemthing.client.WandEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import static gurumirum.gemthing.GemthingMod.id;

public class HealWandEffect extends WandEffect.SpinningTipEffect {
	public static final HealWandEffect INSTANCE = new HealWandEffect();

	private static final ResourceLocation TEXTURE = id("textures/wand_effect/heal.png");

	@Override
	protected void offset(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective, Vector3f dest) {
		dest.set(15, 15, 8f);
	}

	@Override
	protected double getRotationDegrees(Player player, ItemStack stack, int ticksUsingItem, boolean firstPersonPerspective) {
		return ticksUsingItem * 4;
	}

	@Override
	protected ResourceLocation texture(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective) {
		return TEXTURE;
	}
}
