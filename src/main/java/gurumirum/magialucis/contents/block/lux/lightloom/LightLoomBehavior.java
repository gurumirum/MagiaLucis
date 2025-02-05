package gurumirum.magialucis.contents.block.lux.lightloom;

import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxNode;
import gurumirum.magialucis.impl.luxnet.behavior.LuxConsumerNodeBehavior;
import gurumirum.magialucis.impl.luxnet.behavior.LuxNodeType;
import gurumirum.magialucis.utils.LuxSampler;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class LightLoomBehavior implements LuxConsumerNodeBehavior {
	public final LuxSampler luxInput = new LuxSampler(5);

	private final LightLoomType type;

	public LightLoomBehavior(LightLoomType type) {
		this.type = type;
	}

	@Override
	public @NotNull LuxNodeType<?> type() {
		return this.type.behaviorType();
	}

	@Override
	public @NotNull LuxStat stat() {
		return this.type.luxStat();
	}

	@Override
	public void consumeLux(@NotNull ServerLevel level, @NotNull LuxNet luxNet, @NotNull LuxNode node, @NotNull Vector3d receivedLux) {
		this.luxInput.nextSampler().set(receivedLux);
		receivedLux.zero();
	}
}
