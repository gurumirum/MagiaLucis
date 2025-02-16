package gurumirum.magialucis.api.luxnet;

import gurumirum.magialucis.api.capability.LinkDestination;
import gurumirum.magialucis.api.capability.MagiaLucisCaps;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface LinkDestinationSelector {
	LinkDestinationSelector DEFAULT = (level, context, hitResult) ->
			level.getCapability(MagiaLucisCaps.LINK_DESTINATION, hitResult.getBlockPos(), hitResult.getDirection());

	@Nullable LinkDestination chooseLinkDestination(@NotNull Level level, @Nullable ServerSideLinkContext context,
	                                                @NotNull BlockHitResult hitResult);
}
