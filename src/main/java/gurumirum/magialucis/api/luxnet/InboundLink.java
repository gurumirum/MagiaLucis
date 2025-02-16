package gurumirum.magialucis.api.luxnet;

import org.jetbrains.annotations.NotNull;

public interface InboundLink extends LinkCollectionBase {
	@NotNull LuxNode dst();
}
