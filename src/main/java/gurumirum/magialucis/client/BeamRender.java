package gurumirum.magialucis.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import gurumirum.magialucis.contents.item.BeamSource;
import gurumirum.magialucis.contents.item.wand.AncientLightWandItem;
import gurumirum.magialucis.impl.ancientlight.AncientLightCrafting;
import gurumirum.magialucis.impl.ancientlight.AncientLightRecord;
import gurumirum.magialucis.impl.ancientlight.LocalAncientLightManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
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

import java.util.List;
import java.util.UUID;

import static gurumirum.magialucis.MagiaLucisMod.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class BeamRender {
	private BeamRender() {}

	private static final Object2ObjectOpenHashMap<UUID, Vector3f> playerBeamStarts = new Object2ObjectOpenHashMap<>();

	private static final Vector3f _beamEnd = new Vector3f();

	public static Vector3f getOrCreatePlayerBeamStart(Player player) {
		return playerBeamStarts.computeIfAbsent(player.getUUID(), u -> new Vector3f(Float.NaN));
	}

	@SubscribeEvent
	public static void onRender(RenderLevelStageEvent event) {
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
			renderCrumblingEffect(event);
		} else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
			renderBeam(event);
		}
	}

	private static void renderCrumblingEffect(RenderLevelStageEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) return;

		Vec3 cameraPos = event.getCamera().getPosition();
		LocalAncientLightManager m = AncientLightCrafting.tryGetLocalManager();
		if (m == null) return;
		AncientLightRecord record = m.record();
		if (record == null) return;

		record.forEachBlock((pos, progress, totalProgress) -> {
			double x = pos.getX() - cameraPos.x;
			double y = pos.getY() - cameraPos.y;
			double z = pos.getZ() - cameraPos.z;
			if (x * x + y * y + z * z > 1024.0) return;

			List<RenderType> destroyStages = ModRenderTypes.whiteDestroyStages();

			int i = Math.clamp((int)(progress / (double)totalProgress * destroyStages.size()), 0, destroyStages.size() - 1);

			PoseStack poseStack = event.getPoseStack();

			poseStack.pushPose();
			poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);

			VertexConsumer vc = new SheetedDecalTextureGenerator(mc.renderBuffers()
					.crumblingBufferSource()
					.getBuffer(destroyStages.get(i)), poseStack.last(), 1);

			mc.getBlockRenderer().renderBreakingTexture(mc.level.getBlockState(pos), pos, mc.level, poseStack,
					vc, mc.level.getModelData(pos));

			poseStack.popPose();
		});
	}

	private static void renderBeam(RenderLevelStageEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) return;

		PoseStack poseStack = event.getPoseStack();
		Vec3 cameraPos = event.getCamera().getPosition();
		float partialTicks = event.getPartialTick().getGameTimeDeltaPartialTick(false);

		poseStack.pushPose();
		poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

		for (AbstractClientPlayer player : mc.level.players()) {
			var beamStart = playerBeamStarts.get(player.getUUID());
			if (beamStart == null || !beamStart.isFinite()) continue;

			drawBeam(player, poseStack, beamStart, cameraPos, partialTicks);

			beamStart.set(Float.NaN);
		}

		poseStack.popPose();
	}

	private static void drawBeam(AbstractClientPlayer player, PoseStack poseStack, Vector3f beamStart,
	                             Vec3 cameraPos, float partialTicks) {
		if (player.isDeadOrDying() || !player.isUsingItem()) return;

		ItemStack useItem = player.getUseItem();
		if (!(useItem.getItem() instanceof BeamSource beamSource)) return;
		if (!beamSource.canProduceBeam(player, useItem, player.getUsedItemHand())) return;

		Vec3 start = player.getEyePosition();
		Vec3 end = start.add(player.getLookAngle().scale(AncientLightWandItem.DISTANCE));
		BlockHitResult hitResult = AncientLightWandItem.trace(player, start, end);

		if (hitResult.getType() == HitResult.Type.BLOCK) end = hitResult.getLocation();

		_beamEnd.set(end.x, end.y, end.z);

		Minecraft mc = Minecraft.getInstance();
		MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
		if (player == mc.getCameraEntity() && mc.options.getCameraType().isFirstPerson()) {
			// for 1st person perspective the start position is screen world coordination
			var vec = new Matrix4f()
					.mul(RenderSystem.getProjectionMatrix())
					.mul(RenderSystem.getModelViewMatrix())
					.mul(poseStack.last().pose())
					.unproject(beamStart,
							new int[]{-1, -1, 2, 2},
							new Vector3f());

			drawBeam(poseStack, bufferSource, player, useItem, vec, _beamEnd, partialTicks, beamSource, true);
		} else {
			// for 3rd person perspective the start position is in world coordination
			beamStart.add((float)cameraPos.x, (float)cameraPos.y, (float)cameraPos.z);
			drawBeam(poseStack, bufferSource, player, useItem, beamStart, _beamEnd, partialTicks, beamSource, false);
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
	                            AbstractClientPlayer player, ItemStack stack, Vector3f start, Vector3f end,
	                            float partialTick, BeamSource beamSource, boolean firstPersonPerspective) {
		Vector3f vec = new Vector3f(start).sub(end);
		Quaternionf angle = new Vector3f(0, 0, -1).rotationTo(vec, new Quaternionf());

		poseStack.pushPose();
		poseStack.translate(start.x, start.y, start.z);
		poseStack.mulPose(angle);

		float diameter = beamSource.beamDiameter(player, stack, firstPersonPerspective);
		poseStack.scale(diameter, diameter, 1);

		int ticksUsingItem = player.getTicksUsingItem();
		poseStack.mulPose(Axis.ZP.rotationDegrees(beamSource.beamRotationDegrees(player, stack, ticksUsingItem, firstPersonPerspective, partialTick)));

		int color = beamSource.beamColor(player, stack, firstPersonPerspective);
		RenderShapes.untexturedZGradientBox(
				poseStack,
				bufferSource.getBuffer(ModRenderTypes.BEAM),
				-.5f, -.5f, 0,
				.5f, .5f, vec.length() + .5f,
				color, color);

		poseStack.popPose();
	}
}
