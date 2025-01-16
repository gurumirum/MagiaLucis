package gurumirum.magialucis.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gurumirum.magialucis.contents.item.WandEffectSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@FunctionalInterface
public interface WandEffect {
	@OnlyIn(Dist.CLIENT)
	void render(PoseStack poseStack, Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective);

	static @Nullable WandEffect from(ItemStack stack, Player player, InteractionHand hand) {
		return stack.getItem() instanceof WandEffectSource wandEffectSource ? wandEffectSource.getWandEffect(player, stack, hand) : null;
	}

	abstract class SpinningTipEffect implements WandEffect {
		public static final long DEFAULT_ROTATION_PERIOD = 18;

		private final Vector3f offset = new Vector3f();
		private final Quaternionf rot = new Quaternionf();

		protected void offset(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective, Vector3f dest) {}

		protected float scale(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective) {
			return 1;
		}

		protected void getRotation(Player player, ItemStack stack, boolean firstPersonPerspective, float partialTicks, Quaternionf dest) {
			dest.rotateX(-RotationLogic.rotation(player.getTicksUsingItem(), DEFAULT_ROTATION_PERIOD, partialTicks));
		}

		protected void draw(PoseStack poseStack, Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective) {
			MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

			int color = color(player, stack, partialTicks, firstPersonPerspective);
			VertexConsumer vc = bufferSource.getBuffer(ModRenderTypes.positionTextureColor(
					texture(player, stack, partialTicks, firstPersonPerspective)));
			vc.addVertex(poseStack.last(), 0, -1, -1).setUv(0, 0).setColor(color);
			vc.addVertex(poseStack.last(), 0, 1, -1).setUv(1, 0).setColor(color);
			vc.addVertex(poseStack.last(), 0, 1, 1).setUv(1, 1).setColor(color);
			vc.addVertex(poseStack.last(), 0, -1, 1).setUv(0, 1).setColor(color);
		}

		protected abstract ResourceLocation texture(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective);

		protected int color(Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective) {
			return -1;
		}

		@Override
		public void render(PoseStack poseStack, Player player, ItemStack stack, float partialTicks, boolean firstPersonPerspective) {
			offset(player, stack, partialTicks, firstPersonPerspective, this.offset.set(16, 16, 8));
			poseStack.translate(this.offset.x / 16, this.offset.y / 16, this.offset.z / 16);

			float scale = scale(player, stack, partialTicks, firstPersonPerspective);
			if (scale != 1) poseStack.scale(scale, scale, scale);

			getRotation(player, stack, firstPersonPerspective, partialTicks, this.rot.identity().rotateZ((float)(Math.PI / 4)));
			poseStack.mulPose(this.rot);

			draw(poseStack, player, stack, partialTicks, firstPersonPerspective);
		}
	}
}
