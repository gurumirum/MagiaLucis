package gurumirum.magialucis.contents.block.lux.lightbasin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import gurumirum.magialucis.client.RotationLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public class LightBasinBlockEntityRenderer implements BlockEntityRenderer<LightBasinBlockEntity> {
	private static final int ROTATION_PERIOD = 120;

	public LightBasinBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	public void render(@NotNull LightBasinBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack,
	                   @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
		IItemHandlerModifiable inv = blockEntity.inventory();

		int itemCount = 0;

		for (int i = 0; i < inv.getSlots(); i++) {
			if (!inv.getStackInSlot(i).isEmpty()) itemCount++;
		}

		if (itemCount == 0) return;

		int itemIndex = 0;

		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;

			poseStack.pushPose();
			poseStack.translate(0.5, 1, 0.5);

			Level level = blockEntity.getLevel();
			if (level != null) {
				int cycleOffset = (itemCount - 1) * ROTATION_PERIOD / itemCount * itemIndex;
				poseStack.mulPose(Axis.YP.rotation(RotationLogic.rotation(level.getGameTime() + cycleOffset, ROTATION_PERIOD, partialTick)));
				float offset = Mth.sin(RotationLogic.rotation(level.getGameTime() + cycleOffset, ROTATION_PERIOD * 2, partialTick)) * 0.1f + 0.05f;
				poseStack.translate(0, offset, 0.1 * (itemCount == 1 ? 0 : itemCount));
			}

			itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack,
					bufferSource, level, 906296);

			poseStack.popPose();
			itemIndex++;
		}
	}
}
