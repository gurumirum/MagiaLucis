package gurumirum.magialucis.datagen;

import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.contents.Gem;
import gurumirum.magialucis.contents.ModItemTags;
import gurumirum.magialucis.impl.GemStat;
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
		Builder<GemStat, Item> gemStatData = builder(Contents.GEM_STAT_DATA_MAP_TYPE);

		for (Gem gem : Gem.values()) {
			if (gem == Gem.BRIGHTSTONE) {
				gemStatData.add(ModItemTags.BRIGHTSTONES, new GemStat(gem), false);
			} else if (gem.hasTag()) {
				gemStatData.add(gem.tag(), new GemStat(gem), false);
			} else {
				gem.forEachItem(i -> gemStatData.add(BuiltInRegistries.ITEM.wrapAsHolder(i), new GemStat(gem), false));
			}
		}
	}
}
