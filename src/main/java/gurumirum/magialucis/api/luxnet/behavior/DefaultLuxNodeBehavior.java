package gurumirum.magialucis.api.luxnet.behavior;

import gurumirum.magialucis.api.capability.LuxStat;
import gurumirum.magialucis.contents.LuxNodeTypes;
import org.jetbrains.annotations.NotNull;

public final class DefaultLuxNodeBehavior implements LuxNodeBehavior {
	public static final DefaultLuxNodeBehavior INSTANCE = new DefaultLuxNodeBehavior();

	private DefaultLuxNodeBehavior() {}

	@Override
	public @NotNull LuxNodeType<?> type() {
		return LuxNodeTypes.DEFAULT;
	}

	@Override
	public @NotNull LuxStat stat() {
		return LuxStat.NULL;
	}
}
