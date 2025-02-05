package gurumirum.magialucis.contents.block.lux.sunlight.core;

import gurumirum.magialucis.client.Textures;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MoonlightCoreBlockEntityRenderer extends BaseSunlightCoreBlockEntityRenderer<MoonlightCoreBlockEntity> {

	public MoonlightCoreBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected @Nullable ResourceLocation matrixTexture(@NotNull MoonlightCoreBlockEntity blockEntity) {
		return Textures.IOLITE_MATRIX;
	}
}
