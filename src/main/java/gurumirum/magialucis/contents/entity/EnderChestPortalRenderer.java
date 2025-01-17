package gurumirum.magialucis.contents.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;

public class EnderChestPortalRenderer extends EntityRenderer<EnderChestPortal> {
	public EnderChestPortalRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(@NotNull EnderChestPortal entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		poseStack.pushPose();
		poseStack.translate(-0.5, 0, -0.5);
		VertexConsumer consumer = bufferSource.getBuffer(RenderType.debugFilledBox());
		LevelRenderer.addChainedFilledBoxVertices(poseStack, consumer, 0, 0, 0, 1, 2, 1, 1f, 1f, 1f, 1);
		poseStack.popPose();
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull EnderChestPortal entity) {
		return InventoryMenu.BLOCK_ATLAS; // unused
	}
}
