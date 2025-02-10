package gurumirum.magialucis.contents.block.lux.sunlight.focus;

import com.mojang.blaze3d.vertex.PoseStack;
import gurumirum.magialucis.utils.Orientation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class SunlightFocusBlockEntityRenderer implements BlockEntityRenderer<SunlightFocusBlockEntity> {
	private final Quaternionf q = new Quaternionf();

	public SunlightFocusBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	public void render(@NotNull SunlightFocusBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack,
	                   @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		Minecraft mc = Minecraft.getInstance();
		BakedModel model1 = mc.getModelManager().getModel(SunlightFocusModels.MODEL_1);
		BakedModel model2 = mc.getModelManager().getModel(SunlightFocusModels.MODEL_2);
		BakedModel model3 = mc.getModelManager().getModel(SunlightFocusModels.MODEL_3);

		draw(blockEntity, poseStack, bufferSource, packedOverlay, model1);

		Orientation o = blockEntity.getLink(0);
		if (o != null) {
			poseStack.pushPose();
			poseStack.translate(8 / 16.0, 0, 8 / 16.0);
			poseStack.mulPose(this.q.identity().rotateY((float)Math.PI - o.yRot()));
			poseStack.translate(-8 / 16.0, 0, -8 / 16.0);
		}

		draw(blockEntity, poseStack, bufferSource, packedOverlay, model2);

		poseStack.pushPose();
		poseStack.translate(8 / 16.0, 10 / 16.0, 8 / 16.0);
		poseStack.mulPose(this.q.identity().rotateX(o != null ? (-Math.min(0, o.xRot()) - (float)(Math.PI / 2)) / 2 : -(float)Math.PI / 6));
		poseStack.translate(-8 / 16.0, -10 / 16.0, -8 / 16.0);

		draw(blockEntity, poseStack, bufferSource, packedOverlay, model3);

		if (o != null) {
			poseStack.popPose();
		}
		poseStack.popPose();

		// TODO light effect maybe
	}

	private void draw(SunlightFocusBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource,
	                  int packedOverlay, BakedModel model) {
		Level level = blockEntity.getLevel();
		if (level == null) return;
		BlockState state = blockEntity.getBlockState();
		BlockPos pos = blockEntity.getBlockPos();
		Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(
				level, model, state, pos,
				poseStack, bufferSource.getBuffer(RenderType.SOLID),
				false, RandomSource.create(),
				state.getSeed(pos), packedOverlay,
				ModelData.EMPTY, RenderType.SOLID);
	}

	private static final Quaternionf itemQuat = new Quaternionf();

	public static void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext,
	                                @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
	                                int packedLight, int packedOverlay) {
		Minecraft mc = Minecraft.getInstance();
		BakedModel model1 = mc.getModelManager().getModel(SunlightFocusModels.MODEL_1);
		BakedModel model2 = mc.getModelManager().getModel(SunlightFocusModels.MODEL_2);
		BakedModel model3 = mc.getModelManager().getModel(SunlightFocusModels.MODEL_3);

		drawItem(stack, poseStack, buffer, packedLight, packedOverlay, model1);
		drawItem(stack, poseStack, buffer, packedLight, packedOverlay, model2);

		poseStack.pushPose();
		poseStack.translate(8 / 16.0, 10 / 16.0, 8 / 16.0);
		poseStack.mulPose(itemQuat.identity().rotateX(-(float)Math.PI / 6));
		poseStack.translate(-8 / 16.0, -10 / 16.0, -8 / 16.0);

		drawItem(stack, poseStack, buffer, packedLight, packedOverlay, model3);

		poseStack.popPose();
	}

	private static void drawItem(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer,
	                             int packedLight, int packedOverlay, BakedModel model) {
		for (RenderType renderType : model.getRenderTypes(stack, true)) {
			Minecraft.getInstance().getItemRenderer().renderModelLists(
					model, ItemStack.EMPTY, packedLight, packedOverlay, poseStack,
					buffer.getBuffer(renderType));
		}
	}
}
