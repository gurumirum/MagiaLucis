package gurumirum.magialucis.contents.block.lux.connector;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gurumirum.magialucis.client.render.ModRenderTypes;
import gurumirum.magialucis.client.render.RenderShapes;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.block.lux.relay.GemItemData;
import gurumirum.magialucis.contents.block.lux.relay.RelayBlockEntityRenderer;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class ConnectorBlockEntityRenderer implements BlockEntityRenderer<ConnectorBlockEntity> {
	private final Vector3d luxFlow = new Vector3d();

	public ConnectorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	public void render(@NotNull ConnectorBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack,
	                   @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		BlockState state = blockEntity.getBlockState();
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

		ItemStack stack = blockEntity.stack();
		if (!stack.isEmpty()) {
			blockEntity.luxFlow(this.luxFlow);
			double sum = LuxUtils.sum(this.luxFlow);

			RelayBlockEntityRenderer.drawGemItem(blockEntity.getLevel(), partialTick, poseStack,
					ModRenderTypes.STUB_BUFFER, stack, sum >= 1 ? LightTexture.FULL_BRIGHT : packedLight, packedOverlay);
			ModRenderTypes.STUB_BUFFER.endBatch();
		}

		if (transformed) poseStack.popPose();
	}

	public static void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext,
	                                @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
	                                int packedLight, int packedOverlay) {
		Minecraft mc = Minecraft.getInstance();

		BakedModel blockModel = mc.getBlockRenderer().getBlockModel(ModBlocks.CONNECTOR.block()
				.defaultBlockState());

		for (RenderType renderType : blockModel.getRenderTypes(stack, true)) {
			mc.getItemRenderer().renderModelLists(blockModel, stack, packedLight, packedOverlay,
					poseStack, buffer.getBuffer(renderType));
		}

		ItemStack relayItem = GemItemData.getItem(stack);
		if (!relayItem.isEmpty()) {
			float partialTicks = mc.getTimer().getGameTimeDeltaPartialTick(false);
			RelayBlockEntityRenderer.drawGemItem(mc.level, partialTicks, poseStack, buffer, relayItem,
					packedLight, packedOverlay);
		}


		VertexConsumer vc = buffer.getBuffer(ModRenderTypes.PRISM_ITEM_ENTITY);
		RenderShapes.drawRhombicuboctahedronDome(poseStack, vc, 0xffd2ecf6, false);
		RenderShapes.drawRhombicuboctahedronDome(poseStack, vc, -1, true);
	}
}
