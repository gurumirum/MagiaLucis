package gurumirum.gemthing.impl;

import org.joml.Vector3d;

public interface LuxSourceNodeInterface extends LuxNodeInterface {
	void generateLux(Vector3d dest);
}
