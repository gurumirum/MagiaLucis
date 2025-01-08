package gurumirum.gemthing.contents.block.lux;

import gurumirum.gemthing.impl.InWorldLinkInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector3d;

public interface RelaySyncPropertyAccess {
	int luxNodeId();
	Vector3d luxFlow(Vector3d dest);
	@NotNull @UnmodifiableView Int2ObjectMap<@Nullable InWorldLinkInfo> outboundLinks();
	@NotNull @UnmodifiableView Int2ObjectMap<@Nullable InWorldLinkInfo> inboundLinks();

	byte color();
	double minLuxThreshold();
	double rMaxTransfer();
	double gMaxTransfer();
	double bMaxTransfer();
}
