package gurumirum.magialucis.contents;

import gurumirum.magialucis.contents.block.AmberLanternBlockEntity;
import gurumirum.magialucis.contents.block.artisanrytable.ArtisanryTableBlockEntity;
import gurumirum.magialucis.contents.block.fieldmonitor.FieldMonitorBlockEntity;
import gurumirum.magialucis.contents.block.lux.ambercore.AmberCoreBlockEntity;
import gurumirum.magialucis.contents.block.lux.charger.ChargerBlockEntity;
import gurumirum.magialucis.contents.block.lux.charger.ChargerTier;
import gurumirum.magialucis.contents.block.lux.charger.RemoteChargerBlockEntity;
import gurumirum.magialucis.contents.block.lux.connector.ConnectorBlockEntity;
import gurumirum.magialucis.contents.block.lux.lightbasin.LightBasinBlockEntity;
import gurumirum.magialucis.contents.block.lux.lightloom.LightLoomBlockEntity;
import gurumirum.magialucis.contents.block.lux.lightloom.LightLoomType;
import gurumirum.magialucis.contents.block.lux.relay.RelayBlockEntity;
import gurumirum.magialucis.contents.block.lux.source.LuxSourceBlockEntity;
import gurumirum.magialucis.contents.block.lux.splitter.SplitterBlockEntity;
import gurumirum.magialucis.contents.block.lux.sunlight.core.MoonlightCoreBlockEntity;
import gurumirum.magialucis.contents.block.lux.sunlight.core.SunlightCoreBlockEntity;
import gurumirum.magialucis.contents.block.lux.sunlight.focus.SunlightFocusBlockEntity;
import gurumirum.magialucis.utils.BlockProvider;
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

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AmberLanternBlockEntity>> AMBER_LANTERN =
			blockEntity("amber_lantern", AmberLanternBlockEntity::new, ModBlocks.AMBER_LANTERN);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ArtisanryTableBlockEntity>> ARTISANRY_TABLE =
			blockEntity("artisanry_table", ArtisanryTableBlockEntity::new, ModBlocks.ARTISANRY_TABLE);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LightLoomBlockEntity>> CITRINE_LIGHTLOOM =
			lightLoom("citrine_lightloom", LightLoomType.CITRINE, ModBlocks.CITRINE_LIGHTLOOM);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LightLoomBlockEntity>> IOLITE_LIGHTLOOM =
			lightLoom("iolite_lightloom", LightLoomType.IOLITE, ModBlocks.IOLITE_LIGHTLOOM);

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

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SplitterBlockEntity>> SPLITTER =
			blockEntity("splitter", SplitterBlockEntity::new, ModBlocks.SPLITTER);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ConnectorBlockEntity>> CONNECTOR =
			blockEntity("connector", ConnectorBlockEntity::new, ModBlocks.CONNECTOR);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChargerBlockEntity>> AMBER_CHARGER =
			charger("amber_charger", ChargerTier.PRIMITIVE, ModBlocks.AMBER_CHARGER);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChargerBlockEntity>> LUMINOUS_CHARGER =
			charger("luminous_charger", ChargerTier.LUMINOUS, ModBlocks.LUMINOUS_CHARGER);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RemoteChargerBlockEntity>> LUMINOUS_REMOTE_CHARGER =
			remoteCharger("remote_charger", ChargerTier.LUMINOUS, ModBlocks.LUMINOUS_RESONANCE_LANTERN);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RemoteChargerBlockEntity>> LUSTROUS_REMOTE_CHARGER =
			remoteCharger("lustrous_remote_charger", ChargerTier.LUSTROUS, ModBlocks.LUSTROUS_RESONANCE_LANTERN);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LuxSourceBlockEntity>> LUX_SOURCE =
			blockEntity("lux_source", LuxSourceBlockEntity::new, ModBlocks.PRIMITIVE_LUX_SOURCE, ModBlocks.LUMINOUS_LUX_SOURCE,
					ModBlocks.LUSTROUS_LUX_SOURCE, ModBlocks.IRRADIANT_LUX_SOURCE, ModBlocks.EXUBERANT_LUX_SOURCE);

	private static DeferredHolder<BlockEntityType<?>, BlockEntityType<LightLoomBlockEntity>> lightLoom(
			String name, LightLoomType type, BlockProvider... validBlocks
	) {
		return blockEntity(name,
				(pos, state) -> new LightLoomBlockEntity(type, pos, state),
				validBlocks);
	}

	private static DeferredHolder<BlockEntityType<?>, BlockEntityType<ChargerBlockEntity>> charger(
			String name, ChargerTier tier, BlockProvider... validBlocks
	) {
		return blockEntity(name,
				(pos, state) -> new ChargerBlockEntity(tier, pos, state),
				validBlocks);
	}

	private static DeferredHolder<BlockEntityType<?>, BlockEntityType<RemoteChargerBlockEntity>> remoteCharger(
			String name, ChargerTier tier, BlockProvider... validBlocks
	) {
		return blockEntity(name,
				(pos, state) -> new RemoteChargerBlockEntity(tier, pos, state),
				validBlocks);
	}

	@SuppressWarnings("DataFlowIssue")
	private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> blockEntity(
			String id, BlockEntityType.BlockEntitySupplier<? extends T> factory, BlockProvider... validBlocks) {
		return BLOCK_ENTITIES.register(id, () -> BlockEntityType.Builder.<T>of(factory, Arrays.stream(validBlocks)
				.map(BlockProvider::block).toArray(Block[]::new)).build(null));
	}

	public static void init() {}
}
