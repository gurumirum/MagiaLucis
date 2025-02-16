package gurumirum.magialucis.datagen;

import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.contents.ModParticles;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;

public class ParticleDescriptionGen extends ParticleDescriptionProvider {
	public ParticleDescriptionGen(PackOutput output, ExistingFileHelper fileHelper) {
		super(output, fileHelper);
	}

	@Override protected void addDescriptions() {
		spriteSet(ModParticles.LIGHT.get(),
				MagiaLucisApi.id("light_0"),
				MagiaLucisApi.id("light_1"));
	}
}
