package gurumirum.magialucis.impl.luxnet;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public interface LuxNetCollisionContext extends CollisionContext {
	LuxNetCollisionContext EMPTY = new EntityLuxNetCollisionContext(
			false,
			-Double.MAX_VALUE,
			ItemStack.EMPTY,
			fluidState -> false,
			null) {
		@Override
		public boolean isAbove(@NotNull VoxelShape shape, @NotNull BlockPos pos, boolean canAscend) {
			return canAscend;
		}
	};
}
