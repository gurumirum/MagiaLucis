package gurumirum.magialucis.net;

import gurumirum.magialucis.impl.ancientlight.AncientLightCrafting;
import gurumirum.magialucis.impl.ancientlight.AncientLightRecord;
import gurumirum.magialucis.impl.ancientlight.LocalAncientLightManager;
import gurumirum.magialucis.net.msgs.SyncAncientLightProgressMsg;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientSideHandlers {
	private ClientSideHandlers() {}

	public static void handleSyncAncientLightProgress(SyncAncientLightProgressMsg msg, IPayloadContext context) {
		LocalAncientLightManager localManager = AncientLightCrafting.getLocalManager();

		AncientLightRecord record = localManager.getOrCreateRecord();
		record.applyPacket(msg.chunks(), msg.record());
	}
}
