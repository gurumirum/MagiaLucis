package gurumirum.magialucis.contents.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gurumirum.magialucis.api.MagiaLucisApi;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class EnderChestPortalRenderer extends EntityRenderer<EnderChestPortal> {
	private static final ResourceLocation TEXTURE = MagiaLucisApi.id("textures/entity/ender_chest_portal.png");

	public EnderChestPortalRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(@NotNull EnderChestPortal entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		poseStack.pushPose();
		poseStack.translate(0, entity.getBbHeight() / 2, 0);
		poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

		float tick = (entity.tickCount + partialTick) / 20;

		double n = 0.5;
		double pow = Math.pow(Math.E, n - tick);
		double p = (1 + pow) * (1 + pow) * Math.sin(Math.PI * 2 * (tick - pow)) * 0.1;
		float scale = 1;

		poseStack.scale((float)(Math.pow(2, p) * scale), (float)(Math.pow(2, -p) * scale), 1);

		VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));

		int spriteIndex = entity.tickCount / 4 % 8;
		float v1 = (spriteIndex) / 8f;
		float v2 = (spriteIndex + 1) / 8f;

		vertex(poseStack.last(), vc, -.5f, -.5f, 0, 1, v1, 0, 0, 1);
		vertex(poseStack.last(), vc, .5f, -.5f, 0, 0, v1, 0, 0, 1);
		vertex(poseStack.last(), vc, .5f, .5f, 0, 0, v2, 0, 0, 1);
		vertex(poseStack.last(), vc, -.5f, .5f, 0, 1, v2, 0, 0, 1);

		poseStack.popPose();
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull EnderChestPortal entity) {
		return TEXTURE;
	}

	private static void vertex(PoseStack.Pose pose, VertexConsumer consumer,
	                           float x, float y, float z,
	                           float u, float v,
	                           float normalX, float normalY, float normalZ) {
		consumer.addVertex(pose, x, y, z)
				.setColor(-1)
				.setUv(u, v)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(LightTexture.FULL_BRIGHT)
				.setNormal(pose, normalX, normalZ, normalY);
	}
}
