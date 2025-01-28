package gurumirum.magialucis.contents;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModParticles {
	private ModParticles(){}

	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LIGHT = Contents.PARTICLES.register("light",
			() -> new SimpleParticleType(false));

	public static void init(){}
}
