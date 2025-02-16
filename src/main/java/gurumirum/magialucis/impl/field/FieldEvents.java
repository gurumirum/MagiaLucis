package gurumirum.magialucis.impl.field;

import gurumirum.magialucis.api.MagiaLucisApi;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = MagiaLucisApi.MODID)
public final class FieldEvents {
	private FieldEvents() {}

	private static final int UPDATE_CYCLE = 40;

	@SubscribeEvent
	public static void onLevelTick(LevelTickEvent.Post event) {
		if (event.getLevel() instanceof ServerLevel serverLevel && serverLevel.getGameTime() % UPDATE_CYCLE == 0) {
			FieldManager.get(serverLevel).update();
		}
	}
}
