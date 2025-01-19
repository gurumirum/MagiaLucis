package gurumirum.magialucis.contents.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import gurumirum.magialucis.MagiaLucisMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static gurumirum.magialucis.contents.entity.TempleGuardianModel.TEX_HEIGHT;
import static gurumirum.magialucis.contents.entity.TempleGuardianModel.TEX_WIDTH;

public class TempleGuardianRenderer extends MobRenderer<TempleGuardian, TempleGuardianModel<TempleGuardian>> {
	private static final ResourceLocation TEXTURE = MagiaLucisMod.id("textures/entity/temple_guardian.png");

	public TempleGuardianRenderer(EntityRendererProvider.Context context) {
		super(context, new TempleGuardianModel<>(context.bakeLayer(TempleGuardianModel.LAYER)), 0.5f);
		addLayer(new EmissiveLayer(this));
	}

	@Override
	public boolean shouldRender(@NotNull TempleGuardian livingEntity, @NotNull Frustum camera,
	                            double camX, double camY, double camZ) {
		return super.shouldRender(livingEntity, camera, camX, camY, camZ);
	}

	@Override
	public void render(@NotNull TempleGuardian entity, float entityYaw, float partialTicks,
	                   @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

		Minecraft mc = Minecraft.getInstance();

		boolean bodyVisible = isBodyVisible(entity);
		boolean translucent = !bodyVisible && (mc.player == null || !entity.isInvisibleTo(mc.player));
		boolean glowing = mc.shouldEntityAppearGlowing(entity);

		RenderType renderType = getRenderType(entity, bodyVisible, translucent, glowing);

		if (renderType != null) {
			VertexConsumer vc = buffer.getBuffer(renderType);

			double deg2rad = Math.PI / 180.0;
			float ringRotation = (float)(Mth.rotLerp(partialTicks, entity.clientSideRingRotationO, entity.clientSideRingRotation) * deg2rad);

			ring(poseStack, vc, 16, 3, 3, 45, 0, packedLight, true);

			poseStack.pushPose();
			poseStack.translate(0, 6 / 16f, 0);
			poseStack.mulPose(Axis.YP.rotation(ringRotation));

			ring(poseStack, vc, 12, 2, 2, 50, 16, packedLight, false);

			poseStack.popPose();

			poseStack.pushPose();
			poseStack.translate(0, 15 / 16f, 0);
			poseStack.mulPose(Axis.YP.rotation(-ringRotation));

			ring(poseStack, vc, 14, 2, 2, 34, 16, packedLight, false);

			poseStack.popPose();
		}
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull TempleGuardian entity) {
		return TEXTURE;
	}

