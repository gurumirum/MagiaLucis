package gurumirum.magialucis.contents.block.lux.sunlight.core;

import gurumirum.magialucis.MagiaLucisMod;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MoonlightCoreBlockEntityRenderer extends BaseSunlightCoreBlockEntityRenderer<MoonlightCoreBlockEntity> {
	public static final ResourceLocation TEXTURE = MagiaLucisMod.id("textures/effect/moonlight_core.png");

	public MoonlightCoreBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected @Nullable ResourceLocation spinningThingTexture(@NotNull MoonlightCoreBlockEntity blockEntity) {
		return TEXTURE;
	}
}
