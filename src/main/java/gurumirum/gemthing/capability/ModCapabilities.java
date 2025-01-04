package gurumirum.gemthing.capability;

import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.contents.GemItems;
import gurumirum.gemthing.contents.ModItems;
import gurumirum.gemthing.contents.Wands;
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
	public static final ItemCapability<LuxSourceStat, Void> LUX_SOURCE_STAT = ItemCapability.createVoid(id("lux_source_stat"), LuxSourceStat.class);

	public static final BlockCapability<LinkSource, Void> LINK_SOURCE = BlockCapability.createVoid(id("linkable"), LinkSource.class);
	public static final BlockCapability<LuxNodeBlock, Direction> LUX_NODE_BLOCK = BlockCapability.createSided(id("lux_node_block"), LuxNodeBlock.class);

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		for (GemItems value : GemItems.values()) {
			var stat = value.gem;
			event.registerItem(LUX_SOURCE_STAT, (s, v) -> stat, value.asItem());
		}
		event.registerItem(LUX_SOURCE_STAT, (s, v) -> Gems.AMETHYST, Items.AMETHYST_SHARD);
		event.registerItem(LUX_SOURCE_STAT, (s, v) -> Gems.DIAMOND, Items.DIAMOND);
		event.registerItem(LUX_SOURCE_STAT, (s, v) -> Gems.EMERALD, Items.EMERALD);

		for (Wands w : Wands.values()) {
			if (w.luxContainerStat() != null) {
				registerLuxContainer(event, w.luxContainerStat(), w);
			}
		}

		registerLuxContainer(event, LuxContainerStat.simple(1000, RGB332.WHITE, 0, 100), ModItems.LUX_BATTERY);

		event.registerItem(ItemHandler.ITEM, (s, v) -> new WandBeltItem.ItemHandler(s), ModItems.WAND_BELT);

		registerRelayLinkSource(event, Contents.RELAY_BLOCK_ENTITY.get());
		registerLuxNodeBlock(event, Contents.RELAY_BLOCK_ENTITY.get());
	}

	private static void registerLuxContainer(RegisterCapabilitiesEvent event, LuxContainerStat stat, ItemLike... items) {
		event.registerItem(LUX_SOURCE_STAT, (s, v) -> stat, items);
		event.registerItem(LUX_CONTAINER_STAT, (s, v) -> stat, items);
		event.registerItem(LUX_ACCEPTOR, (s, v) -> new LuxAcceptorImpl(s, stat), items);
	}

	private static <T extends BlockEntity & LinkSource> void registerRelayLinkSource(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
		event.registerBlockEntity(LINK_SOURCE, type, (be, context) -> be);
	}

	private static <T extends BlockEntity & LuxNodeBlock> void registerLuxNodeBlock(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
		event.registerBlockEntity(LUX_NODE_BLOCK, type, (be, context) -> be);
	}
}
