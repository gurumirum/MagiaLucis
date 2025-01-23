package gurumirum.magialucis.capability;

import gurumirum.magialucis.impl.luxnet.LinkContext;
import org.jetbrains.annotations.NotNull;

public interface DirectLinkDestination {
	@NotNull LinkDestination.LinkTestResult directLinkWithSource(@NotNull LinkContext context);
}
