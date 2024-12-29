package gurumirum.gemthing.net;

import gurumirum.gemthing.impl.InWorldBeamCraftingManager;
import gurumirum.gemthing.net.msgs.SetBeamCraftingInfoMsg;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerSideHandlers {
	public static void handleSetBeamCraftingInfo(SetBeamCraftingInfoMsg msg, IPayloadContext context) {
		InWorldBeamCraftingManager.setFocus(context.player(), msg.blockPos());
	}
}
