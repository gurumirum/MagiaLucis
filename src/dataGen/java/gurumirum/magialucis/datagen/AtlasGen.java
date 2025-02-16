package gurumirum.magialucis.datagen;

import gurumirum.magialucis.api.MagiaLucisApi;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.textures.NamespacedDirectoryLister;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

import java.util.concurrent.CompletableFuture;

public class AtlasGen extends SpriteSourceProvider {
	public AtlasGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
	                ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, MagiaLucisApi.MODID, existingFileHelper);
	}

	@Override
	protected void gather() {
		atlas(InventoryMenu.BLOCK_ATLAS)
				.addSource(new NamespacedDirectoryLister(MagiaLucisApi.MODID, "block/matrix", ""));
	}
}
