package gurumirum.gemthing.datagen;

import gurumirum.gemthing.GemthingMod;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import static gurumirum.gemthing.GemthingMod.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public class Datagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		GemthingMod.LOGGER.debug("fuck you");
		PackOutput o = event.getGenerator().getPackOutput();
		ExistingFileHelper exf = event.getExistingFileHelper();

		boolean c = event.includeClient();

		event.getGenerator().addProvider(c, new ItemModelGen(o, exf));
	}
}
