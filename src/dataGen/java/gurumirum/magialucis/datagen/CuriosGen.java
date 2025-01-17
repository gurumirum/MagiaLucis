package gurumirum.magialucis.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosDataProvider;

import java.util.concurrent.CompletableFuture;

import static gurumirum.magialucis.MagiaLucisMod.MODID;
import static gurumirum.magialucis.MagiaLucisMod.id;
import static gurumirum.magialucis.contents.ModCurioSlots.*;

public class CuriosGen extends CuriosDataProvider {
	private static final int BASE_ORDER = 2420;

	public CuriosGen(PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<HolderLookup.Provider> registries) {
		super(MODID, output, fileHelper, registries);
	}

	@Override
	public void generate(HolderLookup.Provider registries, ExistingFileHelper fileHelper) {
		createSlot(TIARA)
				.size(1)
				.order(BASE_ORDER);

		createSlot(NECKLACE)
				.size(1)
				.order(BASE_ORDER + 1);

		createSlot(BRACELET)
				.size(2)
				.order(BASE_ORDER + 2);

		createSlot(RING)
				.size(4)
				.order(BASE_ORDER + 3);

		createSlot(WAND_BELT)
				.size(1)
				.order(BASE_ORDER + 4)
				.icon(id("slot/empty_slot_wand_belt"));

		createEntities("player")
				.addPlayer()
				.addSlots(TIARA, NECKLACE, BRACELET, RING, WAND_BELT);
	}
}
