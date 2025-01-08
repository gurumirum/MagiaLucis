package gurumirum.gemthing.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

import static gurumirum.gemthing.GemthingMod.MODID;
import static gurumirum.gemthing.GemthingMod.id;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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
					.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
					.setOutputState(RenderType.PARTICLES_TARGET)
					.createCompositeState(true));

	public static final RenderType RELAY = RenderType.create(
			MODID + "_relay",
			DefaultVertexFormat.POSITION_COLOR_NORMAL,
			VertexFormat.Mode.TRIANGLES,
			1536,
			false,
			true,
			RenderType.CompositeState.builder()
					.setShaderState(new RenderStateShard.ShaderStateShard(
							() -> Objects.requireNonNull(ModRenderTypes.relayShader)
					))
					.setWriteMaskState(RenderType.COLOR_WRITE)
					.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
					// .setCullState(RenderStateShard.NO_CULL)
					.setOutputState(RenderType.PARTICLES_TARGET)
					.createCompositeState(true));

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
					.setCullState(RenderStateShard.NO_CULL)
					.createCompositeState(true)));

	private static final Function<ResourceLocation, RenderType> BLOCK_HIGHLIGHT = Util.memoize(texture -> RenderType.create(
			MODID + "_block_highlight",
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
					.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
					.setDepthTestState(RenderType.NO_DEPTH_TEST)
					.setOutputState(RenderType.PARTICLES_TARGET)
					.createCompositeState(true)));

	private static ShaderInstance relayShader;

	public static RenderType positionTextureColor(ResourceLocation texture) {
		return POS_TEX_C.apply(texture);
	}

	public static RenderType blockHighlight(ResourceLocation texture) {
		return BLOCK_HIGHLIGHT.apply(texture);
	}

	@SubscribeEvent
	public static void registerShaders(RegisterShadersEvent event) throws IOException {
		event.registerShader(new ShaderInstance(event.getResourceProvider(), id("relay"),
				DefaultVertexFormat.POSITION_COLOR_NORMAL), s -> relayShader = s);
	}
}
