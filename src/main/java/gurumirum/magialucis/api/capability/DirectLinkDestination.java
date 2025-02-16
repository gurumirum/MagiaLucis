package gurumirum.magialucis.api.capability;

import gurumirum.magialucis.api.luxnet.LinkContext;
import org.jetbrains.annotations.NotNull;

public interface DirectLinkDestination {
	@NotNull LinkDestination.LinkTestResult directLinkWithSource(@NotNull LinkContext context);
}
