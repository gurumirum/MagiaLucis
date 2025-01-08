package gurumirum.gemthing.contents.block.lux.relay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import gurumirum.gemthing.client.ModRenderTypes;
import gurumirum.gemthing.client.RenderShapes;
import gurumirum.gemthing.client.RotationLogic;
import gurumirum.gemthing.contents.ModBlocks;
import gurumirum.gemthing.utils.AprilFoolsUtils;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static gurumirum.gemthing.contents.block.lux.relay.RelayBlockEntityRenderer.ROTATION_PERIOD;

public class RelayItemExtension implements IClientItemExtensions {
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
		public Renderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
			super(blockEntityRenderDispatcher, entityModelSet);
		}

		@Override
		public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext,
		                         @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
		                         int packedLight, int packedOverlay) {
			Minecraft mc = Minecraft.getInstance();
			float partialTicks = mc.getTimer().getGameTimeDeltaPartialTick(false);

			mc.getBlockRenderer().renderSingleBlock(
					ModBlocks.RELAY.block().defaultBlockState(),
					poseStack, buffer, packedLight, packedOverlay,
					ModelData.EMPTY, RenderType.SOLID);

			ItemStack relayItem = RelayItemData.getItem(stack);
			if (!relayItem.isEmpty()) {
				RelayBlockEntityRenderer.drawItem(mc.level, partialTicks, poseStack, buffer, relayItem,
						packedLight, packedOverlay, null);
			}

			if (mc.options.graphicsMode().get() == GraphicsStatus.FABULOUS) {
				poseStack.pushPose();

				Level level = mc.level;

				if (level != null) {
					poseStack.translate(.5f, 0, .5f);
					poseStack.mulPose(Axis.YP.rotationDegrees(RotationLogic.rotation(level.getGameTime(), ROTATION_PERIOD, partialTicks)));
					poseStack.translate(-.5f, 0, -.5f);
				}

				VertexConsumer vc = buffer.getBuffer(ModRenderTypes.positionTextureColor(
						AprilFoolsUtils.APRIL_FOOLS ? RenderShapes.NO_FABULOUS_TEXTURE_JOKE : RenderShapes.NO_FABULOUS_TEXTURE));
				RenderShapes.noFabulousWarning(poseStack, vc, AprilFoolsUtils.APRIL_FOOLS);

				poseStack.popPose();
			} else {
				VertexConsumer vc = buffer.getBuffer(ModRenderTypes.RELAY_ITEM_ENTITY);
				RenderShapes.drawOctahedron(poseStack, vc, 0xffd2ecf6, false);
				RenderShapes.drawOctahedron(poseStack, vc, -1, true);
			}
		}
	}
}
