package gurumirum.magialucis.impl.ancientlight;

import gurumirum.magialucis.net.msgs.SyncAncientLightProgressMsg;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AncientLightRecord {
	public static final StreamCodec<FriendlyByteBuf, AncientLightRecord> STREAM_CODEC =
			StreamCodec.of(AncientLightRecord::write, AncientLightRecord::read);

	private static void write(FriendlyByteBuf buffer, AncientLightRecord record) {
		var list = record.beam.values().stream()
				.flatMap(e -> e.object2LongEntrySet().stream())
				.toList();

		buffer.writeVarInt(list.size());
		for (var e : list) {
			buffer.writeBlockPos(e.getKey());
			int p = progress(e.getLongValue());
			int tp = totalProgress(e.getLongValue());
			buffer.writeVarInt(p);
			buffer.writeVarInt(tp);
		}
	}

	private static AncientLightRecord read(FriendlyByteBuf buffer) {
		AncientLightRecord record = new AncientLightRecord();

		for (int i = buffer.readVarInt(); i > 0; i--) {
			BlockPos pos = buffer.readBlockPos();
			int progress = buffer.readVarInt();
			int totalProgress = buffer.readVarInt();
			record.setProgress(pos, progress, totalProgress);
		}

		return record;
	}

	private final Long2ObjectMap<Object2LongMap<BlockPos>> beam = new Long2ObjectOpenHashMap<>();

	public boolean isEmpty() {
		return this.beam.isEmpty();
	}

	public int getProgress(BlockPos pos) {
		return progress(get(pos));
	}

	public int getTotalProgress(BlockPos pos) {
		return totalProgress(get(pos));
	}

	private long get(BlockPos pos) {
		var m = getChunkData(ChunkPos.asLong(pos));
		return m != null ? m.getLong(pos) : 0;
	}

	public void removeProgress(BlockPos pos) {
		long l = ChunkPos.asLong(pos);
		var m = getChunkData(l);
		if (m != null) {
			m.removeLong(pos);
			if (m.isEmpty()) this.beam.remove(l);
		}
	}

	public void setProgress(BlockPos pos, int progress, int totalProgress) {
		if (progress <= 0) {
			removeProgress(pos);
			return;
		}
		this.beam.computeIfAbsent(ChunkPos.asLong(pos), _l -> new Object2LongOpenHashMap<>())
				.put(pos.immutable(), pack(progress, totalProgress));
	}

	public boolean hasChunkData(ChunkPos chunkPos) {
		return hasChunkData(chunkPos.toLong());
	}

	public boolean hasChunkData(long chunkPos) {
		return this.beam.containsKey(chunkPos);
	}

	private @Nullable Object2LongMap<BlockPos> getChunkData(ChunkPos chunkPos) {
		return getChunkData(chunkPos.toLong());
	}

	private @Nullable Object2LongMap<BlockPos> getChunkData(long chunkPos) {
		return this.beam.get(chunkPos);
	}

	private void setChunkData(ChunkPos chunkPos, @Nullable Object2LongMap<BlockPos> contents) {
		setChunkData(chunkPos.toLong(), contents);
	}

	private void setChunkData(long chunkPos, @Nullable Object2LongMap<BlockPos> contents) {
		if (contents == null || contents.isEmpty()) {
			this.beam.remove(chunkPos);
		} else {
			var m = this.beam.get(chunkPos);
			if (m != null) m.clear();
			else this.beam.put(chunkPos, m = new Object2LongOpenHashMap<>());
			m.putAll(contents);
		}
	}

	public void applyDecay(int amount) {
		var it = this.beam.long2ObjectEntrySet().iterator();
		while (it.hasNext()) {
			var m = it.next().getValue();
			var it2 = m.object2LongEntrySet().iterator();
			while (it2.hasNext()) {
				var e = it2.next();
				var p = e.getLongValue();
				var progress = progress(p);
				var totalProgress = totalProgress(p);
				if (progress - amount <= 0) it2.remove();
				else e.setValue(pack(progress - amount, totalProgress));
			}
			if (m.isEmpty()) it.remove();
		}
	}

	public @Nullable SyncAncientLightProgressMsg createUpdatePacket(ChunkTrackingView chunkTrackingView,
	                                                                @Nullable AncientLightRecord lastSent) {
		LongSet updatedChunks = new LongOpenHashSet();
		AncientLightRecord r2 = new AncientLightRecord();

		chunkTrackingView.forEach(chunkPos -> {
			var m = getChunkData(chunkPos);
			if (m != null) {
				updatedChunks.add(chunkPos.toLong());
				r2.setChunkData(chunkPos, m);
			} else if (lastSent != null && lastSent.hasChunkData(chunkPos)) {
				updatedChunks.add(chunkPos.toLong());
			}
		});

		if (updatedChunks.isEmpty()) return null;

		return new SyncAncientLightProgressMsg(updatedChunks, this);
	}

	public @Nullable SyncAncientLightProgressMsg createUpdatePacket(long chunkPos) {
		var m = getChunkData(chunkPos);
		if (m == null) return null;

		AncientLightRecord r2 = new AncientLightRecord();
		r2.setChunkData(chunkPos, m);
		return new SyncAncientLightProgressMsg(LongSets.singleton(chunkPos), r2);
	}

	public void copyFrom(AncientLightRecord record) {
		this.beam.keySet().retainAll(record.beam.keySet());

		for (var e : record.beam.long2ObjectEntrySet()) {
			setChunkData(e.getLongKey(), e.getValue());
		}
	}

	public void applyPacket(LongSet chunks, AncientLightRecord record) {
		chunks.forEach(l -> setChunkData(l, record.getChunkData(l)));
	}

	public void forEachBlock(@NotNull ProgressConsumer consumer) {
		for (var e : this.beam.long2ObjectEntrySet()) {
			for (var e2 : e.getValue().object2LongEntrySet()) {
				BlockPos key = e2.getKey();
				long p = e2.getLongValue();
				consumer.accept(key, progress(p), totalProgress(p));
			}
		}
	}

	private static int progress(long l) {
		return (int)(l >> 32);
	}

	private static int totalProgress(long l) {
		return (int)l;
	}

	private static long pack(int progress, int totalProgress) {
		return ((long)progress << 32) | Integer.toUnsignedLong(totalProgress);
	}

	@FunctionalInterface
	public interface ProgressConsumer {
		void accept(@NotNull BlockPos pos, int progress, int totalProgress);
	}
}
