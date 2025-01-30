package gurumirum.magialucis.contents.block.lux.sunlight.core;

import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.LuxNodeTypes;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxNode;
import gurumirum.magialucis.impl.luxnet.behavior.LuxNodeType;
import gurumirum.magialucis.impl.luxnet.behavior.LuxSpecialNodeBehavior;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class MoonlightCoreBehavior extends BaseSunlightCoreNodeBehavior implements LuxSpecialNodeBehavior {
	@Override
	public @NotNull LuxNodeType<?> type() {
		return LuxNodeTypes.MOONLIGHT_CORE;
	}

	@Override
	public @NotNull LuxStat stat() {
		return MoonlightCoreBlock.STAT;
	}

	@Override
	public void alterLux(@NotNull ServerLevel level, @NotNull LuxNet luxNet, @NotNull LuxNode node, @NotNull Vector3d incomingLux) {
		super.alterLux(level, luxNet, node, incomingLux);
		incomingLux.z -= incomingLux.x + incomingLux.y;
		incomingLux.x = incomingLux.y = 0;
	}
}
