package gurumirum.gemthing.capability;

import gurumirum.gemthing.contents.GemItems;
import gurumirum.gemthing.contents.ModItems;
import gurumirum.gemthing.contents.Wands;
import gurumirum.gemthing.contents.item.wandbelt.WandBeltItem;
import gurumirum.gemthing.impl.RGB332;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
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
	public static final ItemCapability<GemStat, Void> GEM_STAT = ItemCapability.createVoid(id("gem_stat"), GemStat.class);

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		for (GemItems value : GemItems.values()) {
			var stat = value.stat;
			event.registerItem(GEM_STAT, (s, v) -> stat, value.asItem());
		}
		event.registerItem(GEM_STAT, (s, v) -> Gems.AMETHYST, Items.AMETHYST_SHARD);
		event.registerItem(GEM_STAT, (s, v) -> Gems.DIAMOND, Items.DIAMOND);
		event.registerItem(GEM_STAT, (s, v) -> Gems.EMERALD, Items.EMERALD);

		for (Wands w : Wands.values()) {
			if (w.luxContainerStat() != null) {
				registerLuxContainer(event, w.luxContainerStat(), w);
			}
		}

		registerLuxContainer(event, new LuxContainerStat.Simple(1000, RGB332.WHITE, 0, 100), ModItems.LUX_BATTERY);

		event.registerItem(ItemHandler.ITEM, (s, v) -> new WandBeltItem.ItemHandler(s), ModItems.WAND_BELT);
	}

	private static void registerLuxContainer(RegisterCapabilitiesEvent event, LuxContainerStat stat, ItemLike... items) {
		event.registerItem(GEM_STAT, (s, v) -> stat, items);
		event.registerItem(LUX_CONTAINER_STAT, (s, v) -> stat, items);
		event.registerItem(LUX_ACCEPTOR, (s, v) -> new LuxAcceptorImpl(s, stat), items);
	}
}
