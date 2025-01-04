package gurumirum.gemthing.client;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.client.render.entity.GemGolemRenderer;
import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.contents.Wands;
import gurumirum.gemthing.contents.block.lux.relay.RelayBlockEntityRenderer;
import gurumirum.gemthing.contents.item.wand.AmberTorchWandItem;
import gurumirum.gemthing.contents.item.wand.RecallStaffWandItem;
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

	public static final ResourceLocation USING = ResourceLocation.withDefaultNamespace("using");
	public static final ResourceLocation NO_CHARGE = ResourceLocation.withDefaultNamespace("no_charge");

	@SubscribeEvent
	public static void setup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			@SuppressWarnings("deprecation")
			ItemPropertyFunction wandUsing = (stack, level, entity, seed) ->
					entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1 : 0;

			for (Wands w : Wands.values()) {
				ItemProperties.register(w.asItem(), USING, wandUsing);
			}

			ItemProperties.register(Wands.AMBER_TORCH.asItem(), NO_CHARGE, (stack, level, entity, seed) -> {
				return stack.getOrDefault(Contents.LUX_CHARGE, 0L) < AmberTorchWandItem.COST_PER_LIGHT_SOURCE ? 1 : 0;
			});
			ItemProperties.register(Wands.RECALL_STAFF.asItem(), USING, wandUsing);
			ItemProperties.register(Wands.RECALL_STAFF.asItem(), NO_CHARGE, (stack, level, entity, seed) -> {
				return stack.getOrDefault(Contents.LUX_CHARGE, 0L) < RecallStaffWandItem.COST_PER_RECALL ||
						entity != null && entity.hasEffect(Contents.RECALL_FATIGUE) ? 1 : 0;
			});
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

	@SubscribeEvent
	public static void registerEntityRegister(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(Contents.GEM_GOLEM.get(), GemGolemRenderer::new);

		event.registerBlockEntityRenderer(Contents.RELAY_BLOCK_ENTITY.get(), RelayBlockEntityRenderer::new);
	}
}
