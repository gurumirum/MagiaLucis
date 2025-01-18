package gurumirum.magialucis.client;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.*;
import gurumirum.magialucis.contents.block.lux.lightbasin.LightBasinBlockEntityRenderer;
import gurumirum.magialucis.contents.entity.GemGolemRenderer;
import gurumirum.magialucis.contents.entity.EnderChestPortalRenderer;
import gurumirum.magialucis.contents.block.lux.BasicRelayBlockEntityRenderer;
import gurumirum.magialucis.contents.block.lux.relay.RelayBlockEntityRenderer;
import gurumirum.magialucis.contents.block.lux.relay.RelayItemExtension;
import gurumirum.magialucis.contents.block.sunlight.focus.SunlightFocusBlockEntityRenderer;
import gurumirum.magialucis.contents.block.sunlight.focus.SunlightFocusItemExtension;
import gurumirum.magialucis.contents.entity.LesserIceProjectileRenderer;
import gurumirum.magialucis.contents.item.wand.*;
import gurumirum.magialucis.contents.item.wandbelt.WandBeltGuiLayer;
import gurumirum.magialucis.contents.item.wandbelt.WandBeltScreen;
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

import static gurumirum.magialucis.MagiaLucisMod.MODID;
import static gurumirum.magialucis.MagiaLucisMod.id;

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

			ItemProperties.register(Wands.ANCIENT_LIGHT.asItem(), USING, wandUsing);

			ItemProperties.register(Wands.AMBER_TORCH.asItem(), NO_CHARGE, noCharge(AmberTorchWandItem.COST_PER_LIGHT_SOURCE));

			ItemProperties.register(Wands.LESSER_ICE_STAFF.asItem(), USING, wandUsing);
			ItemProperties.register(Wands.LESSER_ICE_STAFF.asItem(), NO_CHARGE, noCharge(LesserIceStaffItem.COST_PER_ATTACK));

			ItemProperties.register(Wands.RECALL_STAFF.asItem(), USING, wandUsing);
			ItemProperties.register(Wands.RECALL_STAFF.asItem(), NO_CHARGE, (stack, level, entity, seed) -> {
				return stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L) < RecallStaffWandItem.COST_PER_RECALL ||
						entity != null && entity.hasEffect(ModMobEffects.RECALL_FATIGUE) ? 1 : 0;
			});

			ItemProperties.register(Wands.HEAL_WAND.asItem(), USING, wandUsing);
			ItemProperties.register(Wands.HEAL_WAND.asItem(), NO_CHARGE, noCharge(HealWandItem.COST_PER_CAST));

			ItemProperties.register(Wands.LAPIS_SHIELD.asItem(), USING, wandUsing);
			ItemProperties.register(Wands.LAPIS_SHIELD.asItem(), NO_CHARGE, noCharge(LapisShieldItem.COST_PER_BLOCK));

			ItemProperties.register(Wands.DIAMOND_MACE.asItem(), NO_CHARGE, noCharge(DiamondMaceItem.COST_PER_ATTACK));

			ItemProperties.register(Wands.ENDER_WAND.asItem(), NO_CHARGE, noCharge(EnderChestPortalWandItem.COST_PER_PORTAL_SPAWN));
		});
	}

	@SubscribeEvent
	public static void addLayers(EntityRenderersEvent.AddLayers event) {
		for (var s : event.getSkins()) {
			if (!(event.getSkin(s) instanceof PlayerRenderer r)) {
				MagiaLucisMod.LOGGER.warn("Failed to inject wand effect layer to player skin {}", s);
				continue;
			}
			r.addLayer(new WandEffectLayer(r));
		}
	}

	@SubscribeEvent
	public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
		event.registerItem(new WandItemExtension(), Arrays.stream(Wands.values())
				.map(Wands::asItem).toArray(Item[]::new));
		event.registerItem(new RelayItemExtension(), ModBlocks.RELAY.asItem());
		event.registerItem(new SunlightFocusItemExtension(), ModBlocks.SUNLIGHT_FOCUS.blockItem());
	}

	@SubscribeEvent
	public static void registerMenuScreens(RegisterMenuScreensEvent event) {
		event.register(ModMenus.WANG_BELT_MENU.get(), WandBeltScreen::new);
	}

	@SubscribeEvent
	public static void registerGuiLayers(RegisterGuiLayersEvent event) {
		event.registerAboveAll(id("wand_belt"), new WandBeltGuiLayer());
		event.registerAboveAll(id("configuration_wand"), new ConfigurationWandGuiLayer());
	}

	@SubscribeEvent
	public static void registerEntityRegister(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntities.GEM_GOLEM.get(), GemGolemRenderer::new);
		event.registerEntityRenderer(ModEntities.ENDER_CHEST_PORTAL.get(), EnderChestPortalRenderer::new);
		event.registerEntityRenderer(ModEntities.LESSER_ICE_PROJECTILE.get(), LesserIceProjectileRenderer::new);

		event.registerBlockEntityRenderer(ModBlockEntities.RELAY.get(), RelayBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.LUX_SOURCE.get(), BasicRelayBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.SUNLIGHT_FOCUS.get(), SunlightFocusBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.LIGHT_BASIN.get(), LightBasinBlockEntityRenderer::new);
	}

	@SuppressWarnings("deprecation")
	private static ItemPropertyFunction noCharge(long minimumCharge) {
		return (stack, level, entity, seed) -> {
			return stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L) < minimumCharge ? 1 : 0;
		};
	}
}
