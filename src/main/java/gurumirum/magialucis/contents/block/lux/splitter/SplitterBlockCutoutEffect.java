package gurumirum.magialucis.contents.block.lux.splitter;

import com.mojang.blaze3d.vertex.PoseStack;
import gurumirum.magialucis.client.render.generic.BlockGenericRenderEffect;
import gurumirum.magialucis.contents.block.RelativeDirection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

public class SplitterBlockCutoutEffect extends BlockGenericRenderEffect<SplitterBlockEntity> {
	public SplitterBlockCutoutEffect(SplitterBlockEntity blockEntity) {
		super(blockEntity);
	}

	@Override
	public void draw(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, float partialTicks) {
		Level level = this.blockEntity.getLevel();
		if (level == null) return;

		Minecraft mc = Minecraft.getInstance();
		BlockState state = this.blockEntity.getBlockState();
		BlockPos pos = this.blockEntity.getBlockPos();
		Direction facing = this.blockEntity.getBlockState().getValue(BlockStateProperties.FACING);

		long seed = state.getSeed(pos);
		RandomSource randomSource = RandomSource.create(seed);

		poseStack.pushPose();
		poseStack.translate(pos.getX(), pos.getY(), pos.getZ());

		for (Direction side : Direction.values()) {
			if (side == facing.getOpposite()) continue;

			BakedModel model = mc.getModelManager().getModel(SplitterBlockModels
					.sideModel(side, blockEntity.apertureLevel(RelativeDirection.getRelativeDirection(facing, side))));

			for (RenderType renderType : model.getRenderTypes(state, randomSource, ModelData.EMPTY)) {
				Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(
						level, model, state, pos,
						poseStack, bufferSource.getBuffer(renderType),
						false, randomSource,
						state.getSeed(pos), OverlayTexture.NO_OVERLAY,
						ModelData.EMPTY, renderType);
			}
		}

		poseStack.popPose();
	}
}
