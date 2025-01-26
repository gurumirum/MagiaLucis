package gurumirum.magialucis.impl.ancientlight;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.net.msgs.SyncAncientLightProgressMsg;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@EventBusSubscriber(modid = MagiaLucisMod.MODID)
public final class AncientLightCrafting {
	private AncientLightCrafting() {}

	private static final Map<ServerLevel, ServerAncientLightManager> managers = new Object2ObjectOpenHashMap<>();

	private static @Nullable LocalAncientLightManager localManager;

	public static @Nullable ServerAncientLightManager tryGetManager(Level level) {
		return level instanceof ServerLevel serverLevel ? getManager(serverLevel) : null;
	}

	public static @NotNull ServerAncientLightManager getManager(ServerLevel level) {
		return managers.computeIfAbsent(level, l -> new ServerAncientLightManager());
	}

	public static @NotNull LocalAncientLightManager getLocalManager() {
		if (localManager == null) localManager = new LocalAncientLightManager();
		return localManager;
	}

	public static @Nullable LocalAncientLightManager tryGetLocalManager() {
		return localManager;
	}

	@SubscribeEvent
	public static void onServerStopping(ServerStoppingEvent event) {
		managers.clear();
	}

	@SuppressWarnings("SuspiciousMethodCalls") // wdym
	@SubscribeEvent
	public static void afterLevelTick(LevelTickEvent.Post event) {
		Level level = event.getLevel();
		if (level.isClientSide) {
			if (localManager != null) localManager.update(level);
		} else {
			ServerAncientLightManager updater = managers.get(level);
			if (updater != null) updater.update(level);
		}
	}

	@SuppressWarnings("SuspiciousMethodCalls") // wdym
	@SubscribeEvent
	public static void onLevelUnload(LevelEvent.Unload event) {
		managers.remove(event.getLevel());
	}

	@SubscribeEvent
	public static void onChunkSent(ChunkWatchEvent.Sent event) {
		ServerAncientLightManager updater = managers.get(event.getLevel());
		if (updater == null) return;
		AncientLightRecord record = updater.lastSyncedRecord();
		if (record == null) return;
		SyncAncientLightProgressMsg msg = record.createUpdatePacket(event.getChunk().getPos().toLong());
		if (msg != null) PacketDistributor.sendToPlayer(event.getPlayer(), msg);
	}
}
