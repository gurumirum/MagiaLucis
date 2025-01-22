package gurumirum.magialucis.impl.luxnet.behavior;

import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxNode;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;

public interface LuxConsumerNodeBehavior extends LuxNodeBehavior {
	void consumeLux(Level level, LuxNet luxNet, LuxNode node, Vector3d receivedLux);
}
