package gurumirum.magialucis.impl.luxnet;

import gurumirum.magialucis.api.luxnet.OutboundLink;
import org.jetbrains.annotations.NotNull;

public final class ServerOutboundLink extends ServerLinkCollectionBase implements OutboundLink {
	private final ServerLuxNode src;
	private int voidLinkWeight;
	private int totalLinkWeight;

	public ServerOutboundLink(ServerLuxNode src) {
		this.src = src;
	}

	@Override
	public @NotNull ServerLuxNode src() {
		return this.src;
	}

	@Override
	public int voidLinkWeight() {
		return this.voidLinkWeight;
	}

	@Override
	public int totalLinkWeight() {
		return this.totalLinkWeight;
	}

	void setVoidLinkWeight(int newValue) {
		if (newValue < 0) throw new IllegalArgumentException("newValue < 0");

		if (this.voidLinkWeight != newValue) {
			int diff = newValue - this.voidLinkWeight;
			this.voidLinkWeight = newValue;
			this.totalLinkWeight += diff;
		}
	}

	void setTotalLinkWeight(int totalLinkWeight) {
		this.totalLinkWeight = totalLinkWeight;
	}
}