	private static void ring(PoseStack poseStack, VertexConsumer vc,
	                         float xz, float y, float thickness,
	                         float uOffset, float vOffset,
	                         int packedLight, boolean padding) {
		float xz0 = (-xz / 2) / 16;
		float xz1 = ((-xz / 2) + thickness) / 16;
		float xz2 = ((xz / 2) - thickness) / 16;
		float xz3 = (xz / 2) / 16;

		float y0 = -1 / 128f;
		float y1 = y / 16;

		float u0 = uOffset / TEX_WIDTH;
		float u1 = u0 + thickness / TEX_WIDTH;
		float u2 = u1 + thickness / TEX_WIDTH;
		float u4 = u1 + xz / TEX_WIDTH;
		float u3 = u4 - thickness / TEX_WIDTH;

		float v0 = vOffset / TEX_HEIGHT;
		float v1 = v0 + thickness / TEX_HEIGHT;
		float v3 = v0 + xz / TEX_HEIGHT;
		float v2 = v3 - thickness / TEX_HEIGHT;

		if (padding) {
			xz0 -= 1 / 128f;
			xz3 += 1 / 128f;
			y0 -= 1 / 128f;
		}

		// top
		vertex(poseStack, vc, xz0, y1, xz0, u1, v3, packedLight, 0, 1, 0);
		vertex(poseStack, vc, xz0, y1, xz3, u4, v3, packedLight, 0, 1, 0);
		vertex(poseStack, vc, xz1, y1, xz3, u4, v2, packedLight, 0, 1, 0);
		vertex(poseStack, vc, xz1, y1, xz0, u1, v2, packedLight, 0, 1, 0);

		vertex(poseStack, vc, xz1, y1, xz0, u1, v2, packedLight, 0, 1, 0);
		vertex(poseStack, vc, xz1, y1, xz1, u2, v2, packedLight, 0, 1, 0);
		vertex(poseStack, vc, xz2, y1, xz1, u2, v1, packedLight, 0, 1, 0);
		vertex(poseStack, vc, xz2, y1, xz0, u1, v1, packedLight, 0, 1, 0);

		vertex(poseStack, vc, xz1, y1, xz2, u3, v2, packedLight, 0, 1, 0);
		vertex(poseStack, vc, xz1, y1, xz3, u4, v2, packedLight, 0, 1, 0);
		vertex(poseStack, vc, xz2, y1, xz3, u4, v1, packedLight, 0, 1, 0);
		vertex(poseStack, vc, xz2, y1, xz2, u3, v1, packedLight, 0, 1, 0);

		vertex(poseStack, vc, xz2, y1, xz0, u1, v1, packedLight, 0, 1, 0);
		vertex(poseStack, vc, xz2, y1, xz3, u4, v1, packedLight, 0, 1, 0);
		vertex(poseStack, vc, xz3, y1, xz3, u4, v0, packedLight, 0, 1, 0);
		vertex(poseStack, vc, xz3, y1, xz0, u1, v0, packedLight, 0, 1, 0);

		// bottom
		vertex(poseStack, vc, xz0, y0, xz0, u1, v3, packedLight, 0, -1, 0);
		vertex(poseStack, vc, xz0, y0, xz3, u4, v3, packedLight, 0, -1, 0);
		vertex(poseStack, vc, xz1, y0, xz3, u4, v2, packedLight, 0, -1, 0);
		vertex(poseStack, vc, xz1, y0, xz0, u1, v2, packedLight, 0, -1, 0);

		vertex(poseStack, vc, xz1, y0, xz0, u1, v2, packedLight, 0, -1, 0);
		vertex(poseStack, vc, xz1, y0, xz1, u2, v2, packedLight, 0, -1, 0);
		vertex(poseStack, vc, xz2, y0, xz1, u2, v1, packedLight, 0, -1, 0);
		vertex(poseStack, vc, xz2, y0, xz0, u1, v1, packedLight, 0, -1, 0);

		vertex(poseStack, vc, xz1, y0, xz2, u3, v2, packedLight, 0, -1, 0);
		vertex(poseStack, vc, xz1, y0, xz3, u4, v2, packedLight, 0, -1, 0);
		vertex(poseStack, vc, xz2, y0, xz3, u4, v1, packedLight, 0, -1, 0);
		vertex(poseStack, vc, xz2, y0, xz2, u3, v1, packedLight, 0, -1, 0);

		vertex(poseStack, vc, xz2, y0, xz0, u1, v1, packedLight, 0, -1, 0);
		vertex(poseStack, vc, xz2, y0, xz3, u4, v1, packedLight, 0, -1, 0);
		vertex(poseStack, vc, xz3, y0, xz3, u4, v0, packedLight, 0, -1, 0);
		vertex(poseStack, vc, xz3, y0, xz0, u1, v0, packedLight, 0, -1, 0);

		// outer side
		vertex(poseStack, vc, xz0, y1, xz0, u1, v0, packedLight, -1, 0, 0);
		vertex(poseStack, vc, xz0, y0, xz0, u0, v0, packedLight, -1, 0, 0);
		vertex(poseStack, vc, xz0, y0, xz3, u0, v3, packedLight, -1, 0, 0);
		vertex(poseStack, vc, xz0, y1, xz3, u1, v3, packedLight, -1, 0, 0);

		vertex(poseStack, vc, xz3, y1, xz3, u1, v0, packedLight, 1, 0, 0);
		vertex(poseStack, vc, xz3, y0, xz3, u0, v0, packedLight, 1, 0, 0);
		vertex(poseStack, vc, xz3, y0, xz0, u0, v3, packedLight, 1, 0, 0);
		vertex(poseStack, vc, xz3, y1, xz0, u1, v3, packedLight, 1, 0, 0);

		vertex(poseStack, vc, xz0, y1, xz0, u1, v3, packedLight, 0, 0, -1);
		vertex(poseStack, vc, xz0, y0, xz0, u0, v3, packedLight, 0, 0, -1);
		vertex(poseStack, vc, xz3, y0, xz0, u0, v0, packedLight, 0, 0, -1);
		vertex(poseStack, vc, xz3, y1, xz0, u1, v0, packedLight, 0, 0, -1);

		vertex(poseStack, vc, xz3, y1, xz3, u1, v3, packedLight, 0, 0, 1);
		vertex(poseStack, vc, xz3, y0, xz3, u0, v3, packedLight, 0, 0, 1);
		vertex(poseStack, vc, xz0, y0, xz3, u0, v0, packedLight, 0, 0, 1);
		vertex(poseStack, vc, xz0, y1, xz3, u1, v0, packedLight, 0, 0, 1);

		// inner side

		vertex(poseStack, vc, xz2, y1, xz1, u1, v0, packedLight, -1, 0, 0);
		vertex(poseStack, vc, xz2, y0, xz1, u0, v0, packedLight, -1, 0, 0);
		vertex(poseStack, vc, xz2, y0, xz2, u0, v3, packedLight, -1, 0, 0);
		vertex(poseStack, vc, xz2, y1, xz2, u1, v3, packedLight, -1, 0, 0);

		vertex(poseStack, vc, xz1, y1, xz2, u1, v0, packedLight, 1, 0, 0);
		vertex(poseStack, vc, xz1, y0, xz2, u0, v0, packedLight, 1, 0, 0);
		vertex(poseStack, vc, xz1, y0, xz1, u0, v3, packedLight, 1, 0, 0);
		vertex(poseStack, vc, xz1, y1, xz1, u1, v3, packedLight, 1, 0, 0);

		vertex(poseStack, vc, xz1, y1, xz2, u1, v3, packedLight, 0, 0, -1);
		vertex(poseStack, vc, xz1, y0, xz2, u0, v3, packedLight, 0, 0, -1);
		vertex(poseStack, vc, xz2, y0, xz2, u0, v0, packedLight, 0, 0, -1);
		vertex(poseStack, vc, xz2, y1, xz2, u1, v0, packedLight, 0, 0, -1);

		vertex(poseStack, vc, xz2, y1, xz1, u1, v3, packedLight, 0, 0, 1);
		vertex(poseStack, vc, xz2, y0, xz1, u0, v3, packedLight, 0, 0, 1);
		vertex(poseStack, vc, xz1, y0, xz1, u0, v0, packedLight, 0, 0, 1);
		vertex(poseStack, vc, xz1, y1, xz1, u1, v0, packedLight, 0, 0, 1);
	}

