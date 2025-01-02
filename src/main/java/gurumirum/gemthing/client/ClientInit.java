package gurumirum.gemthing.client;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.contents.ModItems;
import gurumirum.gemthing.contents.item.wandbag.WandBagScreen;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import static gurumirum.gemthing.GemthingMod.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientInit {
	private ClientInit() {}

	@SubscribeEvent
	public static void setup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			ItemProperties.register(ModItems.WAND.asItem(),
					ResourceLocation.withDefaultNamespace("using"),
					(stack, level, entity, seed) ->
							entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1 : 0);
		});
	}

	@SubscribeEvent
	public static void addLayers(EntityRenderersEvent.AddLayers event) {
		for (var s : event.getSkins()) {
			if (!(event.getSkin(s) instanceof PlayerRenderer r)) {
				GemthingMod.LOGGER.warn("Failed to inject wand effect layer to player skin {}", s);
				continue;
			}
			r.addLayer(new WandEffectLayer(r));
		}
	}

	@SubscribeEvent
	public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
		event.registerItem(new WandItemExtension(), ModItems.WAND.asItem());
	}

	@SubscribeEvent
	public static void registerMenuScreens(RegisterMenuScreensEvent event) {
		event.register(Contents.WANG_BAG_MENU.get(), WandBagScreen::new);
	}
}
