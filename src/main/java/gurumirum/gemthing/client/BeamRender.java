package gurumirum.gemthing.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.contents.item.WandItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.UUID;

import static gurumirum.gemthing.GemthingMod.MODID;
import static gurumirum.gemthing.GemthingMod.id;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class BeamRender {
	private BeamRender() {}

	public static final ItemTransform T_FIRST_PERSON_LEFT_HAND = new ItemTransform(
			new Vector3f(-128.5f, 90, 0),
			new Vector3f(-7, 2.2f, 1.13f).mul(0.0625f),
			new Vector3f(0.68f));
	public static final ItemTransform T_FIRST_PERSON_RIGHT_HAND = new ItemTransform(
			new Vector3f(-128.5f, -90, 0),
			new Vector3f(-7, 2.2f, 1.13f).mul(0.0625f),
			new Vector3f(0.68f));

	public static final ItemTransform T_THIRD_PERSON_LEFT_HAND = new ItemTransform(
			new Vector3f(-37.5f, -90, 0),
			new Vector3f(0, -.5f, -5.75f).mul(0.0625f),
			new Vector3f(0.85f));
	public static final ItemTransform T_THIRD_PERSON_RIGHT_HAND = new ItemTransform(
			new Vector3f(-37.5f, 90, 0),
			new Vector3f(0, -.5f, -5.75f).mul(0.0625f),
			new Vector3f(0.85f));

	public static final ResourceLocation TEXTURE_BEAM_START = id("textures/effect/wand_beam_start.png");

	public static final RenderType THING = RenderType.create(
			MODID + "_thing",
			DefaultVertexFormat.POSITION_TEX_COLOR,
			VertexFormat.Mode.QUADS,
			1536,
			true,
			false,
			RenderType.CompositeState.builder()
					.setShaderState(new RenderStateShard.ShaderStateShard(
							GameRenderer::getPositionTexColorShader
					))
					.setTextureState(new RenderStateShard.TextureStateShard(TEXTURE_BEAM_START, false, false))
					.setWriteMaskState(RenderType.COLOR_DEPTH_WRITE)
					.setTransparencyState(RenderType.NO_TRANSPARENCY)
					.setCullState(RenderStateShard.NO_CULL)
					.setOutputState(RenderType.PARTICLES_TARGET)
					.createCompositeState(false)
	);

	public static final RenderType THING2 = RenderType.create(
			MODID + "_thing2",
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

	private static final Object2ObjectOpenHashMap<UUID, Vector3f> PLAYER_BEAM_STARTS = new Object2ObjectOpenHashMap<>();

	private static final Vector3f _beamEnd = new Vector3f();

	public static Vector3f getOrCreatePlayerBeamStart(Player player) {
		return PLAYER_BEAM_STARTS.computeIfAbsent(player.getUUID(), u -> new Vector3f(Float.NaN));
	}

	@SubscribeEvent
	public static void onRender(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) return;

		PoseStack poseStack = event.getPoseStack();
		MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
		Vec3 cameraPos = event.getCamera().getPosition();
		float partialTicks = event.getPartialTick().getGameTimeDeltaPartialTick(false);

		poseStack.pushPose();
		poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

		for (AbstractClientPlayer player : mc.level.players()) {
			if (!player.isUsingItem() ||
					!player.getUseItem().is(Contents.Items.WAND.asItem())) continue;

			var beamStart = PLAYER_BEAM_STARTS.get(player.getUUID());
			if (beamStart == null || !beamStart.isFinite()) continue;

			Vec3 start = player.getEyePosition();
			Vec3 end = start.add(player.getLookAngle().scale(WandItem.DISTANCE));
			BlockHitResult hitResult = WandItem.trace(player, start, end);

			if (hitResult.getType() == HitResult.Type.BLOCK) end = hitResult.getLocation();

			_beamEnd.set(end.x, end.y, end.z);

			if (player == mc.getCameraEntity() && mc.options.getCameraType().isFirstPerson()) {
				// for 1st person perspective the start position is screen world coordination
				var vec = new Matrix4f()
						.mul(RenderSystem.getProjectionMatrix())
						.mul(RenderSystem.getModelViewMatrix())
						.mul(poseStack.last().pose())
						.unproject(beamStart,
								new int[]{-1, -1, 2, 2},
								new Vector3f());

				drawBeam(poseStack, bufferSource, player, vec, _beamEnd, partialTicks, true);
			} else {
				// for 3rd person perspective the start position is in world coordination
				beamStart.add((float)cameraPos.x, (float)cameraPos.y, (float)cameraPos.z);
				drawBeam(poseStack, bufferSource, player, beamStart, _beamEnd, partialTicks, false);
			}

			beamStart.set(Float.NaN);
		}

		poseStack.popPose();
	}

	// ItemRenderer#render
	public static void applyItemTransform(@NotNull PoseStack poseStack, @NotNull HumanoidArm arm, boolean firstPerson) {
		ItemTransform transform;
		if (arm == HumanoidArm.LEFT) transform = firstPerson ? T_FIRST_PERSON_LEFT_HAND : T_THIRD_PERSON_LEFT_HAND;
		else transform = firstPerson ? T_FIRST_PERSON_RIGHT_HAND : T_THIRD_PERSON_RIGHT_HAND;
		transform.apply(arm == HumanoidArm.LEFT, poseStack);
		poseStack.translate(-.5f, -.5f, -.5f);
	}

	public static void drawWandEffect(@NotNull PoseStack poseStack, @NotNull Player player,
	                                  float partialTick, boolean debug) {
		var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

		if (debug) {
			VertexConsumer vc = bufferSource.getBuffer(RenderType.lines());

			for (int i = 0; i <= 16; i++) {
				vc.addVertex(poseStack.last(), i / 16f, 0, 8.5f / 16).setColor(0xFFFFFF00).setNormal(poseStack.last(), 0, 0, 1);
				vc.addVertex(poseStack.last(), i / 16f, 1, 8.5f / 16).setColor(0xFFFF0000).setNormal(poseStack.last(), 0, 0, 1);
				vc.addVertex(poseStack.last(), 0, i / 16f, 8.5f / 16).setColor(0xFFFFFF00).setNormal(poseStack.last(), 0, 0, 1);
				vc.addVertex(poseStack.last(), 1, i / 16f, 8.5f / 16).setColor(0xFFFF0000).setNormal(poseStack.last(), 0, 0, 1);
				vc.addVertex(poseStack.last(), i / 16f, 0, 7.5f / 16).setColor(0xFFFFFF00).setNormal(poseStack.last(), 0, 0, 1);
				vc.addVertex(poseStack.last(), i / 16f, 1, 7.5f / 16).setColor(0xFFFF0000).setNormal(poseStack.last(), 0, 0, 1);
				vc.addVertex(poseStack.last(), 0, i / 16f, 7.5f / 16).setColor(0xFFFFFF00).setNormal(poseStack.last(), 0, 0, 1);
				vc.addVertex(poseStack.last(), 1, i / 16f, 7.5f / 16).setColor(0xFFFF0000).setNormal(poseStack.last(), 0, 0, 1);
			}
			bufferSource.endBatch(RenderType.lines());
		}

		poseStack.translate(14 / 16f, 14 / 16f, .5);
		poseStack.scale(.5f, .5f, .5f);
		poseStack.mulPose(Axis.ZP.rotationDegrees(45));
		poseStack.mulPose(Axis.XP.rotationDegrees((player.getTicksUsingItem() % 360 + partialTick) * 20));

		VertexConsumer vc = bufferSource.getBuffer(BeamRender.THING);
		vc.addVertex(poseStack.last(), 0, -1, -1).setUv(0, 0).setColor(-1);
		vc.addVertex(poseStack.last(), 0, 1, -1).setUv(1, 0).setColor(-1);
		vc.addVertex(poseStack.last(), 0, 1, 1).setUv(1, 1).setColor(-1);
		vc.addVertex(poseStack.last(), 0, -1, 1).setUv(0, 1).setColor(-1);
	}

	public static void drawBeam(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource,
	                            AbstractClientPlayer player, Vector3f start, Vector3f end,
	                            float partialTick, boolean firstPerson) {
		var vec = new Vector3f(start).sub(end);
		var angle = new Vector3f(0, 0, -1).rotationTo(vec, new Quaternionf());
		var len = vec.length();

		/*
		PoseStack.Pose pose = poseStack.last();
		VertexConsumer vc = bufferSource.getBuffer(RenderType.lines());
		vc.addVertex(pose, start).setColor(0xFFFF0000).setNormal(pose, 0, 1, 0);
		vc.addVertex(pose, end).setColor(0xFF0000FF).setNormal(pose, 0, 1, 0);
		bufferSource.endBatch(RenderType.lines());
		*/

		poseStack.pushPose();
		poseStack.translate(start.x, start.y, start.z);
		poseStack.mulPose(angle);
		poseStack.scale(0.1f, 0.1f, 1);
		poseStack.mulPose(Axis.ZP.rotationDegrees((player.getTicksUsingItem() % 360 + partialTick) * 20));

		renderLineBox(
				poseStack,
				bufferSource.getBuffer(THING2),
				-1, -1, 0,
				1, 1, len + .5f,
				0xFFFF0000, 0xFF00FFFF);

		poseStack.popPose();
	}

	private static void renderLineBox(PoseStack poseStack, VertexConsumer vc,
	                                  float x1, float y1, float z1,
	                                  float x2, float y2, float z2,
	                                  int c1, int c2) {
		PoseStack.Pose pose = poseStack.last();

		vc.addVertex(pose, x1, y1, z1).setColor(c1);
		vc.addVertex(pose, x1, y2, z1).setColor(c1);
		vc.addVertex(pose, x2, y2, z1).setColor(c1);
		vc.addVertex(pose, x2, y1, z1).setColor(c1);

		vc.addVertex(pose, x1, y1, z2).setColor(c2);
		vc.addVertex(pose, x1, y1, z1).setColor(c1);
		vc.addVertex(pose, x2, y1, z1).setColor(c1);
		vc.addVertex(pose, x2, y1, z2).setColor(c2);

		vc.addVertex(pose, x1, y1, z1).setColor(c1);
		vc.addVertex(pose, x1, y1, z2).setColor(c2);
		vc.addVertex(pose, x1, y2, z2).setColor(c2);
		vc.addVertex(pose, x1, y2, z1).setColor(c1);

		vc.addVertex(pose, x2, y2, z2).setColor(c2);
		vc.addVertex(pose, x2, y2, z1).setColor(c1);
		vc.addVertex(pose, x1, y2, z1).setColor(c1);
		vc.addVertex(pose, x1, y2, z2).setColor(c2);

		vc.addVertex(pose, x2, y2, z1).setColor(c1);
		vc.addVertex(pose, x2, y2, z2).setColor(c2);
		vc.addVertex(pose, x2, y1, z2).setColor(c2);
		vc.addVertex(pose, x2, y1, z1).setColor(c1);

		vc.addVertex(pose, x2, y1, z2).setColor(c2);
		vc.addVertex(pose, x2, y2, z2).setColor(c2);
		vc.addVertex(pose, x1, y2, z2).setColor(c2);
		vc.addVertex(pose, x1, y1, z2).setColor(c2);
	}
}
