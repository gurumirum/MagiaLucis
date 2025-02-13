package gurumirum.magialucis.contents.block.lux.relay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gurumirum.magialucis.client.render.RenderShapes;
import gurumirum.magialucis.client.render.prism.BlockPrismEffect;
import gurumirum.magialucis.client.render.prism.PrismEffect;
import org.jetbrains.annotations.NotNull;

public class RelayBlockPrismEffect extends BlockPrismEffect<RelayBlockEntity> {
	public RelayBlockPrismEffect(RelayBlockEntity relay) {
		super(relay);
	}

	@Override
	public void draw(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, boolean reverseCull) {
		RenderShapes.drawOctahedron(poseStack, vertexConsumer, PrismEffect.defaultColor(reverseCull), reverseCull);
	}
}
