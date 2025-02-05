package gurumirum.magialucis.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CustomItemRender {
	void render(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext,
	            @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
	            int packedLight, int packedOverlay);
}
