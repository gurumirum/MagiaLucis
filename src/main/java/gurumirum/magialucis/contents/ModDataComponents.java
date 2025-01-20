package gurumirum.magialucis.contents;

import com.mojang.serialization.Codec;
import gurumirum.magialucis.contents.block.lux.relay.RelayItemData;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.UUID;

public final class ModDataComponents {
	private ModDataComponents() {}

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> FIELD_ID = Contents.DATA_COMPONENTS.register("field_id",
			() -> DataComponentType.<ResourceLocation>builder()
					.persistent(ResourceLocation.CODEC)
					.networkSynchronized(ResourceLocation.STREAM_CODEC)
					.build());

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<RelayItemData>> RELAY_ITEM = Contents.DATA_COMPONENTS.register("relay_item",
			() -> DataComponentType.<RelayItemData>builder()
					.persistent(ItemStack.CODEC.xmap(RelayItemData::new, RelayItemData::stack))
					.networkSynchronized(ItemStack.STREAM_CODEC.map(RelayItemData::new, RelayItemData::stack))
					.build());

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> LINK_SOURCE = Contents.DATA_COMPONENTS.register("link_source",
			() -> DataComponentType.<GlobalPos>builder()
					.persistent(GlobalPos.CODEC)
					.networkSynchronized(GlobalPos.STREAM_CODEC)
					.build());

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> LUX_CHARGE = Contents.DATA_COMPONENTS.register("lux_charge",
			() -> DataComponentType.<Long>builder()
					.persistent(Codec.LONG)
					.networkSynchronized(ByteBufCodecs.VAR_LONG)
					.build());

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Byte>> WAND_BELT_SELECTED_INDEX = Contents.DATA_COMPONENTS.register("wand_belt_selected_index",
			() -> DataComponentType.<Byte>builder()
					.persistent(Codec.BYTE)
					.networkSynchronized(ByteBufCodecs.BYTE)
					.build());

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> PORTAL_UUID = Contents.DATA_COMPONENTS.register("portal_uuid",
			() -> DataComponentType.<UUID>builder()
					.persistent(UUIDUtil.CODEC)
					.networkSynchronized(UUIDUtil.STREAM_CODEC)
					.build());

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> ABSORBED_DAMAGE = Contents.DATA_COMPONENTS.register("absorbed_damage",
			() -> DataComponentType.<Double>builder()
					.persistent(Codec.DOUBLE)
					.networkSynchronized(ByteBufCodecs.DOUBLE)
					.build());

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> POWERED_ON = Contents.DATA_COMPONENTS.register("powered_on",
			() -> DataComponentType.<Boolean>builder()
					.persistent(Codec.BOOL)
					.networkSynchronized(ByteBufCodecs.BOOL)
					.build());

	public static void init() {}
}
