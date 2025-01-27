package gurumirum.magialucis.client.render.prism;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gurumirum.magialucis.client.render.RenderEffect;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.culling.Frustum;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public interface PrismEffect extends RenderEffect {
	double NO_DISTANCE = Double.NaN;

	@OnlyIn(Dist.CLIENT)
	boolean shouldRender(@NotNull Frustum frustum);

	@OnlyIn(Dist.CLIENT)
	void draw(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, boolean reverseCull);

	@OnlyIn(Dist.CLIENT)
	void transform(@NotNull PoseStack poseStack);

	@OnlyIn(Dist.CLIENT)
	void undoTransform(@NotNull PoseStack poseStack);

	double getDistance(@NotNull Camera camera);
}
