package gurumirum.magialucis.client.render.generic;

import gurumirum.magialucis.client.render.RenderEffect;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public abstract class BlockGenericRenderEffect<T extends BlockEntity>
		extends RenderEffect.BlockEntityBound<T>
		implements GenericRenderEffect {
	public BlockGenericRenderEffect(T blockEntity) {
		super(blockEntity);
	}

	@Override
	public boolean shouldRender(@NotNull Frustum frustum) {
		return frustum.isVisible(new AABB(this.blockEntity.getBlockPos()));
	}
}
