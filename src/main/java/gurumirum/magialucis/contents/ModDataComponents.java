package gurumirum.magialucis.contents;

import com.mojang.serialization.Codec;
import gurumirum.magialucis.contents.block.lux.relay.RelayItemData;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class ModDataComponents {
	private ModDataComponents() {}

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> FIELD_ID = register(
			"field_id", ResourceLocation.CODEC, ResourceLocation.STREAM_CODEC);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<RelayItemData>> RELAY_ITEM = register(
			"relay_item",
			ItemStack.CODEC.xmap(RelayItemData::new, RelayItemData::stack),
			ItemStack.STREAM_CODEC.map(RelayItemData::new, RelayItemData::stack));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> LINK_SOURCE = register(
			"link_source", GlobalPos.CODEC, GlobalPos.STREAM_CODEC);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> LUX_CHARGE = register(
			"lux_charge", Codec.LONG, ByteBufCodecs.VAR_LONG);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Byte>> WAND_BELT_SELECTED_INDEX = register(
			"wand_belt_selected_index", Codec.BYTE, ByteBufCodecs.BYTE);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> PORTAL_UUID = register(
			"portal_uuid", UUIDUtil.CODEC, UUIDUtil.STREAM_CODEC);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> SHIELD_CHARGE = register(
			"shield_charge", Codec.FLOAT, ByteBufCodecs.FLOAT);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DEPLETED = register(
			"depleted", Codec.BOOL, ByteBufCodecs.BOOL);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SPEED_BOOST_CHARGE = register(
			"speed_boost_charge", Codec.INT);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> POWER = register(
			"power", Codec.INT);

	public static void init() {}

	private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(
			@NotNull String name, @NotNull Codec<T> codec) {
		return register(name, codec, null);
	}

	private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(
			@NotNull String name, @NotNull Codec<T> codec,
			@Nullable StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
		return Contents.DATA_COMPONENTS.register(name, () -> {
			DataComponentType.Builder<T> builder = DataComponentType.builder();
			builder.persistent(codec);
			if (streamCodec != null) builder.networkSynchronized(streamCodec);
			return builder.build();
		});
	}
}
