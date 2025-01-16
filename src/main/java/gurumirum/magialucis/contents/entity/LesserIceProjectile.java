package gurumirum.magialucis.contents.entity;

import gurumirum.magialucis.contents.Contents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class LesserIceProjectile extends ThrowableProjectile {
	public LesserIceProjectile(EntityType<? extends LesserIceProjectile> entityType, Level level) {
		super(entityType, level);
	}

	public LesserIceProjectile(double x, double y, double z, Level level) {
		super(Contents.LESSER_ICE_PROJECTILE.get(), x, y, z, level);
	}

	public LesserIceProjectile(LivingEntity shooter, Level level) {
		super(Contents.LESSER_ICE_PROJECTILE.get(), shooter, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == 3) {
			ParticleOptions o = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ICE.defaultBlockState());

			for (int i = 0; i < 4; i++) {
				level().addParticle(o, getX(), getY(), getZ(), 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	protected void onHitEntity(@NotNull EntityHitResult result) {
		Entity entity = result.getEntity();
		if (entity.hurt(damageSources().thrown(this, getOwner()), entity instanceof Blaze ? 5 : 1) &&
				entity instanceof LivingEntity livingEntity) {
			livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60));
		}
	}

	@Override
	protected void onHit(@NotNull HitResult result) {
		super.onHit(result);
		if (!level().isClientSide) {
			level().broadcastEntityEvent(this, (byte)3);
			discard();
		}
	}
}
