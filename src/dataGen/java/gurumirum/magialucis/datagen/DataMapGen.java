package gurumirum.magialucis.datagen;

import gurumirum.magialucis.contents.*;
import gurumirum.magialucis.contents.data.AugmentSpecGen;
import gurumirum.magialucis.contents.data.GemStat;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public class DataMapGen extends DataMapProvider {
	public DataMapGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, lookupProvider);
	}

	@Override
	protected void gather() {
		Builder<GemStat, Item> b = builder(ModDataMaps.GEM_STAT);
		for (Gem gem : Gem.values()) {
			if (gem == Gem.BRIGHTSTONE) {
				b.add(ModItemTags.BRIGHTSTONES, new GemStat(gem), false);
			} else if (gem.hasTag()) {
				b.add(gem.tag(), new GemStat(gem), false);
			} else {
				gem.forEachItem(i -> b.add(BuiltInRegistries.ITEM.wrapAsHolder(i), new GemStat(gem), false));
			}
		}

		AugmentSpecGen.add(builder(ModDataMaps.AUGMENT_SPEC));
	}
}
