package gurumirum.magialucis.api.luxnet;

import gurumirum.magialucis.api.capability.DirectLinkDestination;
import gurumirum.magialucis.capability.ModCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface DirectLinkDestinationSelector {
	DirectLinkDestinationSelector DEFAULT = (level, context, pos, direction) ->
			level.getCapability(ModCapabilities.DIRECT_LINK_DESTINATION, pos, direction);

	@Nullable DirectLinkDestination chooseDirectLinkDestination(
			@NotNull Level level, @Nullable ServerSideLinkContext context,
			@NotNull BlockPos pos, @NotNull Direction direction);
}
