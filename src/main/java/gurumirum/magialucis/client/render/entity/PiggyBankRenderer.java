package gurumirum.magialucis.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gurumirum.magialucis.contents.entity.PiggyBankEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PiggyBankRenderer extends EntityRenderer<PiggyBankEntity> {
	public PiggyBankRenderer(EntityRendererProvider.Context context) {
		super(context);
	}
	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull PiggyBankEntity entity) {
		return null;
	}

	@Override
	public void render(@NotNull PiggyBankEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

		poseStack.pushPose();
		poseStack.translate(-0.5, 0, -0.5);
		VertexConsumer consumer = bufferSource.getBuffer(RenderType.debugFilledBox());
		LevelRenderer.addChainedFilledBoxVertices(poseStack, consumer, 0, 0, 0, 1, 2, 1, 1f, 1f, 1f, 1);
		poseStack.popPose();
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
