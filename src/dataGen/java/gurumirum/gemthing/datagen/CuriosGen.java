package gurumirum.gemthing.datagen;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.contents.item.wandbelt.WandBeltItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosDataProvider;

import java.util.concurrent.CompletableFuture;

public class CuriosGen extends CuriosDataProvider {
	public CuriosGen(PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<HolderLookup.Provider> registries) {
		super(GemthingMod.MODID, output, fileHelper, registries);
	}

	@Override
	public void generate(HolderLookup.Provider registries, ExistingFileHelper fileHelper) {
		createSlot(WandBeltItem.CURIO_SLOT)
				.size(1)
				.order(50000);

		createEntities("player")
				.addPlayer()
				.addSlots(WandBeltItem.CURIO_SLOT);
	}
}
