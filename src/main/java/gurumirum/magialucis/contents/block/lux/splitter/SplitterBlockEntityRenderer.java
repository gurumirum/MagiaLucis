package gurumirum.magialucis.contents.block.lux.splitter;

import com.mojang.blaze3d.vertex.PoseStack;
import gurumirum.magialucis.contents.block.RelativeDirection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

public class SplitterBlockEntityRenderer implements BlockEntityRenderer<SplitterBlockEntity> {
	public SplitterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	public void render(@NotNull SplitterBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack,
	                   @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		Minecraft mc = Minecraft.getInstance();
		Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.FACING);

		Level level = blockEntity.getLevel();
		if (level != null) {
			BlockState state = blockEntity.getBlockState();
			BlockPos pos = blockEntity.getBlockPos();

			long seed = state.getSeed(pos);
			RandomSource randomSource = RandomSource.create(seed);

			for (Direction side : Direction.values()) {
				if (side == facing.getOpposite()) continue;

				BakedModel model = mc.getModelManager().getModel(SplitterBlockModels
						.sideModel(side, blockEntity.apertureLevel(RelativeDirection.getRelativeDirection(facing, side))));

				for (RenderType renderType : model.getRenderTypes(state, randomSource, ModelData.EMPTY)) {
					Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(
							level, model, state, pos,
							poseStack, bufferSource.getBuffer(renderType),
							false, randomSource,
							state.getSeed(pos), packedOverlay,
							ModelData.EMPTY, renderType);
				}
			}
		}
	}

	public static void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext,
	                                @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
	                                int packedLight, int packedOverlay) {
		Minecraft mc = Minecraft.getInstance();

		for (Direction side : Direction.values()) {
			if (side == Direction.DOWN) continue;

			BakedModel model = mc.getModelManager().getModel(SplitterBlockModels
					.sideModel(side, SplitterBlockEntity.APERTURE_LEVELS - 1));

			for (RenderType renderType : model.getRenderTypes(stack, true)) {
				Minecraft.getInstance().getItemRenderer().renderModelLists(
						model, ItemStack.EMPTY, packedLight, packedOverlay, poseStack,
						buffer.getBuffer(renderType));
			}
		}
	}
}
