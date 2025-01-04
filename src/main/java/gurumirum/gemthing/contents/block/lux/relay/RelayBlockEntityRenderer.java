package gurumirum.gemthing.contents.block.lux.relay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class RelayBlockEntityRenderer implements BlockEntityRenderer<RelayBlockEntity> {
	private final Vector3f directionCache = new Vector3f();

	public RelayBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	public void render(@NotNull RelayBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack,
	                   @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		blockEntity.getDirection(this.directionCache);

		if (!this.directionCache.isFinite()) return;

		int color = blockEntity.hasOutboundConnection() ? 0xFF00FF00 : 0xFFFF0000;

		VertexConsumer vc = bufferSource.getBuffer(RenderType.lines());
		vc.addVertex(poseStack.last(), .5f, .5f, .5f)
				.setColor(color).setNormal(poseStack.last(), 0, 1, 0);
		vc.addVertex(poseStack.last(),
						.5f + this.directionCache.x * 3,
						.5f + this.directionCache.y * 3,
						.5f + this.directionCache.z * 3)
				.setColor(color).setNormal(poseStack.last(), 0, 1, 0);
	}
}
