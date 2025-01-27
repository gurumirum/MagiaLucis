package gurumirum.magialucis.client.render;

import gurumirum.magialucis.client.render.beam.BeamRender;
import gurumirum.magialucis.client.render.light.LightEffectProvider;
import gurumirum.magialucis.client.render.light.LightEffectRender;
import gurumirum.magialucis.client.render.prism.PrismEffect;
import gurumirum.magialucis.client.render.prism.PrismEffectRender;
import gurumirum.magialucis.contents.item.wand.ConfigurationWandOverlay;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.ArrayList;
import java.util.List;

import static gurumirum.magialucis.MagiaLucisMod.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class RenderEffects {
	private RenderEffects() {}

	private static final List<RenderEffectManager<?>> effectManagers = new ArrayList<>();

	public static final RenderEffectManager<LightEffectProvider> light = create();
	public static final RenderEffectManager<PrismEffect> prism = create();

	private static <T extends RenderEffect> RenderEffectManager<T> create() {
		RenderEffectManager<T> manager = new RenderEffectManager<>();
		effectManagers.add(manager);
		return manager;
	}

	@SubscribeEvent
	public static void onRender(RenderLevelStageEvent event) {
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
			AncientLightCrumblingRender.render(event);
			LightEffectRender.render(event);
			PrismEffectRender.render(event);
		} else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
			BeamRender.render(event);
		} else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
			ConfigurationWandOverlay.render(event);
		}
	}

	@SubscribeEvent
	public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
		effectManagers.forEach(RenderEffectManager::clear);
	}

	@SubscribeEvent
	public static void onLevelUnload(LevelEvent.Unload event) {
		LevelAccessor level = event.getLevel();
		if (!level.isClientSide()) return;

		for (RenderEffectManager<?> manager : effectManagers) {
			manager.onLevelUnload(level);
		}
	}
}
