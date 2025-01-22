package gurumirum.magialucis.contents.block.lux.lightbasin;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.LuxNodeTypes;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxNode;
import gurumirum.magialucis.impl.luxnet.behavior.LuxConsumerNodeBehavior;
import gurumirum.magialucis.impl.luxnet.behavior.LuxNodeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class LightBasinBehavior implements LuxConsumerNodeBehavior {
	final Vector3d luxInput = new Vector3d();

	@Override
	public @NotNull LuxNodeType<?> type() {
		return LuxNodeTypes.LIGHT_BASIN;
	}

	@Override
	public @NotNull LuxStat stat() {
		return GemStats.BRIGHTSTONE;
	}

	@Override
	public void consumeLux(Level level, LuxNet luxNet, LuxNode node, Vector3d receivedLux) {
		this.luxInput.set(receivedLux);
		receivedLux.zero();
	}
}
