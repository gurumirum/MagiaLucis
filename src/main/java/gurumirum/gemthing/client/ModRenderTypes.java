package gurumirum.gemthing.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

import static gurumirum.gemthing.GemthingMod.MODID;

public final class ModRenderTypes {
	private ModRenderTypes() {}

	public static final RenderType BEAM = RenderType.create(
			MODID + "_beam",
			DefaultVertexFormat.POSITION_COLOR,
			VertexFormat.Mode.QUADS,
			1536,
			true,
			false,
			RenderType.CompositeState.builder()
					.setShaderState(new RenderStateShard.ShaderStateShard(
							GameRenderer::getPositionColorShader
					))
					.setWriteMaskState(RenderType.COLOR_DEPTH_WRITE)
					.setTransparencyState(RenderType.NO_TRANSPARENCY)
					.setOutputState(RenderType.PARTICLES_TARGET)
					.createCompositeState(false)
	);

	private static final Function<ResourceLocation, RenderType> POS_TEX_C = Util.memoize(texture -> RenderType.create(
			MODID + "_ptc",
			DefaultVertexFormat.POSITION_TEX_COLOR,
			VertexFormat.Mode.QUADS,
			1536,
			true,
			false,
			RenderType.CompositeState.builder()
					.setShaderState(new RenderStateShard.ShaderStateShard(
							GameRenderer::getPositionTexColorShader
					))
					.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
					.setWriteMaskState(RenderType.COLOR_DEPTH_WRITE)
					.setTransparencyState(RenderType.NO_TRANSPARENCY)
					.setCullState(RenderStateShard.NO_CULL)
					.setOutputState(RenderType.PARTICLES_TARGET)
					.createCompositeState(false)
	));

	public static RenderType positionTextureColor(ResourceLocation texture) {
		return POS_TEX_C.apply(texture);
	}
}
