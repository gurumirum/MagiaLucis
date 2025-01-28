package gurumirum.magialucis.datagen;

import gurumirum.magialucis.MagiaLucisMod;
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
				MagiaLucisMod.id("light_0"),
				MagiaLucisMod.id("light_1"));
	}
}
