package gurumirum.magialucis.api.luxnet;

import org.jetbrains.annotations.NotNull;

public interface OutboundLink extends LinkCollectionBase {
	@NotNull LuxNode src();
	int voidLinkWeight();
	int totalLinkWeight();
}
