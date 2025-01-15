package gurumirum.magialucis.net;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.net.msgs.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = MagiaLucisMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class Net {
	@SubscribeEvent
	public static void register(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar("1").executesOn(HandlerThread.MAIN);

		registrar.commonToServer(
				SetBeamCraftingInfoMsg.TYPE,
				SetBeamCraftingInfoMsg.STREAM_CODEC,
				ServerSideHandlers::handleSetBeamCraftingInfo);

		registrar.commonToServer(
				SetWandBeltSelectedIndexMsg.TYPE,
				SetWandBeltSelectedIndexMsg.STREAM_CODEC,
				ServerSideHandlers::handleSetWandBeltSelectedIndex);

		registrar.commonToServer(
				SwapWandBeltItemMsg.TYPE,
				SwapWandBeltItemMsg.STREAM_CODEC,
				ServerSideHandlers::handleSwapWandBeltItem);

		registrar.commonToServer(
				SetLinkMsg.TYPE,
				SetLinkMsg.STREAM_CODEC,
				ServerSideHandlers::handleSetLink);

		registrar.commonToClient(
				SyncAncientLightProgressMsg.TYPE,
				SyncAncientLightProgressMsg.STREAM_CODEC,
				ClientSideHandlers::handleSyncAncientLightProgress);
	}
}
