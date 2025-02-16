package gurumirum.magialucis.api.capability;

import gurumirum.magialucis.api.luxnet.LinkContext;
import gurumirum.magialucis.impl.luxnet.ServerLuxNet;
import org.jetbrains.annotations.NotNull;

public interface LinkDestination {
	int NO_ID = ServerLuxNet.NO_ID;

	@NotNull LinkTestResult linkWithSource(@NotNull LinkContext context);

	record LinkTestResult(boolean isLinkable, int nodeId) {
		private static final LinkTestResult FAIL = new LinkTestResult(false, NO_ID);

		public static LinkTestResult linkable(int nodeId) {
			return new LinkTestResult(true, nodeId);
		}

		public static LinkTestResult reject() {
			return LinkTestResult.FAIL;
		}
	}
}
