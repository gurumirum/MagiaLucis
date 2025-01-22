package gurumirum.magialucis.contents.block.lux.sunlight.core;

import gurumirum.magialucis.MagiaLucisMod;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SunlightCoreBlockEntityRenderer extends BaseSunlightCoreBlockEntityRenderer<SunlightCoreBlockEntity> {
	public static final ResourceLocation TEXTURE = MagiaLucisMod.id("textures/effect/sunlight_core.png");

	public SunlightCoreBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected @Nullable ResourceLocation spinningThingTexture(@NotNull SunlightCoreBlockEntity blockEntity) {
		return TEXTURE;
	}
}
