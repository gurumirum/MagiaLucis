package gurumirum.magialucis.contents.block.lux.ambercore;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.LuxNodeTypes;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxNode;
import gurumirum.magialucis.impl.luxnet.behavior.LuxGeneratorNodeBehavior;
import gurumirum.magialucis.impl.luxnet.behavior.LuxNodeBehavior;
import gurumirum.magialucis.impl.luxnet.behavior.LuxNodeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class AmberCoreBehavior implements LuxNodeBehavior, LuxGeneratorNodeBehavior {
	public static final LuxStat STAT = LuxStat.simple(
			GemStats.AMBER.color(),
			0,
			10, 5, 0);

	private double power;

	public AmberCoreBehavior() {}

	@Override
	public @NotNull LuxNodeType<?> type() {
		return LuxNodeTypes.AMBER_CORE;
	}

	@Override
	public @NotNull LuxStat stat() {
		return STAT;
	}

	@Override
	public void generateLux(Level level, LuxNet luxNet, LuxNode node, Vector3d generatedLux) {
		if (node.isLoaded()) generatedLux.set(10, 5, 0).mul(this.power());
	}

	public double power() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}
}
