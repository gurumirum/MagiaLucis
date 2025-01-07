package gurumirum.gemthing.impl;

import gurumirum.gemthing.capability.LuxStat;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector3d;

import java.util.Map;

public interface LuxNodeInterface {
	@Nullable LuxStat calculateNodeStat(LuxNet luxNet);
	void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector);

	void syncLuxFlow(Vector3d amount);
	void syncNodeStats(byte color, double minLuxThreshold, double rMaxTransfer, double gMaxTransfer, double bMaxTransfer);
	void syncConnection(@NotNull @UnmodifiableView Map<LuxNode, @Nullable InWorldLinkInfo> outboundLinks,
	                    @NotNull @UnmodifiableView Map<LuxNode, @Nullable InWorldLinkInfo> inboundLinks);
	void syncLinkStatus(@NotNull @UnmodifiableView Int2ObjectMap<InWorldLinkState> linkIndexToState);
}
