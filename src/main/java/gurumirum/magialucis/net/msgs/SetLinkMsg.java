package gurumirum.magialucis.net.msgs;

import gurumirum.magialucis.api.Orientation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.Nullable;

import static gurumirum.magialucis.api.MagiaLucisApi.id;

public record SetLinkMsg(
		BlockPos pos,
		int index,
		@Nullable Orientation orientation
) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SetLinkMsg> TYPE =
			new CustomPacketPayload.Type<>(id("set_link"));

	public static final StreamCodec<FriendlyByteBuf, SetLinkMsg> STREAM_CODEC = StreamCodec.of(
			SetLinkMsg::encode,
			SetLinkMsg::decode
	);

	private static void encode(FriendlyByteBuf buffer, SetLinkMsg msg) {
		buffer.writeBlockPos(msg.pos);
		buffer.writeVarInt(msg.index);
		buffer.writeBoolean(msg.orientation != null);
		if (msg.orientation != null) buffer.writeLong(msg.orientation.packageToLong());
	}

	private static SetLinkMsg decode(FriendlyByteBuf buffer) {
		return new SetLinkMsg(buffer.readBlockPos(),
				buffer.readVarInt(),
				buffer.readBoolean() ? Orientation.fromLong(buffer.readLong()) : null);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