	private static void vertex(PoseStack poseStack, VertexConsumer vc,
	                           float x, float y, float z,
	                           float u, float v,
	                           int packedLight,
	                           float normalX, float normalY, float normalZ) {
		PoseStack.Pose pose = poseStack.last();
		Vector3f normal = pose.transformNormal(normalX, normalY, normalZ, new Vector3f());
		vc.addVertex(pose, x, y, z)
				.setColor(-1)
				.setUv(u, v)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(packedLight)
				.setNormal(normal.x, normal.y, normal.z);
	}

	public static class EmissiveLayer extends EyesLayer<TempleGuardian, TempleGuardianModel<TempleGuardian>> {
		private static final RenderType renderType = RenderType.eyes(MagiaLucisMod.id("textures/entity/temple_guardian_emissive.png"));

		public EmissiveLayer(RenderLayerParent<TempleGuardian, TempleGuardianModel<TempleGuardian>> renderer) {
			super(renderer);
		}

		@Override
		public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight,
		                   @NotNull TempleGuardian livingEntity, float limbSwing, float limbSwingAmount,
		                   float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
			if (livingEntity.hasAttackCooldown()) return;
			super.render(poseStack, buffer, packedLight, livingEntity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
		}

		@Override
		public @NotNull RenderType renderType() {
			return renderType;
		}
	}
}
