package gurumirum.magialucis.contents.block.lux.ambercore;

import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.LuxNodeTypes;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxNode;
import gurumirum.magialucis.impl.luxnet.behavior.LuxGeneratorNodeBehavior;
import gurumirum.magialucis.impl.luxnet.behavior.LuxNodeBehavior;
import gurumirum.magialucis.impl.luxnet.behavior.LuxNodeType;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class AmberCoreBehavior implements LuxNodeBehavior, LuxGeneratorNodeBehavior {
	private double power;

	public AmberCoreBehavior() {}

	@Override
	public @NotNull LuxNodeType<?> type() {
		return LuxNodeTypes.AMBER_CORE;
	}

	@Override
	public @NotNull LuxStat stat() {
		return AmberCoreBlock.STAT;
	}

	@Override
	public void generateLux(@NotNull ServerLevel level, @NotNull LuxNet luxNet, @NotNull LuxNode node, @NotNull Vector3d generatedLux) {
		if (node.isLoaded()) generatedLux.set(10, 5, 0).mul(this.power());
	}

	public double power() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}
}
