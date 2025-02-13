package gurumirum.magialucis.contents.block.lux.splitter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gurumirum.magialucis.client.render.RenderShapes;
import gurumirum.magialucis.client.render.prism.BlockPrismEffect;
import gurumirum.magialucis.client.render.prism.PrismEffect;
import org.jetbrains.annotations.NotNull;

public class SplitterBlockPrismEffect extends BlockPrismEffect<SplitterBlockEntity> {
	public SplitterBlockPrismEffect(SplitterBlockEntity relay) {
		super(relay);
	}

	@Override
	public void draw(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, boolean reverseCull) {
		RenderShapes.drawTetrakisHexahedron(poseStack, vertexConsumer, PrismEffect.defaultColor(reverseCull), reverseCull);
	}
}
