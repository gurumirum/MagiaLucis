package gurumirum.magialucis.api.luxnet.behavior;

import gurumirum.magialucis.api.luxnet.LuxNet;
import gurumirum.magialucis.api.luxnet.LuxNode;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public interface LuxSpecialNodeBehavior extends LuxNodeBehavior {
	void alterIncomingLux(@NotNull ServerLevel level, @NotNull LuxNet luxNet, @NotNull LuxNode node, @NotNull Vector3d incomingLux);
}
