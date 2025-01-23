package gurumirum.magialucis.impl.luxnet;

import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record LinkContext(@Nullable Level level,
                          @Nullable LuxNet luxNet,
                          @Nullable LuxNode sourceNode,
                          @Nullable LuxNodeInterface sourceInterface,
                          @Nullable Direction side,
                          @Nullable Vec3 location) {

	public LinkContext(@Nullable Level level,
	                   @Nullable LuxNet luxNet,
	                   @Nullable LuxNode sourceNode,
	                   @Nullable LuxNodeInterface sourceInterface,
	                   @Nullable BlockHitResult hitResult) {
		this(level, luxNet, sourceNode, sourceInterface,
				hitResult != null ? hitResult.getDirection() : null,
				hitResult != null ? hitResult.getLocation() : null);
	}

	public LinkContext(@Nullable Level level,
	                   @Nullable LuxNet luxNet,
	                   @Nullable LuxNode node,
	                   @Nullable Direction side,
	                   @Nullable Vec3 location) {
		this(level, luxNet, node, node != null ? node.iface() : null, side, location);
	}

	public LinkContext(@Nullable Level level,
	                   @Nullable LuxNet luxNet,
	                   @Nullable LuxNode node,
	                   @Nullable BlockHitResult hitResult) {
		this(level, luxNet, node, node != null ? node.iface() : null, hitResult);
	}

	public LinkContext(@Nullable Level level,
	                   @Nullable LuxNet luxNet,
	                   int nodeId,
	                   @Nullable BlockHitResult hitResult) {
		this(level, luxNet, luxNet != null ? luxNet.get(nodeId) : null, hitResult);
	}

	public LinkContext(@Nullable Level level, @Nullable LuxNodeInterface iface, @Nullable BlockHitResult hitResult) {
		this(level, null, null, iface, hitResult);
	}
}
