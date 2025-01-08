package gurumirum.gemthing.contents.block.lux.relay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.client.ModRenderTypes;
import gurumirum.gemthing.client.RenderShapes;
import gurumirum.gemthing.contents.block.lux.BasicRelayBlockEntityRenderer;
import gurumirum.gemthing.utils.AprilFoolsUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import java.util.ArrayDeque;
import java.util.Queue;

@EventBusSubscriber(modid = GemthingMod.MODID, value = Dist.CLIENT)
public class RelayBlockEntityRenderer extends BasicRelayBlockEntityRenderer<RelayBlockEntity> {
	private static final Queue<BlockPos> relay = new ArrayDeque<>();

	private final Vector3d luxFlow = new Vector3d();

	public RelayBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(@NotNull RelayBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack,
	                   @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);

		ItemStack stack = blockEntity.stack();
		if (!stack.isEmpty()) {
			drawItem(blockEntity, partialTick, poseStack, bufferSource, stack, packedLight, packedOverlay);
		}

		Minecraft mc = Minecraft.getInstance();
		if (mc.options.graphicsMode().get() == GraphicsStatus.FABULOUS) {
			poseStack.pushPose();

			Level level = blockEntity.getLevel();

			if (level != null) {
				poseStack.translate(.5f, 0, .5f);
				float rotation = (level.getGameTime() % 720) * -2.5f;
				poseStack.mulPose(Axis.YP.rotationDegrees(
						mc.isPaused() ? rotation : Mth.lerp(partialTick, rotation, rotation + 1)));
				poseStack.translate(-.5f, 0, -.5f);
			}

			boolean joke = AprilFoolsUtils.APRIL_FOOLS || AprilFoolsUtils.isDank(blockEntity.getBlockPos());
			VertexConsumer vc = bufferSource.getBuffer(ModRenderTypes.positionTextureColor(
					joke ? RenderShapes.NO_FABULOUS_TEXTURE_JOKE : RenderShapes.NO_FABULOUS_TEXTURE));
			RenderShapes.noFabulousWarning(poseStack, vc, joke);

			poseStack.popPose();
		} else {
			relay.add(blockEntity.getBlockPos());
		}
	}

	private void drawItem(RelayBlockEntity blockEntity, float partialTick, PoseStack poseStack,
	                      MultiBufferSource bufferSource, ItemStack stack, int packedLight, int packedOverlay) {
		poseStack.pushPose();
		poseStack.translate(.5f, .5f - 2 / 16f, .5f);

		Minecraft mc = Minecraft.getInstance();
		Level level = blockEntity.getLevel();

		if (level != null) {
			float rotation = (level.getGameTime() % 720) * -2.5f;
			poseStack.mulPose(Axis.YP.rotationDegrees(
					mc.isPaused() ? rotation : Mth.lerp(partialTick, rotation, rotation + 1)));
		}

		blockEntity.luxFlow(this.luxFlow);

		double max = Math.max(Math.max(this.luxFlow.x, this.luxFlow.y), this.luxFlow.z);

		mc.getItemRenderer().renderStatic(
				stack,
				ItemDisplayContext.GROUND,
				max > 0 ? LightTexture.FULL_BRIGHT : packedLight, // emissive
				packedOverlay,
				poseStack,
				bufferSource,
				level,
				0);

		poseStack.popPose();
	}

	@SubscribeEvent
	public static void onRender(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
		if (relay.isEmpty()) return;

		Minecraft mc = Minecraft.getInstance();
		PoseStack poseStack = event.getPoseStack();
		MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

		Camera camera = event.getCamera();

		poseStack.pushPose();
		Vec3 cameraPos = camera.getPosition();
		poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

		for (BlockPos pos : relay) {
			poseStack.pushPose();
			poseStack.translate(pos.getX(), pos.getY(), pos.getZ());

			VertexConsumer vc = bufferSource.getBuffer(ModRenderTypes.RELAY);
			RenderShapes.drawOctahedron(poseStack, vc, 0xffd2ecf6, false);
			RenderShapes.drawOctahedron(poseStack, vc, -1, true);

			poseStack.popPose();
		}

		poseStack.popPose();
		relay.clear();
	}
}
