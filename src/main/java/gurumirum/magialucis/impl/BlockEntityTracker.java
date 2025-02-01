package gurumirum.magialucis.impl;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.block.RegisteredBlockEntity;
import gurumirum.magialucis.contents.block.RegisteredBlockEntity.UnregisterContext;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = MagiaLucisMod.MODID)
public final class BlockEntityTracker {
	private static final Map<ServerLevel, BlockEntityTracker> trackers = new Object2ObjectOpenHashMap<>();

	public static void register(RegisteredBlockEntity blockEntity) {
		if (!(blockEntity.getLevel() instanceof ServerLevel serverLevel)) return;
		trackers.computeIfAbsent(serverLevel, BlockEntityTracker::new).add(blockEntity);
	}

	@SubscribeEvent
	public static void onChunkTicketLevelUpdated(ChunkTicketLevelUpdatedEvent event) {
		BlockEntityTracker tracker = trackers.get(event.getLevel());
		if (tracker != null) {
			tracker.onChunkTicketLevelUpdated(event.getChunkPos(), event.getOldTicketLevel(), event.getNewTicketLevel());
		}
	}

	@SuppressWarnings("SuspiciousMethodCalls") // shut the fuck up
	@SubscribeEvent
	public static void onChunkUnload(ChunkEvent.Unload event) {
		BlockEntityTracker tracker = trackers.get(event.getLevel());
		if (tracker != null) {
			tracker.onChunkUnload(event.getChunk().getPos().toLong());
		}
	}

	@SuppressWarnings("SuspiciousMethodCalls") // SHUT THE FUCK UP
	@SubscribeEvent
	public static void onLevelUnload(LevelEvent.Unload event) {
		BlockEntityTracker tracker = trackers.remove(event.getLevel());
		if (tracker != null) {
			tracker.removeAll(UnregisterContext.CHUNK_FULLY_UNLOADED);
		}
	}

	@SubscribeEvent
	public static void onServerStopping(ServerStoppingEvent event) {
		for (BlockEntityTracker tracker : trackers.values()) {
			tracker.removeAll(UnregisterContext.SERVER_STOPPING);
		}
		trackers.clear();
	}

	private final ServerLevel level;
	private final Long2ObjectMap<List<RegisteredBlockEntity>> blockEntities = new Long2ObjectOpenHashMap<>();
	private final Long2IntOpenHashMap chunkTicketDemotionCache = new Long2IntOpenHashMap();

	private BlockEntityTracker(ServerLevel level) {
		this.level = level;
		this.chunkTicketDemotionCache.defaultReturnValue(-1);
	}

	private void add(RegisteredBlockEntity blockEntity) {
		BlockPos pos = blockEntity.getBlockPos();

		this.blockEntities.computeIfAbsent(ChunkPos.asLong(pos), l -> new ArrayList<>())
				.add(blockEntity);

		if (level.isLoaded(pos)) {
			blockEntity.register(level);
		}
	}

	private void onChunkTicketLevelUpdated(long chunkPos, int oldTicket, int newTicket) {
		int prev = this.chunkTicketDemotionCache.remove(chunkPos);
		if (prev != -1) {
			applyTicketChange(chunkPos, prev, newTicket);
		}

		// minecraft has this weird ping pong like call chain where they "unload" the chunk while demoting chunk levels
		// the actual chunk unload should be able to be handled by ChunkEvent.Unload so hopefully this won't bite my ass
		if (!ChunkLevel.isLoaded(newTicket)) {
			this.chunkTicketDemotionCache.put(chunkPos, oldTicket);
		} else {
			applyTicketChange(chunkPos, oldTicket, newTicket);
		}
	}

	private void applyTicketChange(long chunkPos, int oldTicket, int newTicket) {
		boolean register = ChunkLevel.isBlockTicking(newTicket);
		if (ChunkLevel.isBlockTicking(oldTicket) == register) return;

		var list = this.blockEntities.get(chunkPos);
		if (list == null) return;

		for (RegisteredBlockEntity be : list) {
			if (register) be.register(this.level);
			else be.unregister(this.level, UnregisterContext.CHUNK_PARTIALLY_UNLOADED);
		}
	}

	private void onChunkUnload(long chunkPos) {
		var list = this.blockEntities.remove(chunkPos);
		if (list == null) return;

		for (RegisteredBlockEntity be : list) {
			be.unregister(this.level, UnregisterContext.CHUNK_FULLY_UNLOADED);
		}

		this.chunkTicketDemotionCache.remove(chunkPos);
	}

	private void removeAll(UnregisterContext context) {
		for (var e2 : this.blockEntities.long2ObjectEntrySet()) {
			for (RegisteredBlockEntity be : e2.getValue()) {
				be.unregister(this.level, context);
			}
		}
	}
}
