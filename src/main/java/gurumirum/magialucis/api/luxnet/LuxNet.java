package gurumirum.magialucis.api.luxnet;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

public interface LuxNet {
	int NO_ID = 0;

	@NotNull @UnmodifiableView Int2ObjectMap<? extends LuxNode> nodes();

	@Nullable LuxNode get(int nodeId);

	boolean hasOutboundLink(int nodeId);
	boolean hasInboundLink(int nodeId);

	@Nullable OutboundLink outboundLinks(int nodeId);
	@Nullable InboundLink inboundLinks(int nodeId);

	@NotNull @UnmodifiableView IntSet nodesWithOutboundLink();
	@NotNull @UnmodifiableView IntSet nodesWithInboundLink();

	int register(@NotNull LuxNodeInterface iface, int existingId);
	void unregister(int nodeId);
	void unbindInterface(int nodeId);

	void queueLinkUpdate(int nodeId);
	void queueLuxFlowSync(int nodeId);
	void queueConnectionSync(int nodeId);

	void clear(@NotNull ClearMode clearMode);

	enum ClearMode {
		ALL,
		UNLOADED
	}
}
