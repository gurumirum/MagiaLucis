package gurumirum.magialucis.client.render.light;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.client.render.ModRenderTypes;
import gurumirum.magialucis.client.render.RenderShapes;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static gurumirum.magialucis.client.render.light.LightEffect.LineEffect;
import static gurumirum.magialucis.client.render.light.LightEffect.SpotEffect;

@EventBusSubscriber(modid = MagiaLucisMod.MODID, value = Dist.CLIENT)
public final class LightEffectRender {
	private LightEffectRender() {}

	private static final List<LightEffectProvider> lightEffectProviders = new ArrayList<>();
	private static final List<LightEffect> effects = new ArrayList<>();

	public static void register(LightEffectProvider provider) {
		lightEffectProviders.add(provider);
	}

	private static final Vector3f currentLightStart = new Vector3f();
	private static final Vector3f currentLightEnd = new Vector3f();
	private static float currentLightRadius;

	public static Vector3f lightStart(Vector3f dest) {
		return dest.set(currentLightStart);
	}

	public static Vector3f lightEnd(Vector3f dest) {
		return dest.set(currentLightEnd);
	}

	public static float lightRadius() {
		return currentLightRadius;
	}

	private static boolean _setup;

	@SubscribeEvent
	public static void onRender(RenderLevelStageEvent event) {
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
			renderLightEffects(event);
		}
	}

	private static void renderLightEffects(RenderLevelStageEvent event) {
		if (lightEffectProviders.isEmpty()) return;

		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) return;

		_setup = false;

		float partialTicks = event.getPartialTick().getGameTimeDeltaPartialTick(false);

		for (int i = 0; i < lightEffectProviders.size(); i++) {
			LightEffectProvider p = lightEffectProviders.get(i);
			if (!p.isLightEffectProviderValid()) {
				lightEffectProviders.remove(i--);
				continue;
			}

			p.getLightEffects(partialTicks, effects::add);
			for (LightEffect e : effects) draw(event, e);
			effects.clear();
		}

		if (_setup) {
			_setup = false;
			event.getPoseStack().popPose();
			RenderSystem.depthMask(true);
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
		}
	}

	private static void draw(RenderLevelStageEvent event, LightEffect e) {
		PoseStack poseStack = event.getPoseStack();
		BufferBuilder bufferBuilder = Tesselator.getInstance()
				.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

		setup(event);

		poseStack.pushPose();
		poseStack.translate(e.start().x, e.start().y, e.start().z);

		currentLightStart.set(0, 0, 0);
		poseStack.last().pose().transformPosition(currentLightStart);

		float r = e.radius();
		currentLightRadius = r;

		switch (e) {
			case LineEffect l -> {
				RenderSystem.setShader(ModRenderTypes::lightCylinderShader);
				Vec3 vec = l.end().subtract(l.start());

				poseStack.mulPose(new Vector3f(0, 1, 0)
						.rotationTo((float)vec.x, (float)vec.y, (float)vec.z, new Quaternionf()));
				poseStack.scale(r, r, r);

				// TODO variable falloff padding with hit angle?
				float endOffset = (float)((vec.length() + (l.fallOff() ? 0.1 : 0.015)) / r);

				currentLightEnd.set(0, endOffset, 0);
				poseStack.last().pose().transformPosition(currentLightEnd);

				RenderShapes.cylinder(poseStack, bufferBuilder,
						-0.015f / r, endOffset,
						e.color(), false);
			}
			case SpotEffect ignored -> {
				RenderSystem.setShader(ModRenderTypes::lightSphereShader);

				poseStack.scale(r, r, r);

				RenderShapes.sphere(poseStack, bufferBuilder, e.color());
			}
		}

		BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

		poseStack.popPose();
	}

	private static void setup(RenderLevelStageEvent event) {
		if (!_setup) {
			_setup = true;
			RenderSystem.colorMask(true, true, true, true);
			RenderSystem.depthMask(false);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
			RenderSystem.enableDepthTest();

			Vec3 cameraPos = event.getCamera().getPosition();
			PoseStack poseStack = event.getPoseStack();

			poseStack.pushPose();
			poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		}
	}

	@SubscribeEvent
	public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
		lightEffectProviders.clear();
	}

	@SubscribeEvent
	public static void onLevelUnload(LevelEvent.Unload event) {
		LevelAccessor level = event.getLevel();
		if (!level.isClientSide()) return;

		for (LightEffectProvider p : lightEffectProviders) {
			p.onLevelUnload(level);
		}
	}
}
