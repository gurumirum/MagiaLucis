package gurumirum.magialucis.impl.luxnet;

import gurumirum.magialucis.capability.LuxNetLinkDestination;
import gurumirum.magialucis.capability.ModCapabilities;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface LinkDestinationSelector {
	LinkDestinationSelector DEFAULT = (level, context, hitResult) ->
			level.getCapability(ModCapabilities.LUX_NET_LINK_DESTINATION, hitResult.getBlockPos(), hitResult.getDirection());

	@Nullable LuxNetLinkDestination chooseLinkDestination(@NotNull Level level, @Nullable ServerSideLinkContext context, @NotNull BlockHitResult hitResult);
}
