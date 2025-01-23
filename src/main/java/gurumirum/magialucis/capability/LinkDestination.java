package gurumirum.magialucis.capability;

import gurumirum.magialucis.impl.luxnet.LinkContext;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import org.jetbrains.annotations.NotNull;

public interface LinkDestination {
	int NO_ID = LuxNet.NO_ID;

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
