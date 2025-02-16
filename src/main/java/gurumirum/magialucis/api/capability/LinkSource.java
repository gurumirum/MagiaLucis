package gurumirum.magialucis.api.capability;

import gurumirum.magialucis.api.luxnet.InWorldLinkState;
import gurumirum.magialucis.api.luxnet.LinkDestinationSelector;
import gurumirum.magialucis.api.luxnet.LuxNodeInterface;
import gurumirum.magialucis.utils.Orientation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LinkSource {
	double DEFAULT_LINK_DISTANCE = 15;

	int maxLinks();
	@Nullable Orientation getLink(int index);
	@Nullable InWorldLinkState getLinkState(int index);
	void setLink(int index, @Nullable Orientation orientation);

	@Nullable LinkDestinationSelector linkDestinationSelector();

	@NotNull Vec3 linkOrigin();

	default double linkDistance() {
		return DEFAULT_LINK_DISTANCE;
	}

	default @Nullable LuxNodeInterface clientSideInterface() {
		return this instanceof LuxNodeInterface iface ? iface : null;
	}

	default void unlinkAll() {
		for (int i = 0, maxLinks = maxLinks(); i < maxLinks; i++) setLink(i, null);
	}
}
