package gurumirum.magialucis.datagen;

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

import static gurumirum.magialucis.MagiaLucisMod.MODID;

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
		event.getGenerator().addProvider(c, new ParticleDescriptionGen(o, exf));
		event.getGenerator().addProvider(c, new AtlasGen(o, l, exf));

		var l2 = event.getGenerator().addProvider(s, (DataProvider.Factory<DatapackBuiltinEntriesProvider>)output ->
				new DatapackBuiltinEntriesProvider(output, l, DatapackEntryGen.getEntries(),
						Set.of(MODID))).getRegistryProvider();

		event.getGenerator().addProvider(s, new BiomeTagGen(o, l2, exf));
		var blockTags = event.getGenerator().addProvider(s, new BlockTagGen(o, l2, exf));
		event.getGenerator().addProvider(s, new ItemTagGen(o, l2, blockTags.contentsGetter(), exf));
		event.getGenerator().addProvider(s, new DamageTypeTagGen(o, l2, exf));
		event.getGenerator().addProvider(s, new RecipeGen(o, l2));
		event.getGenerator().addProvider(s, new LootGen(o, l2));
		event.getGenerator().addProvider(s, new CuriosGen(o, exf, l2));
		event.getGenerator().addProvider(s, new LootModifierGen(o, l2));
		event.getGenerator().addProvider(s, new DataMapGen(o, l2));
	}
}
