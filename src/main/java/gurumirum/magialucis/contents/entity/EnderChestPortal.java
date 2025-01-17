package gurumirum.magialucis.contents.entity;

import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.contents.Wands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class EnderChestPortal extends Entity {
	private @Nullable UUID owner;
	private int life;

	public EnderChestPortal(EntityType<? extends EnderChestPortal> type, Level level) {
		super(type, level);
	}

	public EnderChestPortal(Level level) {
		super(Contents.ENDER_CHEST_PORTAL.get(), level);
	}

	public int life() {
		return life;
	}

	public void setOwnerUuid(@Nullable UUID p) {
		this.owner = p;
	}

	public void setLife(int life) {
		this.life = life;
	}

	@Override
	public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
		if (player.isSecondaryUseActive()) return InteractionResult.PASS;

		Level level = level();
		if (level.isClientSide) return InteractionResult.SUCCESS;

		if (this.owner == null) return InteractionResult.FAIL;

		Player ownerPlayer = level.getPlayerByUUID(this.owner);
		if (ownerPlayer == null) return InteractionResult.FAIL;

		PlayerEnderChestContainer container = ownerPlayer.getEnderChestInventory();

		Vec3 pos = this.position();
		Vec3 playerDirection = ownerPlayer.position().subtract(pos).normalize().scale(0.5);
		UUID portalId = this.getUUID();

		player.openMenu(new SimpleMenuProvider(
				(id, inventory, p) -> new ChestMenu(MenuType.GENERIC_9x3, id, inventory, container, 3) {
					@Override
					public boolean stillValid(@NotNull Player player) {
						return EnderChestPortal.this.isAlive();
					}

					@Override
					public void removed(@NotNull Player player) {
						for (int i = 0; i < container.getItems().size(); i++) {
							ItemStack stack = container.getItem(i);
							if (stack.is(Wands.ENDER_WAND.asItem()) &&
									Objects.equals(stack.get(Contents.PORTAL_UUID), portalId)) {

								ItemEntity itemEntity = new ItemEntity(level, pos.x, pos.y, pos.z, stack);
								itemEntity.setDeltaMovement(playerDirection);
								if (level.addFreshEntity(itemEntity)) {
									container.setItem(i, ItemStack.EMPTY);
								}
							}
						}
					}
				},
				Component.translatable("container.enderchest")));

		return InteractionResult.CONSUME;
	}

	@Override
	public void tick() {
		super.tick();

		if (!isAlive()) return;

		if (!level().isClientSide) {
			if (this.life <= 0) {
				kill();
				return;
			} else this.life--;
		}

		Vec3 delta = getDeltaMovement();
		move(MoverType.SELF, delta);

		setDeltaMovement(delta.multiply(.5, .5, .5));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		if (compound.contains("owner")) this.owner = compound.getUUID("owner");
	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
		if (this.owner != null) compound.putUUID("owner", this.owner);
	}

	@Override
	public boolean isPickable() {
		return true;
	}
}
