package gurumirum.magialucis.contents.block.lux;

import gurumirum.magialucis.impl.luxnet.InWorldLinkInfo;
import gurumirum.magialucis.impl.luxnet.InWorldLinkState;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector3d;

import java.util.Collection;

public interface LuxNodeSyncPropertyAccess {
	int luxNodeId();
	Vector3d luxFlow(Vector3d dest);
	@NotNull @UnmodifiableView Int2ObjectMap<@Nullable InWorldLinkInfo> outboundLinks();
	@NotNull @UnmodifiableView Int2ObjectMap<@Nullable InWorldLinkInfo> inboundLinks();
	@NotNull @UnmodifiableView Collection<InWorldLinkState> linkStates();

	byte color();
	double minLuxThreshold();
	double rMaxTransfer();
	double gMaxTransfer();
	double bMaxTransfer();
}
