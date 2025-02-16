package gurumirum.magialucis;

import com.mojang.logging.LogUtils;
import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.api.capability.MagiaLucisCaps;
import gurumirum.magialucis.api.capability.DirectLinkDestination;
import gurumirum.magialucis.api.capability.LinkDestination;
import gurumirum.magialucis.api.capability.LinkSource;
import gurumirum.magialucis.api.capability.LuxContainerStat;
import gurumirum.magialucis.capability.ItemStackLuxAcceptor;
import gurumirum.magialucis.contents.Accessories;
import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.Wands;
import gurumirum.magialucis.contents.block.lux.lightloom.LightLoomType;
import gurumirum.magialucis.contents.item.wandbelt.WandBeltItem;
import gurumirum.magialucis.impl.field.Fields;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.slf4j.Logger;

@Mod(MagiaLucisApi.MODID)
public class MagiaLucisMod {
	public static final Logger LOGGER = LogUtils.getLogger();

	public MagiaLucisMod(IEventBus modBus) {
		Contents.init(modBus);
		Fields.init();

		modBus.addListener(MagiaLucisMod::registerCapabilities);
	}

	private static void registerCapabilities(RegisterCapabilitiesEvent event) {
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

		event.registerItem(Capabilities.ItemHandler.ITEM, (s, v) -> new WandBeltItem.ItemHandler(s), Accessories.WAND_BELT);

		linkSource(event, ModBlockEntities.RELAY.get());
		linkDestination(event, ModBlockEntities.RELAY.get());
		directLinkDestination(event, ModBlockEntities.RELAY.get());

		linkDestination(event, ModBlockEntities.SPLITTER.get());
		linkDestination(event, ModBlockEntities.CONNECTOR.get());

		linkDestination(event, ModBlockEntities.AMBER_CHARGER.get());
		directLinkDestination(event, ModBlockEntities.AMBER_CHARGER.get());
		linkDestination(event, ModBlockEntities.LUMINOUS_CHARGER.get());
		directLinkDestination(event, ModBlockEntities.LUMINOUS_CHARGER.get());

		linkDestination(event, ModBlockEntities.LUMINOUS_REMOTE_CHARGER.get());
		directLinkDestination(event, ModBlockEntities.LUMINOUS_REMOTE_CHARGER.get());
		linkDestination(event, ModBlockEntities.LUSTROUS_REMOTE_CHARGER.get());
		directLinkDestination(event, ModBlockEntities.LUSTROUS_REMOTE_CHARGER.get());

		for (LightLoomType type : LightLoomType.values()) {
			linkDestination(event, type.blockEntityType());
		}

		linkDestination(event, ModBlockEntities.LIGHT_BASIN.get());
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.LIGHT_BASIN.get(), (lightBasin, direction) -> {
			return direction == Direction.DOWN || direction == Direction.UP ? null : lightBasin.inventory();
		});

		linkDestination(event, ModBlockEntities.SUNLIGHT_FOCUS.get());
		linkSource(event, ModBlockEntities.SUNLIGHT_FOCUS.get());
		linkDestination(event, ModBlockEntities.SUNLIGHT_CORE.get());

		linkSource(event, ModBlockEntities.LUX_SOURCE.get());
		linkDestination(event, ModBlockEntities.LUX_SOURCE.get());
	}

	private static void luxContainer(RegisterCapabilitiesEvent event, LuxContainerStat stat, ItemLike... items) {
		event.registerItem(MagiaLucisCaps.LUX_CONTAINER_STAT, (s, v) -> new ItemStackLuxAcceptor(s, stat), items);
		event.registerItem(MagiaLucisCaps.LUX_ACCEPTOR, (s, v) -> new ItemStackLuxAcceptor(s, stat), items);
	}

	private static <T extends BlockEntity & LinkSource> void linkSource(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
		event.registerBlockEntity(MagiaLucisCaps.LINK_SOURCE, type, (be, context) -> be);
	}

	private static <T extends BlockEntity & LinkDestination> void linkDestination(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
		event.registerBlockEntity(MagiaLucisCaps.LINK_DESTINATION, type, (be, context) -> be);
	}

	private static <T extends BlockEntity & DirectLinkDestination> void directLinkDestination(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
		event.registerBlockEntity(MagiaLucisCaps.DIRECT_LINK_DESTINATION, type, (be, context) -> be);
	}
}
