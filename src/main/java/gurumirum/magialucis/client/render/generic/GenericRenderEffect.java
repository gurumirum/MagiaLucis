package gurumirum.magialucis.client.render.generic;

import com.mojang.blaze3d.vertex.PoseStack;
import gurumirum.magialucis.client.render.RenderEffect;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public interface GenericRenderEffect extends RenderEffect {
	@OnlyIn(Dist.CLIENT)
	boolean shouldRender(@NotNull Frustum frustum);

	@OnlyIn(Dist.CLIENT)
	void draw(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, float partialTicks);
}
