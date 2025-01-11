package gurumirum.magialucis.impl.luxnet;

import org.jetbrains.annotations.NotNull;

public interface ServerSideLinkContext {
	@NotNull LuxNet luxNet();
	@NotNull LuxNode luxNode();
}
