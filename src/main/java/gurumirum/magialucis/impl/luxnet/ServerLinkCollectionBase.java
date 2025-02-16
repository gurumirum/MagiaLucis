package gurumirum.magialucis.impl.luxnet;

import gurumirum.magialucis.api.luxnet.LinkCollectionBase;
import gurumirum.magialucis.api.luxnet.LinkInfo;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Map;

public abstract sealed class ServerLinkCollectionBase implements LinkCollectionBase permits ServerOutboundLink, ServerInboundLink {
	protected final Map<ServerLuxNode, LinkInfo> links = new Object2ObjectOpenHashMap<>();

	@Override
	public @NotNull @UnmodifiableView Map<ServerLuxNode, LinkInfo> links() {
		return Collections.unmodifiableMap(this.links);
	}
}
