package gurumirum.magialucis.contents.block.lux.lightloom;

import gurumirum.magialucis.api.MagiaLucisApi;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = MagiaLucisApi.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class LightLoomModels {
	private LightLoomModels() {}

	public static final ModelResourceLocation ITEM_BASE = new ModelResourceLocation(MagiaLucisApi.id("item/lightloom_base"), ModelResourceLocation.STANDALONE_VARIANT);

	@SubscribeEvent
	public static void registerAdditionalModel(ModelEvent.RegisterAdditional event) {
		event.register(ITEM_BASE);
	}
}
