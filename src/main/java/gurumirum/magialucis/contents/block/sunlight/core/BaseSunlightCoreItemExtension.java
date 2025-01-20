package gurumirum.magialucis.contents.block.sunlight.core;

import com.mojang.blaze3d.vertex.PoseStack;
import gurumirum.magialucis.client.RotationLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseSunlightCoreItemExtension implements IClientItemExtensions {
	private @Nullable Renderer renderer;

	@Override
	public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
		if (this.renderer == null) {
			Minecraft mc = Minecraft.getInstance();
			this.renderer = new Renderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
		}
		return this.renderer;
	}

	protected abstract @NotNull BlockState getBlockState();
	protected abstract @Nullable ResourceLocation spinningThingTexture();

	public class Renderer extends BlockEntityWithoutLevelRenderer {
		private static final int ROTATION_PERIOD = 720;

		public Renderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
			super(blockEntityRenderDispatcher, entityModelSet);
		}

		@Override
		public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext,
		                         @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
		                         int packedLight, int packedOverlay) {
			Minecraft mc = Minecraft.getInstance();

			BakedModel blockModel = mc.getBlockRenderer().getBlockModel(getBlockState());

			for (RenderType renderType : blockModel.getRenderTypes(stack, true)) {
				mc.getItemRenderer().renderModelLists(blockModel, stack, packedLight, packedOverlay,
						poseStack, buffer.getBuffer(renderType));
			}

			float partialTicks = mc.getTimer().getGameTimeDeltaPartialTick(false);

			BaseSunlightCoreBlockEntityRenderer.render(poseStack, buffer, spinningThingTexture(),
					RotationLogic.rotation(
							mc.level != null ? mc.level.getGameTime() : 0,
							ROTATION_PERIOD,
							partialTicks),
					Direction.UP);
		}
	}
}
