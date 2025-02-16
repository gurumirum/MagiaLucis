package gurumirum.magialucis.impl.luxnet;

import gurumirum.magialucis.api.luxnet.InboundLink;
import org.jetbrains.annotations.NotNull;

public final class ServerInboundLink extends ServerLinkCollectionBase implements InboundLink {
	private final ServerLuxNode dst;

	public ServerInboundLink(ServerLuxNode dst) {
		this.dst = dst;
	}

	@Override
	public @NotNull ServerLuxNode dst() {
		return dst;
	}
}
