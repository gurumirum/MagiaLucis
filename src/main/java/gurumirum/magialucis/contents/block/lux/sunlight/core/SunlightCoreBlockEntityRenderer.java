package gurumirum.magialucis.contents.block.lux.sunlight.core;

import gurumirum.magialucis.client.Textures;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SunlightCoreBlockEntityRenderer extends BaseSunlightCoreBlockEntityRenderer<SunlightCoreBlockEntity> {

	public SunlightCoreBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected @Nullable ResourceLocation matrixTexture(@NotNull SunlightCoreBlockEntity blockEntity) {
		return Textures.CITRINE_MATRIX;
	}
}
