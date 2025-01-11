package gurumirum.magialucis.impl.luxnet;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public record LinkContext(@Nullable Level level,
                          @Nullable LuxNet luxNet,
                          @Nullable LuxNode sourceNode,
                          @Nullable LuxNodeInterface sourceInterface,
                          @Nullable BlockHitResult hitResult) {
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
