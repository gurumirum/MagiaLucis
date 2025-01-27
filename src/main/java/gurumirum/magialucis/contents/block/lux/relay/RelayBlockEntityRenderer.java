package gurumirum.magialucis.contents.block.lux.relay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import gurumirum.magialucis.client.RotationLogic;
import gurumirum.magialucis.client.render.ModRenderTypes;
import gurumirum.magialucis.client.render.RenderShapes;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import gurumirum.magialucis.utils.AprilFoolsUtils;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class RelayBlockEntityRenderer implements BlockEntityRenderer<RelayBlockEntity> {
	public static final long ROTATION_PERIOD = 160;

	private final Vector3d luxFlow = new Vector3d();

	public RelayBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	public void render(@NotNull RelayBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack,
	                   @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		BlockState state = blockEntity.getBlockState();
		boolean transformed = false;

		if (state.hasProperty(BlockStateProperties.FACING)) {
			Direction facing = state.getValue(BlockStateProperties.FACING);
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

			drawItem(blockEntity.getLevel(), partialTick, poseStack, ModRenderTypes.STUB_BUFFER, stack,
					sum >= 1 ? LightTexture.FULL_BRIGHT : packedLight, packedOverlay);
			ModRenderTypes.STUB_BUFFER.endBatch();
		}

		Minecraft mc = Minecraft.getInstance();
		if (mc.options.graphicsMode().get() == GraphicsStatus.FABULOUS) {
			poseStack.pushPose();

			Level level = blockEntity.getLevel();

			if (level != null) {
				poseStack.translate(.5f, 0, .5f);
				poseStack.mulPose(Axis.YP.rotation(RotationLogic.rotation(level.getGameTime(), ROTATION_PERIOD, partialTick)));
				poseStack.translate(-.5f, 0, -.5f);
			}

			boolean joke = AprilFoolsUtils.APRIL_FOOLS || AprilFoolsUtils.isDank(blockEntity.getBlockPos());
			VertexConsumer vc = bufferSource.getBuffer(ModRenderTypes.positionTextureColor(
					joke ? RenderShapes.NO_FABULOUS_TEXTURE_JOKE : RenderShapes.NO_FABULOUS_TEXTURE));
			RenderShapes.noFabulousWarning(poseStack, vc, joke);

			poseStack.popPose();
		}

		if (transformed) poseStack.popPose();
	}

	public static void drawItem(@Nullable Level level, float partialTick, PoseStack poseStack,
	                            MultiBufferSource bufferSource, ItemStack stack, int packedLight, int packedOverlay) {
		poseStack.pushPose();
		poseStack.translate(.5f, .5f - 2 / 16f, .5f);

		Minecraft mc = Minecraft.getInstance();

		if (level != null) {
			poseStack.mulPose(Axis.YP.rotation(RotationLogic.rotation(level.getGameTime(), ROTATION_PERIOD, partialTick)));
		}

		mc.getItemRenderer().renderStatic(
				stack,
				ItemDisplayContext.GROUND,
				packedLight,
				packedOverlay,
				poseStack,
				bufferSource,
				level,
				0);

		poseStack.popPose();
	}
}
