package gurumirum.magialucis.client.render.prism;

import com.mojang.blaze3d.vertex.PoseStack;
import gurumirum.magialucis.client.render.RenderEffect;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public abstract class BlockPrismEffect<BE extends BlockEntity>
		extends RenderEffect.BlockEntityBound<BE>
		implements PrismEffect {
	public BlockPrismEffect(BE blockEntity) {
		super(blockEntity);
	}

	@Override public boolean shouldRender(@NotNull Frustum frustum) {
		return frustum.isVisible(new AABB(this.blockEntity.getBlockPos()));
	}

	@Override public void transform(@NotNull PoseStack poseStack) {
		poseStack.pushPose();
		BlockPos pos = this.blockEntity.getBlockPos();
		poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
	}

	@Override public void undoTransform(@NotNull PoseStack poseStack) {
		poseStack.popPose();
	}

	@Override public double getDistance(@NotNull Camera camera) {
		BlockPos pos = this.blockEntity.getBlockPos();
		return camera.getPosition().distanceToSqr(
				pos.getX() + 0.5,
				pos.getY() + 0.5,
				pos.getZ() + 0.5);
	}
}
