package gurumirum.magialucis.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class FixedItemStackHandler extends ItemStackHandler {
	public FixedItemStackHandler(int size) {
		super(size);
	}
	public FixedItemStackHandler(NonNullList<ItemStack> stacks) {
		super(stacks);
	}

	// no-op
	// overridden to prevent resizing of the inventory via code / loading fucked up data and breaking everything
	// seriously who the fuck wants this
	@Override
	public void setSize(int size) {}

	@Override
	public @NotNull CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
		CompoundTag tag = super.serializeNBT(provider);
		tag.remove("Size"); // Don't serialize the container size from the start??
		return tag;
	}
}
