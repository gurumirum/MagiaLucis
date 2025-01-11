package gurumirum.magialucis.contents.block.sunlight.focus;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public class SunlightFocusItemExtension implements IClientItemExtensions {
	private @Nullable Renderer renderer;

	@Override
	public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
		if (this.renderer == null) {
			Minecraft mc = Minecraft.getInstance();
			this.renderer = new Renderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
		}
		return this.renderer;
	}

	public static class Renderer extends BlockEntityWithoutLevelRenderer {
		private final Quaternionf q = new Quaternionf();

		public Renderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
			super(blockEntityRenderDispatcher, entityModelSet);
		}

		@Override
		public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext,
		                         @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
		                         int packedLight, int packedOverlay) {
			Minecraft mc = Minecraft.getInstance();
			BakedModel model1 = mc.getModelManager().getModel(SunlightFocusModels.MODEL_1);
			BakedModel model2 = mc.getModelManager().getModel(SunlightFocusModels.MODEL_2);
			BakedModel model3 = mc.getModelManager().getModel(SunlightFocusModels.MODEL_3);

			draw(stack, poseStack, buffer, packedLight, packedOverlay, model1);
			draw(stack, poseStack, buffer, packedLight, packedOverlay, model2);

			poseStack.pushPose();
			poseStack.translate(8 / 16.0, 10 / 16.0, 8 / 16.0);
			poseStack.mulPose(this.q.identity().rotateX(-(float)Math.PI / 6));
			poseStack.translate(-8 / 16.0, -10 / 16.0, -8 / 16.0);

			draw(stack, poseStack, buffer, packedLight, packedOverlay, model3);

			poseStack.popPose();
		}

		private void draw(@NotNull ItemStack stack, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay, BakedModel model) {
			for (RenderType renderType : model.getRenderTypes(stack, true)) {
				Minecraft.getInstance().getItemRenderer().renderModelLists(
						model, ItemStack.EMPTY, packedLight, packedOverlay, poseStack,
						buffer.getBuffer(renderType));
			}
		}
	}
}
