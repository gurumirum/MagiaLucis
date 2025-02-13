package gurumirum.magialucis.contents.block.lux.connector;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gurumirum.magialucis.client.render.RenderShapes;
import gurumirum.magialucis.client.render.prism.BlockPrismEffect;
import gurumirum.magialucis.client.render.prism.PrismEffect;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

public class ConnectorBlockPrismEffect extends BlockPrismEffect<ConnectorBlockEntity> {
	public ConnectorBlockPrismEffect(ConnectorBlockEntity blockEntity) {
		super(blockEntity);
	}

	@Override
	public void draw(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, boolean reverseCull) {
		BlockState state = this.blockEntity.getBlockState();
		boolean transformed = false;

		if (state.hasProperty(BlockStateProperties.FACING)) {
			Direction facing = state.getValue(BlockStateProperties.FACING).getOpposite();
			if (facing != Direction.UP) {
				transformed = true;
				poseStack.pushPose();
				poseStack.translate(.5f, .5f, .5f);
				poseStack.mulPose(facing.getRotation());
				poseStack.translate(-.5f, -.5f, -.5f);
			}
		}

		RenderShapes.drawRhombicuboctahedronDome(poseStack, vertexConsumer, PrismEffect.defaultColor(reverseCull), reverseCull);

		if (transformed) poseStack.popPose();
	}
}
