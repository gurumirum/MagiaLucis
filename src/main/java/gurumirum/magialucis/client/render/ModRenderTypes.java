package gurumirum.magialucis.client.render;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import gurumirum.magialucis.client.render.light.LightCylinderShaderInstance;
import gurumirum.magialucis.client.render.light.LightSphereShaderInstance;
import gurumirum.magialucis.contents.item.wand.ConfigurationWandOverlay;
import net.minecraft.Util;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static gurumirum.magialucis.MagiaLucisMod.MODID;
import static gurumirum.magialucis.MagiaLucisMod.id;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModRenderTypes {
	private ModRenderTypes() {}

	public static final MultiBufferSource.BufferSource STUB_BUFFER = MultiBufferSource
			.immediate(new ByteBufferBuilder(1536));

	public static final RenderType BEAM = RenderType.create(
			MODID + "_beam",
			DefaultVertexFormat.POSITION_COLOR,
			VertexFormat.Mode.QUADS,
			RenderType.TRANSIENT_BUFFER_SIZE,
			false,
			false,
			RenderType.CompositeState.builder()
					.setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
					.setWriteMaskState(RenderType.COLOR_DEPTH_WRITE)
					.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
					.setOutputState(RenderType.PARTICLES_TARGET)
					.createCompositeState(false));

	public static final RenderType PRISM_ITEM_ENTITY = RenderType.create(
			MODID + "_prism_item_entity",
			DefaultVertexFormat.POSITION_COLOR_NORMAL,
			VertexFormat.Mode.TRIANGLES,
			RenderType.TRANSIENT_BUFFER_SIZE,
			false,
			false,
			RenderType.CompositeState.builder()
					.setShaderState(new RenderStateShard.ShaderStateShard(ModRenderTypes::prismShader))
					.setWriteMaskState(RenderType.COLOR_WRITE)
					.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
					.setOutputState(RenderType.ITEM_ENTITY_TARGET)
					.createCompositeState(false));

	private static final Function<ResourceLocation, RenderType> POS_TEX_C = Util.memoize(texture -> RenderType.create(
			MODID + "_ptc",
			DefaultVertexFormat.POSITION_TEX_COLOR,
			VertexFormat.Mode.QUADS,
			RenderType.TRANSIENT_BUFFER_SIZE,
			false,
			false,
			RenderType.CompositeState.builder()
					.setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexColorShader))
					.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
					.setWriteMaskState(RenderType.COLOR_DEPTH_WRITE)
					.setCullState(RenderStateShard.NO_CULL)
					.createCompositeState(true)));

	public static final RenderType BLOCK_HIGHLIGHT_BOX = RenderType.create(
			MODID + "_block_highlight_box",
			DefaultVertexFormat.POSITION_TEX_COLOR,
			VertexFormat.Mode.QUADS,
			RenderType.TRANSIENT_BUFFER_SIZE,
			false,
			false,
			RenderType.CompositeState.builder()
					.setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexColorShader))
					.setTextureState(new RenderStateShard.TextureStateShard(ConfigurationWandOverlay.BLOCK_HIGHLIGHT, false, false))
					.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
					.setDepthTestState(RenderType.NO_DEPTH_TEST)
					.setOutputState(RenderType.PARTICLES_TARGET)
					.createCompositeState(false));

	public static final RenderType BLOCK_HIGHLIGHT_LINE = RenderType.create(
			MODID + "_block_highlight_line",
			DefaultVertexFormat.POSITION_COLOR,
			VertexFormat.Mode.QUADS,
			RenderType.TRANSIENT_BUFFER_SIZE,
			false,
			false,
			RenderType.CompositeState.builder()
					.setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
					.setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
					.setOutputState(RenderType.PARTICLES_TARGET)
					.createCompositeState(false));

	public static final Function<ResourceLocation, RenderType> WHITE_CRUMBLING = Util.memoize(tex -> RenderType.create(
			MODID + "_white_crumbling",
			DefaultVertexFormat.BLOCK,
			VertexFormat.Mode.QUADS,
			RenderType.TRANSIENT_BUFFER_SIZE,
			false,
			true,
			RenderType.CompositeState.builder()
					.setShaderState(RenderType.RENDERTYPE_CRUMBLING_SHADER)
					.setTextureState(new RenderStateShard.TextureStateShard(tex, false, false))
					.setTransparencyState(RenderType.ADDITIVE_TRANSPARENCY)
					.setWriteMaskState(RenderType.COLOR_WRITE)
					.setLayeringState(RenderType.POLYGON_OFFSET_LAYERING)
					.createCompositeState(false)));

	private static ShaderInstance prismShader;
	private static ShaderInstance lightCylinderShader;
	private static ShaderInstance lightSphereShader;

	private static @Nullable List<RenderType> whiteDestroyStages;

	public static RenderType positionTextureColor(ResourceLocation texture) {
		return POS_TEX_C.apply(texture);
	}

	public static ShaderInstance prismShader() {
		return Objects.requireNonNull(prismShader);
	}

	public static ShaderInstance lightCylinderShader() {
		return Objects.requireNonNull(lightCylinderShader);
	}

	public static ShaderInstance lightSphereShader() {
		return Objects.requireNonNull(lightSphereShader);
	}

	public static @NotNull List<RenderType> whiteDestroyStages() {
		if (whiteDestroyStages == null) {
			RenderType[] arr = new RenderType[10];

			for (int i = 0; i < arr.length; i++) {
				arr[i] = WHITE_CRUMBLING.apply(id("textures/block/white_destroy_stage/" + i + ".png"));
			}

			whiteDestroyStages = List.of(arr);
		}
		return whiteDestroyStages;
	}

	@SubscribeEvent
	public static void registerShaders(RegisterShadersEvent event) throws IOException {
		event.registerShader(new ShaderInstance(event.getResourceProvider(), id("prism"),
				DefaultVertexFormat.POSITION_COLOR_NORMAL), s -> prismShader = s);
		event.registerShader(new LightCylinderShaderInstance(event.getResourceProvider(), id("light_cylinder"),
				DefaultVertexFormat.POSITION_COLOR), s -> lightCylinderShader = s);
		event.registerShader(new LightSphereShaderInstance(event.getResourceProvider(), id("light_sphere"),
				DefaultVertexFormat.POSITION_COLOR), s -> lightSphereShader = s);
	}

	@SubscribeEvent
	public static void registerRenderBuffers(RegisterRenderBuffersEvent event) {
		event.registerRenderBuffer(PRISM_ITEM_ENTITY);
		event.registerRenderBuffer(BLOCK_HIGHLIGHT_BOX);
		event.registerRenderBuffer(BLOCK_HIGHLIGHT_LINE);

		for (RenderType t : whiteDestroyStages()) event.registerRenderBuffer(t);
	}
}
