package gurumirum.magialucis.datagen;

import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.client.Textures;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
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
		atlas(Textures.AUGMENT_ATLAS_INFO)
		 		.addSource(new DirectoryLister("effect/magialucis_augments", ""));
	}
}
