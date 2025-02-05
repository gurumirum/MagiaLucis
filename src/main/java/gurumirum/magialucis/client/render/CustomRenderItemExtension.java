package gurumirum.magialucis.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class CustomRenderItemExtension implements IClientItemExtensions {
	public static CustomRenderItemExtension customRenderItem(@NotNull CustomItemRender customItemRender) {
		return new Simple(customItemRender);
	}

	private @Nullable Renderer renderer;

	protected abstract @NotNull CustomItemRender customItemRender();

	@Override
	public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
		if (this.renderer == null) {
			Minecraft mc = Minecraft.getInstance();
			this.renderer = new Renderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels(), customItemRender());
		}
		return this.renderer;
	}

	public static class Simple extends CustomRenderItemExtension {
		private final CustomItemRender customItemRender;

		public Simple(@NotNull CustomItemRender customItemRender) {
			this.customItemRender = Objects.requireNonNull(customItemRender);
		}

		@Override
		protected @NotNull CustomItemRender customItemRender() {
			return this.customItemRender;
		}
	}

	public static class Renderer extends BlockEntityWithoutLevelRenderer {
		private final CustomItemRender customItemRender;

		public Renderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet,
		                CustomItemRender customItemRender) {
			super(blockEntityRenderDispatcher, entityModelSet);
			this.customItemRender = customItemRender;
		}

		@Override
		public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext,
		                         @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
		                         int packedLight, int packedOverlay) {
			this.customItemRender.render(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
		}
	}
}
