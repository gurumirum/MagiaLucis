package gurumirum.magialucis.impl.luxnet.behavior;

import gurumirum.magialucis.capability.LuxStat;
import org.jetbrains.annotations.NotNull;

public interface LuxNodeBehavior {
	static DefaultLuxNodeBehavior none() {
		return DefaultLuxNodeBehavior.INSTANCE;
	}

	@NotNull LuxNodeType<?> type();
	@NotNull LuxStat stat();
}
