package gurumirum.magialucis.impl.luxnet.behavior;

import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxNode;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public interface LuxConsumerNodeBehavior extends LuxNodeBehavior {
	void consumeLux(@NotNull ServerLevel level, @NotNull LuxNet luxNet, @NotNull LuxNode node, @NotNull Vector3d receivedLux);
}
