package gurumirum.magialucis.net.msgs;

import gurumirum.magialucis.impl.ancientlight.AncientLightRecord;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static gurumirum.magialucis.api.MagiaLucisApi.id;

public record SyncAncientLightProgressMsg(
		LongSet chunks,
		AncientLightRecord record
) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SyncAncientLightProgressMsg> TYPE =
			new CustomPacketPayload.Type<>(id("sync_ancient_light_progress"));

	public static final StreamCodec<FriendlyByteBuf, SyncAncientLightProgressMsg> STREAM_CODEC = StreamCodec.of(
			SyncAncientLightProgressMsg::encode,
			SyncAncientLightProgressMsg::decode
	);

	private static void encode(FriendlyByteBuf buffer, SyncAncientLightProgressMsg msg) {
		buffer.writeVarInt(msg.chunks.size());
		msg.chunks.forEach(buffer::writeLong);
		AncientLightRecord.STREAM_CODEC.encode(buffer, msg.record);
	}

	private static SyncAncientLightProgressMsg decode(FriendlyByteBuf buffer) {
		LongSet chunks = new LongOpenHashSet();
		for (int i = buffer.readVarInt(); i > 0; i--) {
			chunks.add(buffer.readLong());
		}

		return new SyncAncientLightProgressMsg(chunks, AncientLightRecord.STREAM_CODEC.decode(buffer));
	}

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
