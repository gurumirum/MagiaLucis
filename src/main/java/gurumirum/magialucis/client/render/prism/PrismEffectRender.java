package gurumirum.magialucis.client.render.prism;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import gurumirum.magialucis.client.render.ModRenderTypes;
import gurumirum.magialucis.client.render.RenderEffects;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class PrismEffectRender {
	private PrismEffectRender() {}

	private static final List<DistanceEffectPair> sortedEffects = new ArrayList<>();

	public static void render(RenderLevelStageEvent event) {
		PoseStack poseStack = event.getPoseStack();

		for (PrismEffect effect : RenderEffects.prism) {
			if (!effect.shouldRender(event.getFrustum())) continue;
			sortedEffects.add(new DistanceEffectPair(effect.getDistance(event.getCamera()), effect));
		}

		sortedEffects.sort(Comparator.reverseOrder());

		boolean setup = false;

		for (DistanceEffectPair pair : sortedEffects) {
			if (!setup) {
				setup = true;
				RenderSystem.setShader(ModRenderTypes::prismShader);
				RenderSystem.colorMask(true, true, true, true);
				RenderSystem.depthMask(true);
				RenderSystem.enableDepthTest();
				RenderSystem.enableBlend();
				RenderSystem.blendFuncSeparate(
						GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
						GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

				Vec3 cameraPos = event.getCamera().getPosition();

				poseStack.pushPose();
				poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
			}

			pair.effect.transform(poseStack);

			Tesselator t = Tesselator.getInstance();
			BufferBuilder bb = t.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR_NORMAL);

			pair.effect.draw(poseStack, bb, true);
			BufferUploader.drawWithShader(bb.buildOrThrow());

			RenderSystem.depthMask(false);

			bb = t.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR_NORMAL);

			pair.effect.draw(poseStack, bb, false);
			BufferUploader.drawWithShader(bb.buildOrThrow());

			RenderSystem.depthMask(true);

			pair.effect.undoTransform(poseStack);
		}

		if (setup) {
			poseStack.popPose();
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
		}

		sortedEffects.clear();
	}

	private record DistanceEffectPair(double distance, PrismEffect effect) implements Comparable<DistanceEffectPair> {
		@Override public int compareTo(@NotNull DistanceEffectPair other) {
			return Double.compare(this.distance, other.distance);
		}
	}
}
