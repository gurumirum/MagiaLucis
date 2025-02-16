package gurumirum.magialucis.api.luxnet;

import gurumirum.magialucis.api.luxnet.behavior.LuxNodeBehavior;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector3d;

public interface LuxNodeInterface {
	@NotNull LuxNodeBehavior updateNodeBehavior(@NotNull LuxNodeBehavior previous, boolean initial);

	void updateLink(LuxNet luxNet, LuxNetLinkCollector linkCollector);

	void syncLuxFlow(Vector3d amount);
	void syncConnection(OutboundLink outboundLinks, InboundLink inboundLinks);
	void syncLinkStatus(@NotNull @UnmodifiableView Int2ObjectMap<InWorldLinkState> linkIndexToState);

	@Nullable BlockPos nodeBlockPos();
}
