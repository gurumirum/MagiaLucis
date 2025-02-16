package gurumirum.magialucis.contents.block.lux.sunlight.focus;

import gurumirum.magialucis.api.MagiaLucisApi;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = MagiaLucisApi.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class SunlightFocusModels {
	private SunlightFocusModels(){}

	public static final ModelResourceLocation MODEL_1 = new ModelResourceLocation(MagiaLucisApi.id("block/sunlight_focus_1"), ModelResourceLocation.STANDALONE_VARIANT);
	public static final ModelResourceLocation MODEL_2 = new ModelResourceLocation(MagiaLucisApi.id("block/sunlight_focus_2"), ModelResourceLocation.STANDALONE_VARIANT);
	public static final ModelResourceLocation MODEL_3 = new ModelResourceLocation(MagiaLucisApi.id("block/sunlight_focus_3"), ModelResourceLocation.STANDALONE_VARIANT);

	@SubscribeEvent
	public static void registerAdditionalModel(ModelEvent.RegisterAdditional event) {
		event.register(MODEL_1);
		event.register(MODEL_2);
		event.register(MODEL_3);
	}
}
