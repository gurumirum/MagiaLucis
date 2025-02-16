package gurumirum.magialucis.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosDataProvider;

import java.util.concurrent.CompletableFuture;

import static gurumirum.magialucis.api.MagiaLucisApi.MODID;
import static gurumirum.magialucis.api.MagiaLucisApi.id;
import static gurumirum.magialucis.contents.ModCurioSlots.*;

public class CuriosGen extends CuriosDataProvider {
	private static final int BASE_ORDER = 2420;

	public CuriosGen(PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<HolderLookup.Provider> registries) {
		super(MODID, output, fileHelper, registries);
	}

	@Override
	public void generate(HolderLookup.Provider registries, ExistingFileHelper fileHelper) {
		createSlot(HEADWEAR)
				.size(1)
				.order(BASE_ORDER)
				.icon(id("slot/empty_slot_headwear"));

		createSlot(NECKLACE)
				.size(1)
				.order(BASE_ORDER + 1)
				.icon(id("slot/empty_slot_necklace"));

		createSlot(BRACELET)
				.size(2)
				.order(BASE_ORDER + 2)
				.icon(id("slot/empty_slot_bracelet"));

		createSlot(RING)
				.size(4)
				.order(BASE_ORDER + 3)
				.icon(id("slot/empty_slot_ring"));

		createSlot(WAND_BELT)
				.size(1)
				.order(BASE_ORDER + 4)
				.icon(id("slot/empty_slot_wand_belt"));

		createEntities("player")
				.addPlayer()
				.addSlots(HEADWEAR, NECKLACE, BRACELET, RING, WAND_BELT);
	}
}
