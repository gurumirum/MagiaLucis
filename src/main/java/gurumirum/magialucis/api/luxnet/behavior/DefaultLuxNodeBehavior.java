package gurumirum.magialucis.api.luxnet.behavior;

import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.api.capability.LuxStat;
import org.jetbrains.annotations.NotNull;

public final class DefaultLuxNodeBehavior implements LuxNodeBehavior {
	public static final DefaultLuxNodeBehavior INSTANCE = new DefaultLuxNodeBehavior();

	public static final LuxNodeType<DefaultLuxNodeBehavior> NODE_TYPE = new LuxNodeType.Simple<>(
			MagiaLucisApi.id("default"),
			DefaultLuxNodeBehavior.class,
			INSTANCE);

	private DefaultLuxNodeBehavior() {}

	@Override
	public @NotNull LuxNodeType<?> type() {
		return NODE_TYPE;
	}

	@Override
	public @NotNull LuxStat stat() {
		return LuxStat.NULL;
	}
}
