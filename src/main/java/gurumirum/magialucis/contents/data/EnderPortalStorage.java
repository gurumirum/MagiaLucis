package gurumirum.magialucis.contents.data;

import gurumirum.magialucis.capability.FixedItemStackHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.invoke.arg.ArgumentIndexOutOfBoundsException;

public class EnderPortalStorage extends FixedItemStackHandler {
	private @Nullable ContainerWrapper w1, w2, w3;

	public EnderPortalStorage() {
		super(3 * 9);
	}

	public @NotNull Container getOneRowWrapper() {
		if (this.w1 == null) this.w1 = new ContainerWrapper(9);
		return this.w1;
	}

	public @NotNull Container getTwoRowWrapper() {
		if (this.w2 == null) this.w2 = new ContainerWrapper(2 * 9);
		return this.w2;
	}

	public @NotNull Container getThreeRowWrapper() {
		if (this.w3 == null) this.w3 = new ContainerWrapper(3 * 9);
		return this.w3;
	}

	private final class ContainerWrapper implements Container {
		private final int size;

		public ContainerWrapper(int size) {
			this.size = size;
		}

		private void checkIndex(int slot) {
			if (slot < 0 || slot >= this.size) throw new ArgumentIndexOutOfBoundsException(slot);
		}

		@Override
		public int getContainerSize() {
			return this.size;
		}

		@Override
		public boolean isEmpty() {
			return false; // whoever calling this shit on my container needs to be put in rubber room
		}

		@Override
		public @NotNull ItemStack getItem(int slot) {
			checkIndex(slot);
			return getStackInSlot(slot);
		}

		@Override
		public @NotNull ItemStack removeItem(int slot, int amount) {
			checkIndex(slot);
			return extractItem(slot, amount, false);
		}

		@Override
		public @NotNull ItemStack removeItemNoUpdate(int slot) {
			checkIndex(slot);
			ItemStack ret = getStackInSlot(slot);
			setStackInSlot(slot, ItemStack.EMPTY);
			return ret;
		}

		@Override
		public void setItem(int slot, @NotNull ItemStack stack) {
			checkIndex(slot);
			setStackInSlot(slot, stack);
		}

		@Override
		public void setChanged() {}

		@Override
		public boolean stillValid(@NotNull Player player) {
			return true;
		}

		@Override
		public void clearContent() {
			for (int i = 0; i < this.size; i++) {
				setStackInSlot(i, ItemStack.EMPTY);
			}
		}
	}

	public static final class Serializer implements IAttachmentSerializer<CompoundTag, EnderPortalStorage> {
		@Override
		public @NotNull EnderPortalStorage read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag,
		                                        HolderLookup.@NotNull Provider provider) {
			EnderPortalStorage storage = new EnderPortalStorage();
			storage.deserializeNBT(provider, tag);
			return storage;
		}

		@Override
		public @NotNull CompoundTag write(@NotNull EnderPortalStorage storage,
		                                  HolderLookup.@NotNull Provider provider) {
			return storage.serializeNBT(provider);
		}
	}
}
