package gurumirum.magialucis.contents.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TempleGuardian extends Monster {
	private static final EntityDataAccessor<Integer> DATA_ID_ATTACK_TARGET =
			SynchedEntityData.defineId(TempleGuardian.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> ATTACK_COOLDOWN_FLAG =
			SynchedEntityData.defineId(TempleGuardian.class, EntityDataSerializers.BOOLEAN);

	private static final float RANGE = 15;
	private static final int ATTACK_CHARGE_DURATION = 20 * 5;
	private static final int ATTACK_COOLDOWN = 20 * 5;

	private static final float ROTATION_BASE = 10;
	private static final float ROTATION_BASE_ATTACK = 0.25f;
	private static final float ROTATION_BASE_COOLDOWN = 5;
	private static final float ROTATION_HEAD_ATTACK = 0.5f;
	private static final float ROTATION_HEAD_RESET = 5;
	private static final int HEAD_IDLE_DURATION = 20 * 2;

	@Nullable
	private LivingEntity clientSideCachedAttackTarget;
	private int clientSideAttackTime;

	public float clientSideRingRotationO;
	public float clientSideRingRotation;

	public float clientSideHeadRotationO;
	public float clientSideHeadRotation;

	public int clientSideIdleCounter;

	private int attackCooldown;

	public TempleGuardian(EntityType<? extends TempleGuardian> entityType, Level level) {
		super(entityType, level);
		setPersistenceRequired();
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 25)
				.add(Attributes.MOVEMENT_SPEED, 0)
				.add(Attributes.ATTACK_DAMAGE, 1)
				.add(Attributes.STEP_HEIGHT, 1);
	}

	@Override
	protected void registerGoals() {
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class,
				1, true, false, this::chooseTarget));
		this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, RANGE + 3));
		this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(4, new AttackGoal());
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public void knockback(double strength, double x, double z) {}

	@Override
	protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_ID_ATTACK_TARGET, 0);
		builder.define(ATTACK_COOLDOWN_FLAG, false);
	}

	protected void setActiveAttackTarget(int id) {
		this.entityData.set(DATA_ID_ATTACK_TARGET, id);
	}

	public boolean hasActiveAttackTarget() {
		return this.entityData.get(DATA_ID_ATTACK_TARGET) != 0;
	}

	public boolean hasAttackCooldown() {
		return this.entityData.get(ATTACK_COOLDOWN_FLAG);
	}

	@Override
	public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (DATA_ID_ATTACK_TARGET.equals(key)) {
			this.clientSideAttackTime = 0;
			this.clientSideCachedAttackTarget = null;
		}
	}

	@Override
	protected Entity.@NotNull MovementEmission getMovementEmission() {
		return Entity.MovementEmission.NONE;
	}

	@Nullable
	public LivingEntity getActiveAttackTarget() {
		if (!this.hasActiveAttackTarget()) {
			return null;
		} else if (this.level().isClientSide) {
			if (this.clientSideCachedAttackTarget != null) {
				return this.clientSideCachedAttackTarget;
			} else {
				Entity entity = this.level().getEntity(this.entityData.get(DATA_ID_ATTACK_TARGET));
				if (entity instanceof LivingEntity) {
					this.clientSideCachedAttackTarget = (LivingEntity)entity;
					return this.clientSideCachedAttackTarget;
				} else {
					return null;
				}
			}
		} else {
			return this.getTarget();
		}
	}

	@Override
	public void aiStep() {
		super.aiStep();

		if (level().isClientSide) {
			this.clientSideRingRotationO = this.clientSideRingRotation;
			this.clientSideHeadRotationO = this.clientSideHeadRotation;

			float baseRotation = ROTATION_BASE;
			float headRotation = 0;

			if (hasAttackCooldown()) {
				baseRotation = ROTATION_BASE_COOLDOWN;
			} else if (hasActiveAttackTarget()) {
				if (this.clientSideAttackTime < ATTACK_CHARGE_DURATION) {
					this.clientSideAttackTime++;
				}

				baseRotation += this.clientSideAttackTime * ROTATION_BASE_ATTACK;
				headRotation = this.clientSideAttackTime * ROTATION_HEAD_ATTACK;

				LivingEntity target = getActiveAttackTarget();
				if (target != null) {
					getLookControl().setLookAt(target, 90.0F, 90.0F);
					getLookControl().tick();
				}
			} else {
				if (this.clientSideIdleCounter < HEAD_IDLE_DURATION) {
					this.clientSideIdleCounter++;
				}

				if (this.clientSideIdleCounter >= HEAD_IDLE_DURATION) {
					headRotation = this.clientSideHeadRotation >= 0 ?
							Math.max(-this.clientSideHeadRotation, -ROTATION_HEAD_RESET) :
							Math.min(this.clientSideHeadRotation, ROTATION_HEAD_RESET);
				}
			}

			this.clientSideRingRotation += baseRotation;
			this.clientSideHeadRotation += headRotation;
			this.clientSideRingRotation = Mth.wrapDegrees(this.clientSideRingRotation);
			this.clientSideHeadRotation = Mth.wrapDegrees(this.clientSideHeadRotation);
		} else {
			if (this.attackCooldown > 0) {
				this.attackCooldown--;
				this.entityData.set(ATTACK_COOLDOWN_FLAG, this.attackCooldown > 0);
			}
		}
	}

	@Override
	public boolean canHoldItem(@NotNull ItemStack stack) {
		return false;
	}

	@Override
	protected boolean shouldDespawnInPeaceful() {
		return false;
	}

	private boolean chooseTarget(LivingEntity t) {
		return (t instanceof Player || (t instanceof Mob &&
				!(t instanceof TempleGuardian) &&
				!(t instanceof Creeper) &&
				!(t instanceof EnderMan))) &&
				t.distanceToSqr(this) <= RANGE * RANGE;
	}

	private class AttackGoal extends Goal {
		private int attackTime;

		@Override
		public boolean canUse() {
			TempleGuardian entity = TempleGuardian.this;
			if (entity.attackCooldown > 0) return false;

			LivingEntity target = entity.getTarget();
			return target != null && target.isAlive();
		}

		@Override
		public boolean canContinueToUse() {
			if (!super.canContinueToUse()) return false;

			TempleGuardian entity = TempleGuardian.this;
			LivingEntity target = entity.getTarget();

			return target != null && entity.distanceToSqr(target) <= RANGE * RANGE;
		}

		@Override
		public void start() {
			TempleGuardian entity = TempleGuardian.this;

			this.attackTime = -10;

			LivingEntity livingentity = entity.getTarget();
			if (livingentity != null) {
				entity.getLookControl().setLookAt(livingentity, 90.0F, 90.0F);
			}

			entity.hasImpulse = true;
		}

		@Override
		public void stop() {
			TempleGuardian.this.setActiveAttackTarget(0);
			TempleGuardian.this.setTarget(null);
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			TempleGuardian entity = TempleGuardian.this;

			LivingEntity target = entity.getTarget();
			if (target == null) return;

			entity.getNavigation().stop();
			entity.getLookControl().setLookAt(target, 90.0F, 90.0F);

			if (!entity.hasLineOfSight(target)) {
				entity.setTarget(null);
				return;
			}

			this.attackTime++;

			if (this.attackTime == 0) {
				entity.setActiveAttackTarget(target.getId());

				if (!entity.isSilent()) {
					// TODO sound? what's that
				}
			} else if (this.attackTime >= ATTACK_CHARGE_DURATION) {
				float f = 10f;

				if (entity.level().getDifficulty() == Difficulty.HARD) {
					f += 5f;
				}

				target.hurt(entity.damageSources().indirectMagic(entity, entity), f);
				entity.doHurtTarget(target);
				entity.setTarget(null);

				entity.attackCooldown = ATTACK_COOLDOWN;
			}

			super.tick();
		}
	}
}
