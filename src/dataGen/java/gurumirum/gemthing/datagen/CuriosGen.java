package gurumirum.gemthing.datagen;

import gurumirum.gemthing.contents.item.wandbelt.WandBeltItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosDataProvider;

import java.util.concurrent.CompletableFuture;

import static gurumirum.gemthing.GemthingMod.MODID;
import static gurumirum.gemthing.GemthingMod.id;

public class CuriosGen extends CuriosDataProvider {
	public CuriosGen(PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<HolderLookup.Provider> registries) {
		super(MODID, output, fileHelper, registries);
	}

	@Override
	public void generate(HolderLookup.Provider registries, ExistingFileHelper fileHelper) {
		createSlot(WandBeltItem.CURIO_SLOT)
				.size(1)
				.order(50000)
				.icon(id("slot/empty_slot_wand_belt"));

		createEntities("player")
				.addPlayer()
				.addSlots(WandBeltItem.CURIO_SLOT);
	}
}
