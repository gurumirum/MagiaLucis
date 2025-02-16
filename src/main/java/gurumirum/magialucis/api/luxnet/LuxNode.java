package gurumirum.magialucis.api.luxnet;

import gurumirum.magialucis.api.luxnet.behavior.LuxNodeBehavior;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LuxNode {
	int id();

	@Nullable LuxNodeInterface iface();
	@NotNull LuxNodeBehavior behavior();

	@Nullable BlockPos lastBlockPos();

	default boolean isLoaded() {
		return iface() != null;
	}

	default boolean isUnloaded() {
		return iface() == null;
	}
}
