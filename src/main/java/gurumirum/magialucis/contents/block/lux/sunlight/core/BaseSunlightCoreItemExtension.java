package gurumirum.magialucis.contents.block.lux.sunlight.core;

import gurumirum.magialucis.client.render.CustomItemRender;
import gurumirum.magialucis.client.render.CustomRenderItemExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseSunlightCoreItemExtension extends CustomRenderItemExtension {
	@Override
	protected @NotNull CustomItemRender customItemRender() {
		return (stack, displayContext, poseStack,
		        buffer, packedLight, packedOverlay) -> {
			BaseSunlightCoreBlockEntityRenderer.renderByItem(stack, displayContext, poseStack,
					buffer, packedLight, packedOverlay, getBlockState(), matrixTexture());
		};
	}

	protected abstract @NotNull BlockState getBlockState();
	protected abstract @Nullable ResourceLocation matrixTexture();
}
