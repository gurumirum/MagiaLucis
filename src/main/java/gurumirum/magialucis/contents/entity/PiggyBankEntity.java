package gurumirum.magialucis.contents.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class PiggyBankEntity extends Entity {
	private UUID owner;
	private static final EntityDataAccessor<Optional<UUID>> accessor = SynchedEntityData.defineId(PiggyBankEntity.class, EntityDataSerializers.OPTIONAL_UUID);

	public PiggyBankEntity(EntityType<?> pType, Level pLevel) {
		super(pType, pLevel);
	}

	public void setOwnerUuid(UUID p) {this.owner = p;}

	@Override
	public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
		super.interact(player, hand);
		if (owner == null) return InteractionResult.FAIL;
		Level level = level();
		Player ownerPlayer = level.getPlayerByUUID(owner);
		if (ownerPlayer == null) return InteractionResult.FAIL;
		PlayerEnderChestContainer playerenderchestcontainer = ownerPlayer.getEnderChestInventory();
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			player.openMenu(new SimpleMenuProvider((id, inventory, p) -> ChestMenu.threeRows(id, inventory, playerenderchestcontainer), Component.translatable("container.enderchest")));
			player.awardStat(Stats.OPEN_ENDERCHEST);
			PiglinAi.angerNearbyPiglins(player, true);
			return InteractionResult.CONSUME;
		}
	}

	@Override
	public void tick() {
		super.tick();
		this.move(MoverType.SELF, this.getDeltaMovement());
		if (tickCount < 40) this.setDeltaMovement(this.getDeltaMovement().add(this.getDeltaMovement().scale(-0.5)));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(accessor, Optional.empty()).build();
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		if (compound.contains("owner")) this.owner = compound.getUUID("owner");

	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
		if (owner != null) compound.putUUID("owner", owner);
	}

	@Override
	public boolean isPickable() {
		return true;
	}
}
