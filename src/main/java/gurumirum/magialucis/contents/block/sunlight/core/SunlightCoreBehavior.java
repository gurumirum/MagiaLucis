package gurumirum.magialucis.contents.block.sunlight.core;

import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.LuxNodeTypes;
import gurumirum.magialucis.impl.luxnet.behavior.LuxNodeType;
import org.jetbrains.annotations.NotNull;

public class SunlightCoreBehavior extends BaseSunlightCoreNodeBehavior {
	public SunlightCoreBehavior() {}

	@Override
	public @NotNull LuxNodeType<?> type() {
		return LuxNodeTypes.SUNLIGHT_CORE;
	}

	@Override
	public @NotNull LuxStat stat() {
		return SunlightCoreBlock.STAT;
	}
}
