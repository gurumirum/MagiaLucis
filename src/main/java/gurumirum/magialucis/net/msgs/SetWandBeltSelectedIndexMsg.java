package gurumirum.magialucis.net.msgs;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static gurumirum.magialucis.api.MagiaLucisApi.id;

public record SetWandBeltSelectedIndexMsg(
		int selectedIndex
) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SetWandBeltSelectedIndexMsg> TYPE =
			new CustomPacketPayload.Type<>(id("set_wand_belt_selected_index"));

	public static final StreamCodec<FriendlyByteBuf, SetWandBeltSelectedIndexMsg> STREAM_CODEC = StreamCodec.of(
			SetWandBeltSelectedIndexMsg::encode,
			SetWandBeltSelectedIndexMsg::decode
	);

	private static void encode(FriendlyByteBuf buffer, SetWandBeltSelectedIndexMsg msg) {
		buffer.writeVarInt(msg.selectedIndex);
	}

	private static SetWandBeltSelectedIndexMsg decode(FriendlyByteBuf buffer) {
		return new SetWandBeltSelectedIndexMsg(buffer.readVarInt());
	}

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
