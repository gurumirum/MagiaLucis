package gurumirum.magialucis.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.impl.RGB332;
import net.minecraft.client.Minecraft;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = MagiaLucisMod.MODID, value = Dist.CLIENT)
public record LightEffect(
		float radius,
		Vec3 start,
		Vec3 end,
		int color
) {
	private static final List<LightEffect> lightEffects = new ArrayList<>();

	public static void addCircularEffect(Vec3 point, byte color, Vector3d luxFlow,
	                                     double rMaxTransfer, double gMaxTransfer, double bMaxTransfer) {
		addEffect(new LightEffect(.8f, point, point,
				getProportionalLight(color, luxFlow, rMaxTransfer, gMaxTransfer, bMaxTransfer)));
	}

	public static void addCylindricalEffect(Vec3 start, Vec3 end, byte color, Vector3d luxFlow,
	                                        double rMaxTransfer, double gMaxTransfer, double bMaxTransfer) {
		addEffect(new LightEffect(.4f, start, end,
				getProportionalLight(color, luxFlow, rMaxTransfer, gMaxTransfer, bMaxTransfer)));
	}

	public static void addEffect(LightEffect lightEffect) {
		lightEffects.add(lightEffect);
	}

	private static int getProportionalLight(byte color, Vector3d luxFlow,
	                                        double rMaxTransfer, double gMaxTransfer, double bMaxTransfer) {
		int r = rMaxTransfer <= 0 ? 0 : (int)(RGB332.rBrightness(color) * (luxFlow.x / rMaxTransfer) * 255);
		int g = gMaxTransfer <= 0 ? 0 : (int)(RGB332.gBrightness(color) * (luxFlow.y / gMaxTransfer) * 255);
		int b = bMaxTransfer <= 0 ? 0 : (int)(RGB332.bBrightness(color) * (luxFlow.z / bMaxTransfer) * 255);

		return FastColor.ARGB32.color(r, g, b);
	}

	@SubscribeEvent
	public static void onRender(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || lightEffects.isEmpty()) return;

		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) return;

		PoseStack poseStack = event.getPoseStack();
		Vec3 cameraPos = event.getCamera().getPosition();

		poseStack.pushPose();
		poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

		for (LightEffect e : lightEffects) {
			poseStack.pushPose();
			if (e.start.equals(e.end)) {
				poseStack.translate(e.start.x, e.start.y, e.start.z);
				poseStack.scale(e.radius, e.radius, e.radius);

				VertexConsumer vc = mc.renderBuffers().bufferSource().getBuffer(ModRenderTypes.LIGHT);
				RenderShapes.sphere(poseStack, vc, e.color);
			} else {

			}
			poseStack.popPose();
		}

		poseStack.popPose();
		lightEffects.clear();
	}
}
