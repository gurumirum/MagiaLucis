package gurumirum.magialucis.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.impl.RGB332;
import net.minecraft.client.Minecraft;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = MagiaLucisMod.MODID, value = Dist.CLIENT)
public record LightEffect(
		float radius,
		Vec3 start,
		Vec3 end,
		int color,
		boolean fallOff
) {
	public LightEffect(float radius, Vec3 point, int color) {
		this(radius, point, point, color, false);
	}

	private static final List<LightEffect> lightEffects = new ArrayList<>();

	public static void addCircularEffect(float radius, Vec3 point, int color) {
		addEffect(new LightEffect(radius, point, color));
	}

	public static void addCylindricalEffect(float radius, Vec3 start, Vec3 end, int color, boolean fallOff) {
		addEffect(new LightEffect(radius, start, end, color, fallOff));
	}

	public static void addEffect(LightEffect lightEffect) {
		lightEffects.add(lightEffect);
	}

	public static int getProportionalLight(byte color, Vector3d luxFlow,
	                                       double rMaxTransfer, double gMaxTransfer, double bMaxTransfer) {
		int r = rMaxTransfer <= 0 ? 0 : (int)(RGB332.rBrightness(color) * (luxFlow.x / rMaxTransfer) * 255);
		int g = gMaxTransfer <= 0 ? 0 : (int)(RGB332.gBrightness(color) * (luxFlow.y / gMaxTransfer) * 255);
		int b = bMaxTransfer <= 0 ? 0 : (int)(RGB332.bBrightness(color) * (luxFlow.z / bMaxTransfer) * 255);

		return FastColor.ARGB32.color(r, g, b);
	}

	private static @Nullable LightEffect currentLightEffect;
	private static final Vector3f currentLightStart = new Vector3f();
	private static final Vector3f currentLightEnd = new Vector3f();

	public static Vector3f lightStart(Vector3f dest) {
		if (currentLightEffect == null) return dest.zero();
		return dest.set(currentLightStart);
	}

	public static Vector3f lightEnd(Vector3f dest) {
		if (currentLightEffect == null) return dest.zero();
		return dest.set(currentLightEnd);
	}

	public static float lightRadius() {
		if (currentLightEffect == null) return 0;
		return currentLightEffect.radius;
	}

	@SubscribeEvent
	public static void onRender(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || lightEffects.isEmpty()) return;

		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) return;

		PoseStack poseStack = event.getPoseStack();

		boolean setup = false;

		for (LightEffect e : lightEffects) {
			// cull check

			if (!setup) {
				setup = true;
				RenderSystem.colorMask(true, true, true, true);
				RenderSystem.depthMask(false);
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

				Vec3 cameraPos = event.getCamera().getPosition();
				poseStack.pushPose();
				poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
			}

			BufferBuilder bufferBuilder = Tesselator.getInstance()
					.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
			poseStack.pushPose();
			poseStack.translate(e.start.x, e.start.y, e.start.z);

			currentLightEffect = e;
			currentLightStart.set(0, 0, 0);
			poseStack.last().pose().transformPosition(currentLightStart);

			if (e.start.equals(e.end)) {
				RenderSystem.setShader(ModRenderTypes::lightSphereShader);

				poseStack.scale(e.radius, e.radius, e.radius);

				RenderShapes.sphere(poseStack, bufferBuilder, e.color);
			} else {
				currentLightEnd.set(e.end.x - e.start.x, e.end.y - e.start.y, e.end.z - e.start.z);
				poseStack.last().pose().transformPosition(currentLightEnd);

				RenderSystem.setShader(ModRenderTypes::lightCylinderShader);
				Vec3 vec = e.end.subtract(e.start);

				poseStack.mulPose(new Vector3f(0, 1, 0)
						.rotationTo((float)vec.x, (float)vec.y, (float)vec.z, new Quaternionf()));
				poseStack.scale(e.radius, e.radius, e.radius);
				RenderShapes.cylinder(poseStack, bufferBuilder, (float)(vec.length() / e.radius), e.color);
			}

			BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

			poseStack.popPose();
			currentLightEffect = null;
		}

		if (setup) {
			poseStack.popPose();
			RenderSystem.depthMask(true);
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
		}
		lightEffects.clear();
	}
}
