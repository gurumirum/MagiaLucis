package gurumirum.gemthing.net;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.net.msgs.SetBeamCraftingInfoMsg;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = GemthingMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class Net {
	@SubscribeEvent
	public static void register(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar("1").executesOn(HandlerThread.MAIN);

		registrar.commonToServer(
				SetBeamCraftingInfoMsg.TYPE,
				SetBeamCraftingInfoMsg.STREAM_CODEC,
				ServerSideHandlers::handleSetBeamCraftingInfo);
	}
}
