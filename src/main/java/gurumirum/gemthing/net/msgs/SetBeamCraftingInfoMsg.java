package gurumirum.gemthing.net.msgs;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static gurumirum.gemthing.GemthingMod.id;

public record SetBeamCraftingInfoMsg(
		@Nullable BlockPos blockPos
) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SetBeamCraftingInfoMsg> TYPE =
			new CustomPacketPayload.Type<>(id("set_beam_crafting_info"));

	public static final StreamCodec<FriendlyByteBuf, SetBeamCraftingInfoMsg> STREAM_CODEC = StreamCodec.of(
			SetBeamCraftingInfoMsg::encode,
			SetBeamCraftingInfoMsg::decode
	);

	private static void encode(FriendlyByteBuf buffer, SetBeamCraftingInfoMsg msg) {
		buffer.writeBoolean(msg.blockPos != null);
		if (msg.blockPos != null) buffer.writeBlockPos(msg.blockPos);
	}

	private static SetBeamCraftingInfoMsg decode(FriendlyByteBuf buffer) {
		return new SetBeamCraftingInfoMsg(buffer.readBoolean() ? buffer.readBlockPos() : null);
	}

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
