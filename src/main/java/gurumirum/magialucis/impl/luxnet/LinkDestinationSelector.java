package gurumirum.magialucis.impl.luxnet;

import gurumirum.magialucis.capability.LuxNetLinkDestination;
import gurumirum.magialucis.capability.ModCapabilities;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface LinkDestinationSelector {
	LinkDestinationSelector DEFAULT = (@NotNull Level level, int sourceNodeId, @NotNull BlockHitResult hitResult) -> {
		LuxNetLinkDestination dest = level.getCapability(ModCapabilities.LUX_NET_LINK_DESTINATION, hitResult.getBlockPos(), hitResult.getDirection());
		return dest != null ? dest.getLinkDestinationId(sourceNodeId, hitResult) : LuxNet.NO_ID;
	};

	int getLinkDestination(@NotNull Level level, int sourceNodeId, @NotNull BlockHitResult hitResult);
}
