package gurumirum.magialucis.contents.block.lux.lightbasin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import gurumirum.magialucis.client.RotationLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class LightBasinBlockEntityRenderer implements BlockEntityRenderer<LightBasinBlockEntity> {
	private static final int ROTATION_PERIOD = 50;

	public LightBasinBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	public void render(@NotNull LightBasinBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack,
	                   @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
		boolean setup = false;

		for (int i = 0; i < blockEntity.inventory().getSlots(); i++) {
			ItemStack stack = blockEntity.inventory().getStackInSlot(i);
			if (stack.isEmpty()) continue;

			if (!setup) {
				setup = true;
				poseStack.pushPose();
				poseStack.translate(0.5, 1, 0.5);
			}

			// TODO do it right

			poseStack.pushPose();
			poseStack.translate(0.01 * i, 0, 0.01 * i);

			Level level = blockEntity.getLevel();
			if (level != null) {
				poseStack.mulPose(Axis.YP.rotation(RotationLogic.rotation(level.getGameTime(), ROTATION_PERIOD, partialTick)));
			}

			itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack,
					bufferSource, level, 906296);

			poseStack.popPose();
		}

		if (setup) {
			poseStack.popPose();
		}
	}
}
