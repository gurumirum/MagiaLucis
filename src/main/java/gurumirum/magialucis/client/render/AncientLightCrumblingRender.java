package gurumirum.magialucis.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gurumirum.magialucis.impl.ancientlight.AncientLightCrafting;
import gurumirum.magialucis.impl.ancientlight.AncientLightRecord;
import gurumirum.magialucis.impl.ancientlight.LocalAncientLightManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.List;

public final class AncientLightCrumblingRender {
	private AncientLightCrumblingRender() {}

	public static void render(RenderLevelStageEvent event) {
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
}
