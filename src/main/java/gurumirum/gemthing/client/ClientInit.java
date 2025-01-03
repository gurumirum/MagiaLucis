package gurumirum.gemthing.client;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.contents.Wands;
import gurumirum.gemthing.contents.item.wandbelt.WandBeltGuiLayer;
import gurumirum.gemthing.contents.item.wandbelt.WandBeltScreen;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import java.util.Arrays;

import static gurumirum.gemthing.GemthingMod.MODID;
import static gurumirum.gemthing.GemthingMod.id;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientInit {
	private ClientInit() {}

	@SubscribeEvent
	public static void setup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			@SuppressWarnings("deprecation")
			ItemPropertyFunction wandUsing = (stack, level, entity, seed) ->
					entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1 : 0;
			ResourceLocation wandUsingId = ResourceLocation.withDefaultNamespace("using");

			for (Wands w : Wands.values()) {
				ItemProperties.register(w.asItem(), wandUsingId, wandUsing);
			}
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
		event.registerItem(new WandItemExtension(), Arrays.stream(Wands.values())
				.map(Wands::asItem).toArray(Item[]::new));
	}

	@SubscribeEvent
	public static void registerMenuScreens(RegisterMenuScreensEvent event) {
		event.register(Contents.WANG_BELT_MENU.get(), WandBeltScreen::new);
	}

	@SubscribeEvent
	public static void registerGuiLayers(RegisterGuiLayersEvent event) {
		event.registerAboveAll(id("wand_belt"), new WandBeltGuiLayer());
	}
}
