package gurumirum.magialucis.client.render.generic;

import gurumirum.magialucis.client.render.RenderEffectManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public final class GenericRenderEffectRender {
	private GenericRenderEffectRender() {}

	public static void render(RenderLevelStageEvent event, RenderEffectManager<GenericRenderEffect> effects) {
		float partialTicks = 0;
		boolean setup = false;

		for (GenericRenderEffect effect : effects) {
			if (!effect.shouldRender(event.getFrustum())) continue;

			if (!setup) {
				setup = true;
				Vec3 cameraPos = event.getCamera().getPosition();

				event.getPoseStack().pushPose();
				event.getPoseStack().translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

				partialTicks = event.getPartialTick().getGameTimeDeltaPartialTick(false);
			}

			effect.draw(event.getPoseStack(), Minecraft.getInstance().renderBuffers().bufferSource(), partialTicks);
		}

		if (setup) {
			event.getPoseStack().popPose();
			// Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
		}
	}
}
