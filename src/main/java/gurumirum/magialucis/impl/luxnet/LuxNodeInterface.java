package gurumirum.magialucis.impl.luxnet;

import gurumirum.magialucis.impl.luxnet.behavior.LuxNodeBehavior;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector3d;

import java.util.Map;

public interface LuxNodeInterface {
	@NotNull LuxNodeBehavior updateNodeBehavior(@NotNull LuxNodeBehavior previous, boolean initial);

	void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector);

	void syncLuxFlow(Vector3d amount);
	void syncConnection(@NotNull @UnmodifiableView Map<LuxNode, @Nullable InWorldLinkInfo> outboundLinks,
	                    @NotNull @UnmodifiableView Map<LuxNode, @Nullable InWorldLinkInfo> inboundLinks);
	void syncLinkStatus(@NotNull @UnmodifiableView Int2ObjectMap<InWorldLinkState> linkIndexToState);
}
