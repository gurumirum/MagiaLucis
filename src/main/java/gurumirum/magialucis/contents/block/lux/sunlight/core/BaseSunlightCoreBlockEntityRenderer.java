package gurumirum.magialucis.contents.block.lux.sunlight.core;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gurumirum.magialucis.client.render.ModRenderTypes;
import gurumirum.magialucis.client.render.RenderShapes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public abstract class BaseSunlightCoreBlockEntityRenderer<BE extends BaseSunlightCoreBlockEntity<?>> implements BlockEntityRenderer<BE> {
	public BaseSunlightCoreBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	public void render(@NotNull BE blockEntity, float partialTick, @NotNull PoseStack poseStack,
	                   @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		BlockState state = blockEntity.getBlockState();

		render(poseStack, bufferSource, spinningThingTexture(blockEntity),
				-blockEntity.getClientSideRotation(partialTick) * (float)(Math.PI / 180),
				state.hasProperty(BlockStateProperties.FACING) ? state.getValue(BlockStateProperties.FACING) : Direction.UP);
	}

	protected abstract @Nullable ResourceLocation spinningThingTexture(@NotNull BE blockEntity);

	public static void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource,
	                          @Nullable ResourceLocation coreTexture, float coreRotation, Direction direction) {
		if (coreTexture == null) return;

		poseStack.pushPose();
		poseStack.translate(.5f, .5f, .5f);

		if (direction != Direction.UP) poseStack.mulPose(direction.getRotation());
		poseStack.mulPose(new Quaternionf().rotateYXZ(-coreRotation, (float)(Math.PI / 4), (float)(Math.PI / 4)));
		poseStack.scale(14 / 16f, 14 / 16f, 14 / 16f);
		poseStack.translate(-.5f, -.5f, -.5f);

		VertexConsumer vc = bufferSource.getBuffer(ModRenderTypes.positionTextureColor(coreTexture));
		RenderShapes.texturedTintedBox(poseStack, vc,
				4 / 16f, 4 / 16f, 4 / 16f,
				12 / 16f, 12 / 16f, 12 / 16f,
				-1);

		poseStack.popPose();
	}
}
