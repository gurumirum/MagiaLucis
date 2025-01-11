package gurumirum.magialucis.capability;

import gurumirum.magialucis.impl.luxnet.LuxNet;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public interface LuxNetLinkDestination {
	int NO_ID = LuxNet.NO_ID;

	int getLinkDestinationId(int sourceId, @Nullable BlockHitResult hitResult);
}
