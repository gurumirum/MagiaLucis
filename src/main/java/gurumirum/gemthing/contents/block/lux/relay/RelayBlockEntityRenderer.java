package gurumirum.gemthing.contents.block.lux.relay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import gurumirum.gemthing.contents.block.lux.BasicRelayBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class RelayBlockEntityRenderer extends BasicRelayBlockEntityRenderer<RelayBlockEntity> {
	private final Vector3d luxFlow = new Vector3d();

	public RelayBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(@NotNull RelayBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack,
	                   @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);

		ItemStack stack = blockEntity.stack();
		if (stack.isEmpty()) return;

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
}
