package gurumirum.magialucis.impl.luxnet.behavior;

import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxNode;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

public interface LuxNodeBehavior {
	static DefaultLuxNodeBehavior none() {
		return DefaultLuxNodeBehavior.INSTANCE;
	}

	@NotNull LuxNodeType<?> type();
	@NotNull LuxStat stat();

	default void onBind(@NotNull ServerLevel level, @NotNull LuxNet luxNet, @NotNull LuxNode node) {}
	default void onUnbind(@NotNull ServerLevel level, @NotNull LuxNet luxNet, @NotNull LuxNode node) {}
}
