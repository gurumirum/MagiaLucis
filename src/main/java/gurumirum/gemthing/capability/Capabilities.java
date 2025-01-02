package gurumirum.gemthing.capability;

import gurumirum.gemthing.contents.Gems;
import gurumirum.gemthing.contents.ModItems;
import gurumirum.gemthing.impl.RGB332;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import static gurumirum.gemthing.GemthingMod.MODID;
import static gurumirum.gemthing.GemthingMod.id;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public final class Capabilities {
	private Capabilities() {}

	public static final ItemCapability<LuxAcceptor, Void> LUX_ACCEPTOR = ItemCapability.createVoid(id("lux_acceptor"), LuxAcceptor.class);
	public static final ItemCapability<LuxContainerStat, Void> LUX_CONTAINER_STAT = ItemCapability.createVoid(id("lux_container_stat"), LuxContainerStat.class);
	public static final ItemCapability<GemStat, Void> GEM_STAT = ItemCapability.createVoid(id("gem_stat"), GemStat.class);

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		for (Gems value : Gems.values()) {
			var stat = value.stat;
			event.registerItem(GEM_STAT, (s, v) -> stat, value.asItem());
		}
		event.registerItem(GEM_STAT, (s, v) -> GemStat.AMETHYST, Items.AMETHYST_SHARD);
		event.registerItem(GEM_STAT, (s, v) -> GemStat.DIAMOND, Items.DIAMOND);
		event.registerItem(GEM_STAT, (s, v) -> GemStat.EMERALD, Items.EMERALD);

		registerLuxContainer(event, new LuxContainerStat.Simple(1000, RGB332.WHITE, 0, 100), ModItems.LUX_BATTERY);
	}

	private static void registerLuxContainer(RegisterCapabilitiesEvent event, LuxContainerStat stat, ItemLike... items) {
		event.registerItem(LUX_CONTAINER_STAT, (s, v) -> stat, items);
		event.registerItem(LUX_ACCEPTOR, (s, v) -> new LuxAcceptorImpl(s, stat), items);
	}
}
