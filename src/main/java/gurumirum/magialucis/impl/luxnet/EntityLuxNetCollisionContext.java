package gurumirum.magialucis.impl.luxnet;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class EntityLuxNetCollisionContext extends EntityCollisionContext implements LuxNetCollisionContext {
	protected EntityLuxNetCollisionContext(boolean descending, double entityBottom, ItemStack heldItem,
	                                       Predicate<FluidState> canStandOnFluid, @Nullable Entity entity) {
		super(descending, entityBottom, heldItem, canStandOnFluid, entity);
	}
}
