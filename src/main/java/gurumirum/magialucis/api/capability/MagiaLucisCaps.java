package gurumirum.magialucis.api.capability;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;

import static gurumirum.magialucis.api.MagiaLucisApi.id;

public final class MagiaLucisCaps {
	private MagiaLucisCaps() {}

	public static final ItemCapability<LuxAcceptor, Void> LUX_ACCEPTOR = ItemCapability.createVoid(id("lux_acceptor"), LuxAcceptor.class);
	public static final ItemCapability<LuxContainerStat, Void> LUX_CONTAINER_STAT = ItemCapability.createVoid(id("lux_container_stat"), LuxContainerStat.class);

	public static final BlockCapability<LinkSource, Void> LINK_SOURCE = BlockCapability.createVoid(id("linkable"), LinkSource.class);
	public static final BlockCapability<LinkDestination, Direction> LINK_DESTINATION = BlockCapability.createSided(id("link_destination"), LinkDestination.class);
	public static final BlockCapability<DirectLinkDestination, Direction> DIRECT_LINK_DESTINATION = BlockCapability.createSided(id("direct_link_destination"), DirectLinkDestination.class);
}
