package gurumirum.magialucis.contents.block.lux.splitter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gurumirum.magialucis.client.render.ModRenderTypes;
import gurumirum.magialucis.client.render.RenderShapes;
import gurumirum.magialucis.client.render.prism.PrismEffect;
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
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class SplitterBlockEntityRenderer implements BlockEntityRenderer<SplitterBlockEntity> {
	private final Vector3d luxFlow = new Vector3d();

	public SplitterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	public void render(@NotNull SplitterBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack,
	                   @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		ItemStack stack = blockEntity.stack();
		if (!stack.isEmpty()) {
			blockEntity.luxFlow(this.luxFlow);
			double sum = LuxUtils.sum(this.luxFlow);

			RelayBlockEntityRenderer.drawGemItem(blockEntity.getLevel(), partialTick, poseStack,
					ModRenderTypes.STUB_BUFFER, stack, sum >= 1 ? LightTexture.FULL_BRIGHT : packedLight, packedOverlay);
			ModRenderTypes.STUB_BUFFER.endBatch();
		}
	}

	public static void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext,
	                                @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
	                                int packedLight, int packedOverlay) {
		Minecraft mc = Minecraft.getInstance();

		BakedModel blockModel = mc.getBlockRenderer().getBlockModel(ModBlocks.SPLITTER.block()
				.defaultBlockState());

		for (RenderType renderType : blockModel.getRenderTypes(stack, true)) {
			mc.getItemRenderer().renderModelLists(blockModel, stack, packedLight, packedOverlay,
					poseStack, buffer.getBuffer(renderType));
		}

		for (Direction side : Direction.values()) {
			if (side == Direction.DOWN) continue;

			BakedModel model = mc.getModelManager().getModel(SplitterBlockModels
					.sideModel(side, SplitterBlockEntity.APERTURE_LEVELS - 1));

			for (RenderType renderType : model.getRenderTypes(stack, true)) {
				Minecraft.getInstance().getItemRenderer().renderModelLists(
						model, ItemStack.EMPTY, packedLight, packedOverlay, poseStack,
						buffer.getBuffer(renderType));
			}
		}

		ItemStack relayItem = GemItemData.getItem(stack);
		if (!relayItem.isEmpty()) {
			float partialTicks = mc.getTimer().getGameTimeDeltaPartialTick(false);
			RelayBlockEntityRenderer.drawGemItem(mc.level, partialTicks, poseStack, buffer, relayItem,
					packedLight, packedOverlay);
		}

		VertexConsumer vc = buffer.getBuffer(ModRenderTypes.PRISM_ITEM_ENTITY);
		RenderShapes.drawTetrakisHexahedron(poseStack, vc, PrismEffect.defaultColor(false), false);
		RenderShapes.drawTetrakisHexahedron(poseStack, vc, PrismEffect.defaultColor(true), true);
	}
}
