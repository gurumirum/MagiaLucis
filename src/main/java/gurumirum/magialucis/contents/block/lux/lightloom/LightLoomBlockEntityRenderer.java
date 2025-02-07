package gurumirum.magialucis.contents.block.lux.lightloom;

import com.mojang.blaze3d.vertex.PoseStack;
import gurumirum.magialucis.client.RotationLogic;
import gurumirum.magialucis.client.render.RenderShapes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.EnumMap;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class LightLoomBlockEntityRenderer implements BlockEntityRenderer<LightLoomBlockEntity> {
	private static final int ITEM_ROTATION_PERIOD = 720;

	private static final EnumMap<Direction, Quaternionf> angles = new EnumMap<>(Direction.class);

	static {
		final float xAngle = (float)(-Math.PI / 2 + Math.atan(4 / 7.0));

		for (Direction dir : Direction.values()) {
			if (dir.getAxis() == Direction.Axis.Y) continue;
			angles.put(dir, new Quaternionf().rotateYXZ(-dir.toYRot() * (float)(Math.PI / 180), xAngle, 0));
		}
	}

	private final LightLoomType type;

	public LightLoomBlockEntityRenderer(BlockEntityRendererProvider.Context context, LightLoomType type) {
		this.type = type;
	}

	@Override
	public void render(@NotNull LightLoomBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack,
	                   @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		RenderShapes.renderMatrix(poseStack, bufferSource, type.matrixTexture(),
				blockEntity.clientSideAngle(partialTick) * (float)(Math.PI / 180),
				.5f, angles.get(blockEntity.getBlockState().getValue(HORIZONTAL_FACING)));
	}

	public static void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext,
	                                @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
	                                int packedLight, int packedOverlay, LightLoomType type) {
		Minecraft mc = Minecraft.getInstance();
		BakedModel model = mc.getModelManager().getModel(LightLoomModels.ITEM_BASE);

		for (RenderType renderType : model.getRenderTypes(stack, true)) {
			Minecraft.getInstance().getItemRenderer().renderModelLists(
					model, ItemStack.EMPTY, packedLight, packedOverlay, poseStack,
					buffer.getBuffer(renderType));
		}

		float partialTicks = mc.getTimer().getGameTimeDeltaPartialTick(false);

		RenderShapes.renderMatrix(poseStack, buffer, type.matrixTexture(),
				RotationLogic.rotation(
						mc.level != null ? mc.level.getGameTime() : 0,
						ITEM_ROTATION_PERIOD,
						partialTicks),
				.5f, angles.get(Direction.NORTH));
	}
}
