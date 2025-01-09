package gurumirum.magialucis.contents;

import gurumirum.magialucis.contents.block.fieldmonitor.FieldMonitorBlockEntity;
import gurumirum.magialucis.contents.block.lux.ambercore.AmberCoreBlockEntity;
import gurumirum.magialucis.contents.block.lux.relay.RelayBlockEntity;
import gurumirum.magialucis.contents.block.lux.remotecharger.RemoteChargerBlockEntity;
import gurumirum.magialucis.contents.block.lux.source.LuxSourceBlockEntity;
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

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FieldMonitorBlockEntity>> FIELD_MONITOR =
			blockEntity("field_monitor", FieldMonitorBlockEntity::new, ModBlocks.FIELD_MONITOR);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RelayBlockEntity>> RELAY =
			blockEntity("relay", RelayBlockEntity::new, ModBlocks.RELAY);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RemoteChargerBlockEntity>> REMOTE_CHARGER =
			blockEntity("remote_charger", RemoteChargerBlockEntity::basic, ModBlocks.REMOTE_CHARGER);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RemoteChargerBlockEntity>> REMOTE_CHARGER_2 =
			blockEntity("remote_charger_2", RemoteChargerBlockEntity::advanced, ModBlocks.REMOTE_CHARGER_2);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LuxSourceBlockEntity>> LUX_SOURCE =
			blockEntity("lux_source", LuxSourceBlockEntity::new, ModBlocks.LUX_SOURCE);

	@SuppressWarnings("DataFlowIssue")
	private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> blockEntity(
			String id, BlockEntityType.BlockEntitySupplier<? extends T> factory, BlockProvider... validBlocks) {
		return BLOCK_ENTITIES.register(id, () -> BlockEntityType.Builder.<T>of(factory, Arrays.stream(validBlocks)
				.map(BlockProvider::block).toArray(Block[]::new)).build(null));
	}

	public static void init() {}
}
