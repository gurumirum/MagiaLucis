package gurumirum.magialucis.contents.block.lux.sunlight.core;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gurumirum.magialucis.client.RotationLogic;
import gurumirum.magialucis.client.render.ModRenderTypes;
import gurumirum.magialucis.client.render.RenderShapes;
import gurumirum.magialucis.client.render.prism.PrismEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseSunlightCoreBlockEntityRenderer<BE extends BaseSunlightCoreBlockEntity<?>> implements BlockEntityRenderer<BE> {
	private static final int ITEM_ROTATION_PERIOD = 720;

	public BaseSunlightCoreBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	public void render(@NotNull BE blockEntity, float partialTick, @NotNull PoseStack poseStack,
	                   @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		BlockState state = blockEntity.getBlockState();
		Direction direction = state.hasProperty(BlockStateProperties.FACING) ?
				state.getValue(BlockStateProperties.FACING) : Direction.UP;

		ResourceLocation matrixTexture = matrixTexture(blockEntity);

		if (matrixTexture != null) {
			RenderShapes.renderMatrix(poseStack, bufferSource, matrixTexture,
					-blockEntity.getClientSideRotation(partialTick) * (float)(Math.PI / 180),
					14 / 16f, direction.getRotation());
		}
	}

	protected abstract @Nullable ResourceLocation matrixTexture(@NotNull BE blockEntity);

	public static void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext,
	                                @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
	                                int packedLight, int packedOverlay,
	                                @NotNull BlockState state, @Nullable ResourceLocation matrixTexture) {
		Minecraft mc = Minecraft.getInstance();
		BakedModel blockModel = mc.getBlockRenderer().getBlockModel(state);

		for (RenderType renderType : blockModel.getRenderTypes(stack, true)) {
			mc.getItemRenderer().renderModelLists(blockModel, stack, packedLight, packedOverlay,
					poseStack, buffer.getBuffer(renderType));
		}

		if (matrixTexture != null) {
			float partialTicks = mc.getTimer().getGameTimeDeltaPartialTick(false);

			RenderShapes.renderMatrix(poseStack, buffer, matrixTexture,
					RotationLogic.rotation(
							mc.level != null ? mc.level.getGameTime() : 0,
							ITEM_ROTATION_PERIOD,
							partialTicks),
					14 / 16f, null);
		}

		poseStack.translate(.5f, .5f, .5f);
		poseStack.scale(14 / 16f, 14 / 16f, 14 / 16f);
		poseStack.translate(-.5f, -.5f, -.5f);

		VertexConsumer vc = buffer.getBuffer(ModRenderTypes.PRISM_ITEM_ENTITY);
		RenderShapes.drawTruncatedCube(poseStack, vc, PrismEffect.defaultColor(false), false);
		RenderShapes.drawTruncatedCube(poseStack, vc, PrismEffect.defaultColor(true), true);
	}
}
