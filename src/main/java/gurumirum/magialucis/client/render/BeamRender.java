package gurumirum.magialucis.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import gurumirum.magialucis.client.render.BeamEffect.CoordinateSystem;
import gurumirum.magialucis.contents.item.BeamSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static gurumirum.magialucis.MagiaLucisMod.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class BeamRender {
	private BeamRender() {}

	private static final List<BeamEffect> beamEffects = new ArrayList<>();

	public static void addBeamEffect(Player player, ItemStack stack, BeamSource beamSource) {
		addBeamEffect(new PlayerBeamEffect(player, stack, beamSource));
	}

	public static void addBeamEffect(BeamEffect beamEffect) {
		beamEffects.add(beamEffect);
	}

	@SubscribeEvent
	public static void beforeClientTick(ClientTickEvent.Pre event) {
		beamEffects.removeIf(e -> e.lifetime() == BeamEffect.Lifetime.TICK);
	}

	@SubscribeEvent
	public static void onRender(RenderLevelStageEvent event) {
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
			renderBeam(event);
		}
	}

	private static void renderBeam(RenderLevelStageEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) return;

		PoseStack poseStack = event.getPoseStack();
		Vec3 cameraPos = event.getCamera().getPosition();
		float partialTicks = event.getPartialTick().getGameTimeDeltaPartialTick(false);

		poseStack.pushPose();
		poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

		for (int i = 0; i < beamEffects.size(); i++) {
			BeamEffect beamEffect = beamEffects.get(i);
			drawBeam(poseStack, beamEffect, cameraPos, partialTicks);
			if (beamEffect.lifetime() == BeamEffect.Lifetime.FRAME)
				beamEffects.remove(i--);
		}

		PlayerBeamEffect.clearBeamStarts();

		poseStack.popPose();
	}

	private static final Vector3f _beamStart = new Vector3f();
	private static final Vector3f _beamStartCopy = new Vector3f();
	private static final Vector3f _beamEnd = new Vector3f();

	private static void drawBeam(PoseStack poseStack, BeamEffect beamEffect, Vec3 cameraPos, float partialTicks) {
		var beamStart = beamEffect.beamStart(_beamStart, partialTicks);
		transformCoordinates(poseStack, cameraPos, _beamStart, beamStart);

		var beamEnd = beamEffect.beamEnd(_beamStartCopy.set(_beamStart), _beamEnd, partialTicks);
		if (beamEnd == null) return;
		transformCoordinates(poseStack, cameraPos, _beamEnd, beamEnd);

		Minecraft mc = Minecraft.getInstance();
		MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

		drawBeam(poseStack, bufferSource, beamEffect, _beamStart, _beamEnd, partialTicks);
	}

	private static void transformCoordinates(PoseStack poseStack, Vec3 cameraPos,
	                                         Vector3f coord, CoordinateSystem system) {
		switch (system) {
			case VIEW -> new Matrix4f()
					.mul(RenderSystem.getProjectionMatrix())
					.mul(RenderSystem.getModelViewMatrix())
					.mul(poseStack.last().pose())
					.unproject(coord, new int[]{-1, -1, 2, 2}, coord);
			case WORLD -> {}
			case MODEL -> coord.add((float)cameraPos.x, (float)cameraPos.y, (float)cameraPos.z);
		}
	}

	// ItemRenderer#render
	public static void applyItemTransform(@NotNull PoseStack poseStack, @NotNull ItemStack stack, @NotNull LivingEntity entity, @NotNull HumanoidArm arm, boolean firstPerson) {
		ItemDisplayContext itemDisplayContext = arm == HumanoidArm.LEFT ?
				firstPerson ? ItemDisplayContext.FIRST_PERSON_LEFT_HAND : ItemDisplayContext.THIRD_PERSON_LEFT_HAND :
				firstPerson ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;

		BakedModel model = Minecraft.getInstance().getItemRenderer()
				.getModel(stack, entity.level(), entity, entity.getId() + itemDisplayContext.ordinal());

		model.applyTransform(itemDisplayContext, poseStack, arm == HumanoidArm.LEFT);
		poseStack.translate(-.5f, -.5f, -.5f);
	}

	public static void drawBeam(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource,
	                            BeamEffect beamEffect, Vector3f start, Vector3f end,
	                            float partialTick) {
		Vector3f vec = new Vector3f(start).sub(end);
		Quaternionf angle = new Vector3f(0, 0, -1).rotationTo(vec, new Quaternionf());

		poseStack.pushPose();
		poseStack.translate(start.x, start.y, start.z);
		poseStack.mulPose(angle);

		float diameter = beamEffect.diameter(partialTick);
		poseStack.scale(diameter, diameter, 1);

		poseStack.mulPose(Axis.ZP.rotation(beamEffect.rotation(partialTick)));

		int color = beamEffect.color(partialTick);
		RenderShapes.untexturedZGradientBox(
				poseStack,
				bufferSource.getBuffer(ModRenderTypes.BEAM),
				-.5f, -.5f, 0,
				.5f, .5f, vec.length() + .5f,
				color, color);

		poseStack.popPose();
	}
}
