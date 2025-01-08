package gurumirum.gemthing.contents.block.lux.relay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gurumirum.gemthing.client.ModRenderTypes;
import gurumirum.gemthing.client.RenderShapes;
import gurumirum.gemthing.contents.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RelayItemExtension implements IClientItemExtensions {
	private @Nullable Renderer renderer;

	@Override
	public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
		if (this.renderer == null) {
			Minecraft mc = Minecraft.getInstance();
			this.renderer = new Renderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
		}
		return this.renderer;
	}

	public static class Renderer extends BlockEntityWithoutLevelRenderer {
		public Renderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
			super(blockEntityRenderDispatcher, entityModelSet);
		}

		@Override
		public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext,
		                         @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
		                         int packedLight, int packedOverlay) {
			Minecraft mc = Minecraft.getInstance();

			mc.getBlockRenderer().renderSingleBlock(
					ModBlocks.RELAY.block().defaultBlockState(),
					poseStack, buffer, packedLight, packedOverlay,
					ModelData.EMPTY, RenderType.SOLID);

			ItemStack relayItem = RelayItemData.getItem(stack);
			if (!relayItem.isEmpty()) {
				float partialTicks = mc.getTimer().getGameTimeDeltaPartialTick(false);
				RelayBlockEntityRenderer.drawItem(mc.level, partialTicks, poseStack, buffer, relayItem,
						packedLight, packedOverlay, null);
			}

			VertexConsumer vc = buffer.getBuffer(ModRenderTypes.RELAY_ITEM_ENTITY);
			RenderShapes.drawOctahedron(poseStack, vc, 0xffd2ecf6, false);
			RenderShapes.drawOctahedron(poseStack, vc, -1, true);
		}
	}
}
