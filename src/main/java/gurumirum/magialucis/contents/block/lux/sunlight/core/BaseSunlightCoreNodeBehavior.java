package gurumirum.magialucis.contents.block.lux.sunlight.core;

import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxNode;
import gurumirum.magialucis.impl.luxnet.behavior.LuxSpecialNodeBehavior;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;

public abstract class BaseSunlightCoreNodeBehavior implements LuxSpecialNodeBehavior {
	private double power;

	public double power() {
		return this.power;
	}

	public void setPower(double power) {
		this.power = power;
	}

	@Override
	public void alterLux(Level level, LuxNet luxNet, LuxNode node, Vector3d incomingLux) {
		if (node.isLoaded()) incomingLux.mul(this.power);
		else incomingLux.zero();
	}
}
