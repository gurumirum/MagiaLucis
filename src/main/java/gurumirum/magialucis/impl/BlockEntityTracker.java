package gurumirum.magialucis.impl;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.block.RegisteredBlockEntity;
import gurumirum.magialucis.contents.block.RegisteredBlockEntity.UnregisterContext;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.ChunkTicketLevelUpdatedEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = MagiaLucisMod.MODID)
public final class BlockEntityTracker {
	private BlockEntityTracker() {}

	private static final Map<ServerLevel, Long2ObjectMap<List<RegisteredBlockEntity>>> blockEntities =
			new Object2ObjectOpenHashMap<>();

	public static void register(RegisteredBlockEntity blockEntity) {
		if (!(blockEntity.getLevel() instanceof ServerLevel serverLevel)) return;
		BlockPos pos = blockEntity.getBlockPos();
		if (!serverLevel.isLoaded(pos)) return;

		blockEntities.computeIfAbsent(serverLevel, l -> new Long2ObjectOpenHashMap<>())
				.computeIfAbsent(ChunkPos.asLong(pos), l -> new ArrayList<>())
				.add(blockEntity);

		blockEntity.register(serverLevel);
	}

	private static @Nullable ServerLevel lastUnloadedChunkLevel;
	private static long lastUnloadedChunkPos;
	private static int lastUnloadedChunkTicketLevel;

	@SubscribeEvent
	public static void onChunkTicketLevelUpdated(ChunkTicketLevelUpdatedEvent event) {
		ServerLevel _lastUnloadedChunkLevel = lastUnloadedChunkLevel;
		long _lastUnloadedChunkPos = lastUnloadedChunkPos;
		int _lastUnloadedChunkTicketLevel = lastUnloadedChunkTicketLevel;

		lastUnloadedChunkLevel = null;
		lastUnloadedChunkPos = 0;
		lastUnloadedChunkTicketLevel = 0;

		if (_lastUnloadedChunkLevel == event.getLevel() && _lastUnloadedChunkPos == event.getChunkPos()) {
			onChunkTicketChanged(event.getLevel(), _lastUnloadedChunkPos, _lastUnloadedChunkTicketLevel, event.getNewTicketLevel());
			return;
		}

		// minecraft has this weird ping pong like call chain where they "unload" the chunk while demoting chunk levels
		// the actual chunk unload should be able to be handled by ChunkEvent.Unload so hopefully this won't bite my ass
		if (!ChunkLevel.isLoaded(event.getNewTicketLevel())) {
			lastUnloadedChunkLevel = event.getLevel();
			lastUnloadedChunkPos = event.getChunkPos();
			lastUnloadedChunkTicketLevel = event.getOldTicketLevel();
			return;
		}

		onChunkTicketChanged(event.getLevel(), event.getChunkPos(), event.getOldTicketLevel(), event.getNewTicketLevel());
	}

	private static void onChunkTicketChanged(ServerLevel level, long chunkPos, int oldTicket, int newTicket) {
		boolean register = ChunkLevel.isBlockTicking(newTicket);
		if (ChunkLevel.isBlockTicking(oldTicket) == register) return;

		var map = blockEntities.get(level);
		if (map == null) return;

		var list = map.get(chunkPos);
		if (list != null) {
			for (RegisteredBlockEntity be : list) {
				if (register) be.register(level);
				else be.unregister(level, UnregisterContext.CHUNK_PARTIALLY_UNLOADED);
			}
		}
	}

	@SuppressWarnings("SuspiciousMethodCalls") // shut the fuck up
	@SubscribeEvent
	public static void onChunkUnload(ChunkEvent.Unload event) {
		var map = blockEntities.get(event.getLevel());
		if (map == null) return;

		var list = map.remove(event.getChunk().getPos().toLong());
		if (list != null) {
			MagiaLucisMod.LOGGER.info("Unloading chunk {}, unregistering {} entries",
					event.getChunk().getPos(), list.size());

			for (RegisteredBlockEntity be : list) {
				be.unregister((ServerLevel)event.getLevel(), UnregisterContext.CHUNK_FULLY_UNLOADED);
			}
		}
	}

	@SuppressWarnings("SuspiciousMethodCalls") // SHUT THE FUCK UP
	@SubscribeEvent
	public static void onLevelUnload(LevelEvent.Unload event) {
		var map = blockEntities.remove(event.getLevel());
		if (map == null) return;

		removeAll((ServerLevel)event.getLevel(), map, UnregisterContext.CHUNK_FULLY_UNLOADED);

		if (event.getLevel() == lastUnloadedChunkLevel) {
			lastUnloadedChunkLevel = null;
		}
	}

	@SubscribeEvent
	public static void onServerStopping(ServerStoppingEvent event) {
		for (var e : blockEntities.entrySet()) {
			removeAll(e.getKey(), e.getValue(), UnregisterContext.SERVER_STOPPING);
		}
		blockEntities.clear();

		lastUnloadedChunkLevel = null;
	}

	private static void removeAll(ServerLevel serverLevel, Long2ObjectMap<List<RegisteredBlockEntity>> chunks,
	                              UnregisterContext context) {
		for (var e2 : chunks.long2ObjectEntrySet()) {
			for (RegisteredBlockEntity be : e2.getValue()) {
				be.unregister(serverLevel, context);
			}
		}
	}
}
