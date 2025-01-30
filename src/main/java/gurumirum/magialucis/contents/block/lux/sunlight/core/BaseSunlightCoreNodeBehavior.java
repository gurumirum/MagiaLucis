package gurumirum.magialucis.contents.block.lux.sunlight.core;

import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxNode;
import gurumirum.magialucis.impl.luxnet.behavior.LuxSpecialNodeBehavior;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
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
	public void alterLux(@NotNull ServerLevel level, @NotNull LuxNet luxNet, @NotNull LuxNode node, @NotNull Vector3d incomingLux) {
		if (node.isLoaded()) incomingLux.mul(this.power);
		else incomingLux.zero();
	}
}
