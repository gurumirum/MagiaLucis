package gurumirum.magialucis.contents.block.lux.sunlight.core;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gurumirum.magialucis.client.render.RenderShapes;
import gurumirum.magialucis.client.render.prism.BlockPrismEffect;
import gurumirum.magialucis.client.render.prism.PrismEffect;
import org.jetbrains.annotations.NotNull;

public class SunlightCoreBlockPrismEffect extends BlockPrismEffect<BaseSunlightCoreBlockEntity<?>> {
	public SunlightCoreBlockPrismEffect(BaseSunlightCoreBlockEntity<?> blockEntity) {
		super(blockEntity);
	}

	@Override public void transform(@NotNull PoseStack poseStack) {
		super.transform(poseStack);

		poseStack.translate(.5f, .5f, .5f);
		poseStack.scale(14 / 16f, 14 / 16f, 14 / 16f);
		poseStack.translate(-.5f, -.5f, -.5f);
	}

	@Override
	public void draw(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, boolean reverseCull) {
		RenderShapes.drawTruncatedCube(poseStack, vertexConsumer, PrismEffect.defaultColor(reverseCull), reverseCull);
	}
}
