package gurumirum.magialucis.contents.block.lux.charger;

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

public class ChargerBlockEntityRenderer implements BlockEntityRenderer<ChargerBlockEntity> {
	private static final int ROTATION_PERIOD = 120;

	public ChargerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	public void render(@NotNull ChargerBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack,
	                   @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		ItemStack stack = blockEntity.inventory().getStackInSlot(0);
		if (stack.isEmpty()) return;

		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

		poseStack.pushPose();

		Level level = blockEntity.getLevel();
		if (level != null) {
			poseStack.translate(0.5, 0.5, 0.5);
			poseStack.mulPose(Axis.YP.rotation(-RotationLogic.rotation(level.getGameTime(), ROTATION_PERIOD, partialTick)));
		}

		itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack,
				bufferSource, level, 906296);

		poseStack.popPose();
	}
}
