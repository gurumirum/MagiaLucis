package gurumirum.magialucis.contents.block.lux.splitter;

import com.mojang.blaze3d.vertex.PoseStack;
import gurumirum.magialucis.client.render.ModRenderTypes;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.block.RelativeDirection;
import gurumirum.magialucis.contents.block.lux.relay.GemItemData;
import gurumirum.magialucis.contents.block.lux.relay.RelayBlockEntityRenderer;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
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
import org.joml.Vector3d;

public class SplitterBlockEntityRenderer implements BlockEntityRenderer<SplitterBlockEntity> {
	private final Vector3d luxFlow = new Vector3d();

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

		boolean transformed = false;

		if (facing != Direction.UP) {
			transformed = true;
			poseStack.pushPose();
			poseStack.translate(.5f, .5f, .5f);
			poseStack.mulPose(facing.getRotation());
			poseStack.translate(-.5f, -.5f, -.5f);
		}

		ItemStack stack = blockEntity.stack();
		if (!stack.isEmpty()) {
			blockEntity.luxFlow(this.luxFlow);
			double sum = LuxUtils.sum(this.luxFlow);

			RelayBlockEntityRenderer.drawGemItem(blockEntity.getLevel(), partialTick, poseStack,
					ModRenderTypes.STUB_BUFFER, stack, sum >= 1 ? LightTexture.FULL_BRIGHT : packedLight, packedOverlay);
			ModRenderTypes.STUB_BUFFER.endBatch();
		}

		if (transformed) poseStack.popPose();
	}

	public static void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext,
	                                @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
	                                int packedLight, int packedOverlay) {
		Minecraft mc = Minecraft.getInstance();

		BakedModel blockModel = mc.getBlockRenderer().getBlockModel(ModBlocks.SPLITTER.block()
				.defaultBlockState());

		for (RenderType renderType : blockModel.getRenderTypes(stack, true)) {
			mc.getItemRenderer().renderModelLists(blockModel, stack, packedLight, packedOverlay,
					poseStack, buffer.getBuffer(renderType));
		}

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

		ItemStack relayItem = GemItemData.getItem(stack);
		if (!relayItem.isEmpty()) {
			float partialTicks = mc.getTimer().getGameTimeDeltaPartialTick(false);
			RelayBlockEntityRenderer.drawGemItem(mc.level, partialTicks, poseStack, buffer, relayItem,
					packedLight, packedOverlay);
		}
	}
}
