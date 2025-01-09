package gurumirum.magialucis.capability;

import gurumirum.magialucis.contents.GemItems;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.ModItems;
import gurumirum.magialucis.contents.Wands;
import gurumirum.magialucis.contents.item.wandbelt.WandBeltItem;
import gurumirum.magialucis.impl.RGB332;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import static gurumirum.magialucis.MagiaLucisMod.MODID;
import static gurumirum.magialucis.MagiaLucisMod.id;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModCapabilities {
	private ModCapabilities() {}

	public static final ItemCapability<LuxAcceptor, Void> LUX_ACCEPTOR = ItemCapability.createVoid(id("lux_acceptor"), LuxAcceptor.class);
	public static final ItemCapability<LuxContainerStat, Void> LUX_CONTAINER_STAT = ItemCapability.createVoid(id("lux_container_stat"), LuxContainerStat.class);
	public static final ItemCapability<LuxStat, Void> GEM_STAT = ItemCapability.createVoid(id("gem_stat"), LuxStat.class);

	public static final BlockCapability<LinkSource, Void> LINK_SOURCE = BlockCapability.createVoid(id("linkable"), LinkSource.class);
	public static final BlockCapability<LuxNetComponent, Direction> LUX_NET_COMPONENT = BlockCapability.createSided(id("lux_net_component"), LuxNetComponent.class);

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		for (GemItems value : GemItems.values()) {
			var stat = value.gem;
			event.registerItem(GEM_STAT, (s, v) -> stat, value.asItem());
		}
		event.registerItem(GEM_STAT, (s, v) -> GemStats.AMETHYST, Items.AMETHYST_SHARD);
		event.registerItem(GEM_STAT, (s, v) -> GemStats.DIAMOND, Items.DIAMOND);
		event.registerItem(GEM_STAT, (s, v) -> GemStats.EMERALD, Items.EMERALD);

		for (Wands w : Wands.values()) {
			if (w.luxContainerStat() != null) {
				registerLuxContainer(event, w.luxContainerStat(), w);
			}
		}

		registerLuxContainer(event, LuxContainerStat.simple(1000, RGB332.WHITE, 0, 100), ModItems.LUX_BATTERY);

		event.registerItem(ItemHandler.ITEM, (s, v) -> new WandBeltItem.ItemHandler(s), ModItems.WAND_BELT);

		registerRelayLinkSource(event, ModBlockEntities.RELAY.get());
		event.registerBlockEntity(LUX_NET_COMPONENT, ModBlockEntities.RELAY.get(), (be, dir) ->
				be.getBlockState().getValue(BlockStateProperties.FACING).getOpposite() != dir ? be : null);

		registerRelayLinkSource(event, ModBlockEntities.LUX_SOURCE.get());
		registerLuxNetComponent(event, ModBlockEntities.LUX_SOURCE.get());
		registerLuxNetComponent(event, ModBlockEntities.REMOTE_CHARGER.get());
		registerLuxNetComponent(event, ModBlockEntities.REMOTE_CHARGER_2.get());
	}

	private static void registerLuxContainer(RegisterCapabilitiesEvent event, LuxContainerStat stat, ItemLike... items) {
		event.registerItem(LUX_CONTAINER_STAT, (s, v) -> stat, items);
		event.registerItem(LUX_ACCEPTOR, (s, v) -> new ItemStackLuxAcceptor(s, stat), items);
	}

	private static <T extends BlockEntity & LinkSource> void registerRelayLinkSource(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
		event.registerBlockEntity(LINK_SOURCE, type, (be, context) -> be);
	}

	private static <T extends BlockEntity & LuxNetComponent> void registerLuxNetComponent(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
		event.registerBlockEntity(LUX_NET_COMPONENT, type, (be, context) -> be);
	}
}
