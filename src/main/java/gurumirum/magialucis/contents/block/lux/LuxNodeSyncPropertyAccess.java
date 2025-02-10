package gurumirum.magialucis.contents.block.lux;

import gurumirum.magialucis.impl.luxnet.InWorldLinkState;
import gurumirum.magialucis.impl.luxnet.LinkInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector3d;

import java.util.Collection;

public interface LuxNodeSyncPropertyAccess {
	int luxNodeId();
	Vector3d luxFlow(Vector3d dest);
	@NotNull @UnmodifiableView Int2ObjectMap<LinkInfo> outboundLinks();
	@NotNull @UnmodifiableView Int2ObjectMap<LinkInfo> inboundLinks();
	@NotNull @UnmodifiableView Collection<InWorldLinkState> linkStates();
	int totalLinkWeight();
}
