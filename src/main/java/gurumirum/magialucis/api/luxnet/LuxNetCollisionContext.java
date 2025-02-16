package gurumirum.magialucis.api.luxnet;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public interface LuxNetCollisionContext extends CollisionContext {
	LuxNetCollisionContext EMPTY = new EntityContext(
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

	static boolean is(@Nullable CollisionContext context) {
		return context instanceof LuxNetCollisionContext;
	}

	class EntityContext extends EntityCollisionContext implements LuxNetCollisionContext {
		protected EntityContext(boolean descending, double entityBottom, ItemStack heldItem,
		                        Predicate<FluidState> canStandOnFluid, @Nullable Entity entity) {
			super(descending, entityBottom, heldItem, canStandOnFluid, entity);
		}
	}
}
