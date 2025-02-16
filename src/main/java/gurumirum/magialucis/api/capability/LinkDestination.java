package gurumirum.magialucis.api.capability;

import gurumirum.magialucis.api.luxnet.LinkContext;
import gurumirum.magialucis.api.luxnet.LuxNet;
import org.jetbrains.annotations.NotNull;

public interface LinkDestination {
	@NotNull LinkTestResult linkWithSource(@NotNull LinkContext context);

	record LinkTestResult(boolean isLinkable, int nodeId) {
		private static final LinkTestResult FAIL = new LinkTestResult(false, LuxNet.NO_ID);

		public static LinkTestResult linkable(int nodeId) {
			return new LinkTestResult(true, nodeId);
		}

		public static LinkTestResult reject() {
			return LinkTestResult.FAIL;
		}
	}
}
