package gurumirum.magialucis.capability;

import gurumirum.magialucis.contents.Accessories;
import gurumirum.magialucis.contents.GemItems;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.Wands;
import gurumirum.magialucis.contents.item.wandbelt.WandBeltItem;
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

import static gurumirum.magialucis.MagiaLucisMod.MODID;
import static gurumirum.magialucis.MagiaLucisMod.id;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModCapabilities {
	private ModCapabilities() {}

	public static final ItemCapability<LuxAcceptor, Void> LUX_ACCEPTOR = ItemCapability.createVoid(id("lux_acceptor"), LuxAcceptor.class);
	public static final ItemCapability<LuxContainerStat, Void> LUX_CONTAINER_STAT = ItemCapability.createVoid(id("lux_container_stat"), LuxContainerStat.class);
	public static final ItemCapability<LuxStat, Void> GEM_STAT = ItemCapability.createVoid(id("gem_stat"), LuxStat.class);

	public static final BlockCapability<LinkSource, Void> LINK_SOURCE = BlockCapability.createVoid(id("linkable"), LinkSource.class);
	public static final BlockCapability<LinkDestination, Direction> LINK_DESTINATION = BlockCapability.createSided(id("link_destination"), LinkDestination.class);
	public static final BlockCapability<DirectLinkDestination, Direction> DIRECT_LINK_DESTINATION = BlockCapability.createSided(id("direct_link_destination"), DirectLinkDestination.class);

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		for (GemItems value : GemItems.values()) {
			var stat = value.gem;
			event.registerItem(GEM_STAT, (s, v) -> stat, value.asItem());
		}
		event.registerItem(GEM_STAT, (s, v) -> GemStats.AMETHYST, Items.AMETHYST_SHARD);
		event.registerItem(GEM_STAT, (s, v) -> GemStats.DIAMOND, Items.DIAMOND);
		event.registerItem(GEM_STAT, (s, v) -> GemStats.EMERALD, Items.EMERALD);
		event.registerItem(GEM_STAT, (s, v) -> GemStats.PRISMARINE, Items.PRISMARINE_CRYSTALS);
		event.registerItem(GEM_STAT, (s, v) -> GemStats.ENDER_PEARL, Items.ENDER_PEARL);

		for (Wands w : Wands.values()) {
			if (w.luxContainerStat() != null) {
				luxContainer(event, w.luxContainerStat(), w);
			}
		}

		for (Accessories a : Accessories.values()) {
			if (a.luxContainerStat() != null) {
				luxContainer(event, a.luxContainerStat(), a);
			}
		}

		event.registerItem(ItemHandler.ITEM, (s, v) -> new WandBeltItem.ItemHandler(s), Accessories.WAND_BELT);

		linkSource(event, ModBlockEntities.RELAY.get());
		linkDestination(event, ModBlockEntities.RELAY.get());
		directLinkDestination(event, ModBlockEntities.RELAY.get());

		linkDestination(event, ModBlockEntities.AMBER_CHARGER.get());
		directLinkDestination(event, ModBlockEntities.AMBER_CHARGER.get());
		linkDestination(event, ModBlockEntities.LUMINOUS_CHARGER.get());
		directLinkDestination(event, ModBlockEntities.LUMINOUS_CHARGER.get());

		linkDestination(event, ModBlockEntities.LUMINOUS_REMOTE_CHARGER.get());
		directLinkDestination(event, ModBlockEntities.LUMINOUS_REMOTE_CHARGER.get());
		linkDestination(event, ModBlockEntities.LUSTROUS_REMOTE_CHARGER.get());
		directLinkDestination(event, ModBlockEntities.LUSTROUS_REMOTE_CHARGER.get());

		linkDestination(event, ModBlockEntities.LIGHT_BASIN.get());
		event.registerBlockEntity(ItemHandler.BLOCK, ModBlockEntities.LIGHT_BASIN.get(), (lightBasin, direction) -> {
			return direction == Direction.DOWN || direction == Direction.UP ? null : lightBasin.inventory();
		});

		linkDestination(event, ModBlockEntities.SUNLIGHT_FOCUS.get());
		linkSource(event, ModBlockEntities.SUNLIGHT_FOCUS.get());
		linkDestination(event, ModBlockEntities.SUNLIGHT_CORE.get());

		linkSource(event, ModBlockEntities.LUX_SOURCE.get());
		linkDestination(event, ModBlockEntities.LUX_SOURCE.get());
	}

	private static void luxContainer(RegisterCapabilitiesEvent event, LuxContainerStat stat, ItemLike... items) {
		event.registerItem(LUX_CONTAINER_STAT, (s, v) -> stat, items);
		event.registerItem(LUX_ACCEPTOR, (s, v) -> new ItemStackLuxAcceptor(s, stat), items);
	}

	private static <T extends BlockEntity & LinkSource> void linkSource(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
		event.registerBlockEntity(LINK_SOURCE, type, (be, context) -> be);
	}

	private static <T extends BlockEntity & LinkDestination> void linkDestination(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
		event.registerBlockEntity(LINK_DESTINATION, type, (be, context) -> be);
	}

	private static <T extends BlockEntity & DirectLinkDestination> void directLinkDestination(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
		event.registerBlockEntity(DIRECT_LINK_DESTINATION, type, (be, context) -> be);
	}
}
