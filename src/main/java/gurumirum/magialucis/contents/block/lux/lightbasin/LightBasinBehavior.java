package gurumirum.magialucis.contents.block.lux.lightbasin;

import gurumirum.magialucis.api.capability.LuxStat;
import gurumirum.magialucis.api.luxnet.LuxNet;
import gurumirum.magialucis.api.luxnet.LuxNode;
import gurumirum.magialucis.api.luxnet.behavior.LuxConsumerNodeBehavior;
import gurumirum.magialucis.api.luxnet.behavior.LuxNodeType;
import gurumirum.magialucis.contents.LuxNodeTypes;
import gurumirum.magialucis.utils.LuxSampler;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class LightBasinBehavior implements LuxConsumerNodeBehavior {
	final LuxSampler luxInput = new LuxSampler(5);

	@Override
	public @NotNull LuxNodeType<?> type() {
		return LuxNodeTypes.LIGHT_BASIN;
	}

	@Override
	public @NotNull LuxStat stat() {
		return LightBasinBlock.STAT;
	}

	@Override
	public void consumeLux(@NotNull ServerLevel level, @NotNull LuxNet luxNet, @NotNull LuxNode node, @NotNull Vector3d receivedLux) {
		this.luxInput.nextSampler().set(receivedLux);
		receivedLux.zero();
	}
}
