package gurumirum.gemthing.net.msgs;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static gurumirum.gemthing.GemthingMod.id;

public record SwapWandBeltItemMsg(
		int wandBeltIndex,
		int playerInventoryIndex
) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SwapWandBeltItemMsg> TYPE =
			new CustomPacketPayload.Type<>(id("swap_wand_belt_item"));

	public static final StreamCodec<FriendlyByteBuf, SwapWandBeltItemMsg> STREAM_CODEC = StreamCodec.of(
			SwapWandBeltItemMsg::encode,
			SwapWandBeltItemMsg::decode
	);

	private static void encode(FriendlyByteBuf buffer, SwapWandBeltItemMsg msg) {
		buffer.writeVarInt(msg.wandBeltIndex);
		buffer.writeVarInt(msg.playerInventoryIndex);
	}

	private static SwapWandBeltItemMsg decode(FriendlyByteBuf buffer) {
		return new SwapWandBeltItemMsg(buffer.readVarInt(), buffer.readVarInt());
	}

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
