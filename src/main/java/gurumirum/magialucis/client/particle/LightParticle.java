package gurumirum.magialucis.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LightParticle extends TextureSheetParticle {
	private final SpriteSet spriteSet;

	private LightParticle(ClientLevel level, SpriteSet spriteSet,
	                      double x, double y, double z,
	                      double xSpeed, double ySpeed, double zSpeed) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed);
		this.spriteSet = spriteSet;

		this.xd = xSpeed;
		this.yd = ySpeed;
		this.zd = zSpeed;
		this.gravity = 0;
		this.lifetime = 30;
		setSpriteFromAge(this.spriteSet);
		scale(0.25f);
	}

	@Override public @NotNull ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_LIT;
	}

	@Override public void tick() {
		setSpriteFromAge(this.spriteSet);
		this.setSprite(this.spriteSet.get(this.age / 10 % 2, 1));
		super.tick();
		// this.xd *= 0.95f;
		// this.yd *= 0.95f;
		// this.zd *= 0.95f;
	}

	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public Provider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public @Nullable Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level,
		                                         double x, double y, double z,
		                                         double xSpeed, double ySpeed, double zSpeed) {
			return new LightParticle(level, this.spriteSet, x, y, z, xSpeed, ySpeed, zSpeed);
		}
	}
}
