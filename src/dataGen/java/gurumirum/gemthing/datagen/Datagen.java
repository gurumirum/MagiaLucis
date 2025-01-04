package gurumirum.gemthing.datagen;

import gurumirum.gemthing.GemthingMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static gurumirum.gemthing.GemthingMod.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public class Datagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		PackOutput o = event.getGenerator().getPackOutput();
		CompletableFuture<HolderLookup.Provider> l = event.getLookupProvider();
		ExistingFileHelper exf = event.getExistingFileHelper();

		boolean c = event.includeClient();
		boolean s = event.includeServer();

		event.getGenerator().addProvider(c, new BlockStateGen(o, exf));
		event.getGenerator().addProvider(c, new ItemModelGen(o, exf));

		var blockTags = event.getGenerator().addProvider(s, new BlockTagGen(o, l, exf));
		GemthingMod.LOGGER.error(blockTags.getName());
		event.getGenerator().addProvider(s, new ItemTagGen(o, l, blockTags.contentsGetter(), exf));
		event.getGenerator().addProvider(s, new RecipeGen(o, l));
		event.getGenerator().addProvider(s, (DataProvider.Factory<DatapackBuiltinEntriesProvider>)output ->
				new DatapackBuiltinEntriesProvider(output, l, DatapackEntryGen.getEntries(),
						Set.of(MODID)));
		event.getGenerator().addProvider(s, new CuriosGen(o, exf, l));
	}
}
