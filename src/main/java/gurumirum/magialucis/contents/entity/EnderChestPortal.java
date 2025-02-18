package gurumirum.magialucis.contents.entity;

import gurumirum.magialucis.contents.ModDataAttachments;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.ModEntities;
import gurumirum.magialucis.contents.Wands;
import gurumirum.magialucis.contents.data.EnderPortalStorage;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class EnderChestPortal extends Entity implements MenuProvider {
	private @Nullable UUID owner;
	private int life;
	private StorageTier storageTier = StorageTier.T0;
	private boolean collectItems;

	public EnderChestPortal(EntityType<? extends EnderChestPortal> type, Level level) {
		super(type, level);
	}

	public EnderChestPortal(Level level) {
		super(ModEntities.ENDER_CHEST_PORTAL.get(), level);
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

	public StorageTier storageTier() {
		return this.storageTier;
	}

	public void setStorageTier(StorageTier storageTier) {
		this.storageTier = storageTier;
	}

	public boolean collectItems() {
		return collectItems;
	}

	public void setCollectItems(boolean collectItems) {
		this.collectItems = collectItems;
	}

	@Override
	public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
		if (player.isSecondaryUseActive()) return InteractionResult.PASS;

		Level level = level();
		if (level.isClientSide) return InteractionResult.SUCCESS;

		if (this.owner == null) return InteractionResult.FAIL;

		Player ownerPlayer = level.getPlayerByUUID(this.owner);
		if (ownerPlayer == null) return InteractionResult.FAIL;

		player.openMenu(this);

		return InteractionResult.CONSUME;
	}

	@Override
	public void tick() {
		super.tick();

		if (!isAlive()) return;

		if (level().isClientSide) {
			int xm = random.nextInt(2) * 2 - 1;
			int zm = random.nextInt(2) * 2 - 1;
			level().addParticle(ParticleTypes.PORTAL,
					getX() + 0.25 * xm, getY() + random.nextFloat() - 0.5, getZ() + 0.25 * zm,
					random.nextFloat() * xm, (random.nextFloat() - 0.5) * 0.125, random.nextFloat() * zm);
		} else {
			if (this.life <= 0) {
				kill();
				return;
			} else this.life--;

			if (this.collectItems && this.tickCount % 10 == 0) {
				collectNearbyItems();
			}
		}

		Vec3 delta = getDeltaMovement();
		move(MoverType.SELF, delta);

		setDeltaMovement(delta.scale(.5));
	}

	private void collectNearbyItems() {
		if (this.owner == null) return;
		Player ownerPlayer = level().getPlayerByUUID(this.owner);
		if (ownerPlayer == null) return;

		IItemHandlerModifiable itemHandler = null;

		Vec3 center = position().add(0, getBbHeight() / 2, 0);
		for (ItemEntity item : level().getEntitiesOfClass(ItemEntity.class,
				new AABB(center, center).inflate(4),
				EntitySelector.ENTITY_STILL_ALIVE)) {
			ItemStack stack = item.getItem();

			if (stack.is(Wands.ENDER_WAND.asItem()) &&
					Objects.equals(stack.get(ModDataComponents.PORTAL_UUID), getUUID())) {
				continue;
			}

			if (itemHandler == null) {
				itemHandler = new InvWrapper(ownerPlayer.getEnderChestInventory());
				if (this.storageTier != StorageTier.T0) {
					itemHandler = new CombinedInvWrapper(itemHandler,
							new RangedWrapper(ownerPlayer.getData(ModDataAttachments.ENDER_PORTAL_STORAGE.get()),
									0, 9 * this.storageTier.extraRows()));
				}
			}

			ItemStack newStack = transfer(itemHandler, stack);
			if (newStack.isEmpty()) item.kill();
			else if (stack != newStack) item.setItem(newStack);
			else continue;
			// TODO particle here
		}
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		if (tag.contains("owner")) this.owner = tag.getUUID("owner");
		this.storageTier = StorageTier.values()[Math.clamp(tag.getByte("storageTier"), 0, 3)];
		this.collectItems = tag.getBoolean("collectItems");
	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
		if (this.owner != null) tag.putUUID("owner", this.owner);
		if (this.storageTier != StorageTier.T0) tag.putByte("storageTier", (byte)this.storageTier.ordinal());
		if (this.collectItems) tag.putBoolean("collectItems", true);
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	public @Nullable AbstractContainerMenu createMenu(
			int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
		Level level = level();

		if (this.owner == null) return null;
		Player ownerPlayer = level.getPlayerByUUID(this.owner);
		if (ownerPlayer == null) return null;

		StorageTier tier = this.storageTier;
		Container container = container(ownerPlayer, tier);

		Vec3 pos = position();
		Vec3 playerDirection = player.position().subtract(pos).normalize().scale(0.5);
		UUID portalId = getUUID();

		return new ChestMenu(tier.menuType(), containerId, playerInventory, container, tier.rows()) {
			@Override
			public boolean stillValid(@NotNull Player player) {
				return EnderChestPortal.this.isAlive();
			}

			@Override
			public void removed(@NotNull Player player) {
				for (int i = 0; i < container.getContainerSize(); i++) {
					ItemStack stack = container.getItem(i);
					if (stack.is(Wands.ENDER_WAND.asItem()) &&
							Objects.equals(stack.get(ModDataComponents.PORTAL_UUID), portalId)) {

						ItemEntity itemEntity = new ItemEntity(level, pos.x, pos.y, pos.z, stack);
						itemEntity.setDeltaMovement(playerDirection);
						if (level.addFreshEntity(itemEntity)) {
							container.setItem(i, ItemStack.EMPTY);
						}
					}
				}
			}
		};
	}

	private static ItemStack transfer(IItemHandlerModifiable itemHandler, ItemStack stack) {
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			if (itemHandler.getStackInSlot(i).isEmpty()) continue;

			stack = itemHandler.insertItem(i, stack, false);
			if (stack.isEmpty()) return ItemStack.EMPTY;
		}

		for (int i = 0; i < itemHandler.getSlots(); i++) {
			if (!itemHandler.getStackInSlot(i).isEmpty()) continue;

			stack = itemHandler.insertItem(i, stack, false);
			if (stack.isEmpty()) return ItemStack.EMPTY;
		}

		return stack;
	}

	private static Container container(@NotNull Player ownerPlayer, @NotNull StorageTier storageTier) {
		if (storageTier == StorageTier.T0) return ownerPlayer.getEnderChestInventory();

		EnderPortalStorage storage = ownerPlayer.getData(ModDataAttachments.ENDER_PORTAL_STORAGE.get());
		return new CompoundContainer(ownerPlayer.getEnderChestInventory(), storageTier.getContainer(storage));
	}

	public enum StorageTier {
		T0,
		T1,
		T2,
		T3;

		private MenuType<?> menuType() {
			return switch (this) {
				case T0 -> MenuType.GENERIC_9x3;
				case T1 -> MenuType.GENERIC_9x4;
				case T2 -> MenuType.GENERIC_9x5;
				case T3 -> MenuType.GENERIC_9x6;
			};
		}

		private int extraRows() {
			return switch (this) {
				case T0 -> 0;
				case T1 -> 1;
				case T2 -> 2;
				case T3 -> 3;
			};
		}

		private int rows() {
			return 3 + extraRows();
		}

		private int slots() {
			return rows() * 9;
		}

		private Container getContainer(EnderPortalStorage storage) {
			return switch (this) {
				case T0 -> throw new IllegalStateException();
				case T1 -> storage.getOneRowWrapper();
				case T2 -> storage.getTwoRowWrapper();
				case T3 -> storage.getThreeRowWrapper();
			};
		}
	}
}
