package gurumirum.gemthing.capability;

import gurumirum.gemthing.contents.*;
import gurumirum.gemthing.contents.item.wandbelt.WandBeltItem;
import gurumirum.gemthing.impl.RGB332;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import static gurumirum.gemthing.GemthingMod.MODID;
import static gurumirum.gemthing.GemthingMod.id;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModCapabilities {
	private ModCapabilities() {}

	public static final ItemCapability<LuxAcceptor, Void> LUX_ACCEPTOR = ItemCapability.createVoid(id("lux_acceptor"), LuxAcceptor.class);
	public static final ItemCapability<LuxContainerStat, Void> LUX_CONTAINER_STAT = ItemCapability.createVoid(id("lux_container_stat"), LuxContainerStat.class);
	public static final ItemCapability<LuxStat, Void> LUX_SOURCE_STAT = ItemCapability.createVoid(id("lux_source_stat"), LuxStat.class);

	public static final BlockCapability<LinkSource, Void> LINK_SOURCE = BlockCapability.createVoid(id("linkable"), LinkSource.class);
	public static final BlockCapability<LuxNetComponent, Direction> LUX_NET_COMPONENT = BlockCapability.createSided(id("lux_net_component"), LuxNetComponent.class);

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		for (GemItems value : GemItems.values()) {
			var stat = value.gem;
			event.registerItem(LUX_SOURCE_STAT, (s, v) -> stat, value.asItem());
		}
		event.registerItem(LUX_SOURCE_STAT, (s, v) -> GemStats.AMETHYST, Items.AMETHYST_SHARD);
		event.registerItem(LUX_SOURCE_STAT, (s, v) -> GemStats.DIAMOND, Items.DIAMOND);
		event.registerItem(LUX_SOURCE_STAT, (s, v) -> GemStats.EMERALD, Items.EMERALD);

		for (Wands w : Wands.values()) {
			if (w.luxContainerStat() != null) {
				registerLuxContainer(event, w.luxContainerStat(), w);
			}
		}

		registerLuxContainer(event, LuxContainerStat.simple(1000, RGB332.WHITE, 0, 100), ModItems.LUX_BATTERY);

		event.registerItem(ItemHandler.ITEM, (s, v) -> new WandBeltItem.ItemHandler(s), ModItems.WAND_BELT);

		registerRelayLinkSource(event, ModBlockEntities.RELAY.get());
		registerLuxNodeBlock(event, ModBlockEntities.RELAY.get());
		registerRelayLinkSource(event, ModBlockEntities.LUX_SOURCE.get());
		registerLuxNodeBlock(event, ModBlockEntities.LUX_SOURCE.get());
		registerLuxNodeBlock(event, ModBlockEntities.REMOTE_CHARGER.get());
		registerLuxNodeBlock(event, ModBlockEntities.REMOTE_CHARGER_2.get());
	}

	private static void registerLuxContainer(RegisterCapabilitiesEvent event, LuxContainerStat stat, ItemLike... items) {
		event.registerItem(LUX_CONTAINER_STAT, (s, v) -> stat, items);
		event.registerItem(LUX_ACCEPTOR, (s, v) -> new ItemStackLuxAcceptor(s, stat), items);
	}

	private static <T extends BlockEntity & LinkSource> void registerRelayLinkSource(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
		event.registerBlockEntity(LINK_SOURCE, type, (be, context) -> be);
	}

	private static <T extends BlockEntity & LuxNetComponent> void registerLuxNodeBlock(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
		event.registerBlockEntity(LUX_NET_COMPONENT, type, (be, context) -> be);
	}
}
