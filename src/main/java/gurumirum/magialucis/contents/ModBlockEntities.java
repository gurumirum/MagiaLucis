package gurumirum.magialucis.contents;

import gurumirum.magialucis.contents.block.fieldmonitor.FieldMonitorBlockEntity;
import gurumirum.magialucis.contents.block.lux.ambercore.AmberCoreBlockEntity;
import gurumirum.magialucis.contents.block.lux.lightbasin.LightBasinBlockEntity;
import gurumirum.magialucis.contents.block.lux.relay.RelayBlockEntity;
import gurumirum.magialucis.contents.block.lux.charger.RemoteChargerBlockEntity;
import gurumirum.magialucis.contents.block.lux.source.LuxSourceBlockEntity;
import gurumirum.magialucis.contents.block.sunlight.core.MoonlightCoreBlockEntity;
import gurumirum.magialucis.contents.block.sunlight.core.SunlightCoreBlockEntity;
import gurumirum.magialucis.contents.block.sunlight.focus.SunlightFocusBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Arrays;

import static gurumirum.magialucis.contents.Contents.BLOCK_ENTITIES;

public final class ModBlockEntities {
	private ModBlockEntities() {}

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AmberCoreBlockEntity>> AMBER_CORE =
			blockEntity("amber_core", AmberCoreBlockEntity::new, ModBlocks.AMBER_CORE);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LightBasinBlockEntity>> LIGHT_BASIN =
			blockEntity("light_basin", LightBasinBlockEntity::new, ModBlocks.LIGHT_BASIN);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FieldMonitorBlockEntity>> FIELD_MONITOR =
			blockEntity("field_monitor", FieldMonitorBlockEntity::new, ModBlocks.FIELD_MONITOR);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SunlightCoreBlockEntity>> SUNLIGHT_CORE =
			blockEntity("sunlight_core", SunlightCoreBlockEntity::new, ModBlocks.SUNLIGHT_CORE);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MoonlightCoreBlockEntity>> MOONLIGHT_CORE =
			blockEntity("moonlight_core", MoonlightCoreBlockEntity::new, ModBlocks.MOONLIGHT_CORE);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SunlightFocusBlockEntity>> SUNLIGHT_FOCUS =
			blockEntity("sunlight_focus", SunlightFocusBlockEntity::new, ModBlocks.SUNLIGHT_FOCUS);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RelayBlockEntity>> RELAY =
			blockEntity("relay", RelayBlockEntity::new, ModBlocks.RELAY);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RemoteChargerBlockEntity>> REMOTE_CHARGER =
			blockEntity("remote_charger", RemoteChargerBlockEntity::basic, ModBlocks.REMOTE_CHARGER);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RemoteChargerBlockEntity>> REMOTE_CHARGER_2 =
			blockEntity("remote_charger_2", RemoteChargerBlockEntity::advanced, ModBlocks.REMOTE_CHARGER_2);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LuxSourceBlockEntity>> LUX_SOURCE =
			blockEntity("lux_source", LuxSourceBlockEntity::new, ModBlocks.LUX_SOURCE, ModBlocks.LUX_SOURCE_2);

	@SuppressWarnings("DataFlowIssue")
	private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> blockEntity(
			String id, BlockEntityType.BlockEntitySupplier<? extends T> factory, BlockProvider... validBlocks) {
		return BLOCK_ENTITIES.register(id, () -> BlockEntityType.Builder.<T>of(factory, Arrays.stream(validBlocks)
				.map(BlockProvider::block).toArray(Block[]::new)).build(null));
	}

	public static void init() {}
}
