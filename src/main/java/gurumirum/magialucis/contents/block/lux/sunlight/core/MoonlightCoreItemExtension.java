package gurumirum.magialucis.contents.block.lux.sunlight.core;

import gurumirum.magialucis.contents.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MoonlightCoreItemExtension extends BaseSunlightCoreItemExtension{
	@Override
	protected @NotNull BlockState getBlockState() {
		return ModBlocks.MOONLIGHT_CORE.block().defaultBlockState();
	}

	@Override
	protected @Nullable ResourceLocation spinningThingTexture() {
		return MoonlightCoreBlockEntityRenderer.TEXTURE;
	}
}
