package gurumirum.gemthing.contents.item.wandbag;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.contents.ModItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerCopySlot;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static gurumirum.gemthing.client.SharedGUI.PLAYER_INV_HEIGHT;
import static gurumirum.gemthing.client.SharedGUI.PLAYER_INV_WIDTH;

public class WandBagMenu extends AbstractContainerMenu {
	public static final int WAND_BAG_WIDTH = 18 * 9 + (16 - 9) * 2;
	public static final int WAND_BAG_HEIGHT = 18 * 2 + (16 - 9) * 2;

	public static final int WIDTH = Math.max(PLAYER_INV_WIDTH, WAND_BAG_WIDTH);
	public static final int HEIGHT = WAND_BAG_HEIGHT +
			11 + // inventory label
			PLAYER_INV_HEIGHT;

	public static final int PLAYER_INV_LABEL_Y = WAND_BAG_HEIGHT;
	public static final int PLAYER_INV_Y = PLAYER_INV_LABEL_Y + 11;

	private static final int WAND_BAG_SLOT_UPPER_ROW_Y = 8;
	private static final int WAND_BAG_SLOT_LOWER_ROW_Y = WAND_BAG_SLOT_UPPER_ROW_Y + 18;
	private static final int WAND_BAG_SLOT_SINGLE_ROW_Y = (WAND_BAG_SLOT_UPPER_ROW_Y + WAND_BAG_SLOT_LOWER_ROW_Y) / 2;

	private final List<WandBagSlot> wandBagSlots;
	private final DataSlot selectedIndex = DataSlot.standalone();

	private final IItemHandlerModifiable wandBagInv;
	private boolean oneRow;
	private boolean updateOneRow = true;

	public WandBagMenu(int containerId, Inventory playerInv) {
		super(Contents.WANG_BAG_MENU.get(), containerId);
		this.wandBagSlots = setup(playerInv, this.wandBagInv = new ClientSideItemHandler(), -1);
	}

	public WandBagMenu(int containerId, Inventory playerInv, IItemHandlerModifiable wandBagInv, int selectedIndex) {
		super(Contents.WANG_BAG_MENU.get(), containerId);
		this.wandBagSlots = setup(playerInv, this.wandBagInv = new ServerSideItemHandlerWrapper(wandBagInv), selectedIndex);
	}

	private List<WandBagSlot> setup(Inventory playerInv, IItemHandlerModifiable wandBagInv, int selectedIndex) {
		addDataSlot(this.selectedIndex).set(selectedIndex);

		List<WandBagSlot> wandBagSlots = new ArrayList<>();

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 9; j++) {
				int index = j + i * 9;
				int x = 8 + j * 18;
				int y = WAND_BAG_SLOT_UPPER_ROW_Y + i * 18;
				WandBagSlot slot = new WandBagSlot(wandBagInv, index, x, y);
				wandBagSlots.add(slot);
				addSlot(slot);
			}
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, PLAYER_INV_Y + 8 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlot(new Slot(playerInv, i, 8 + i * 18, PLAYER_INV_Y + 66));
		}

		return wandBagSlots;
	}

	public int selectedIndex() {
		return this.selectedIndex.get();
	}

	public List<? extends Slot> wandBagSlots() {
		return this.wandBagSlots;
	}

	public boolean hasOneRow() {
		return this.oneRow;
	}

	public void updateOneRow() {
		if (!this.updateOneRow) return;
		this.updateOneRow = false;

		boolean oneRow = computeOneRow();
		if (this.oneRow == oneRow) return;

		GemthingMod.LOGGER.info("one row = " + oneRow);
		this.oneRow = oneRow;

		for (int i = 0; i < 18; i++) {
			WandBagSlot slot = this.wandBagSlots.get(i);
			if (oneRow) {
				if (i < 9) slot.y = WAND_BAG_SLOT_SINGLE_ROW_Y;
				else slot.active = false;
			} else {
				slot.y = WAND_BAG_SLOT_UPPER_ROW_Y + 18 * (i / 9);
				slot.active = true;
			}
		}
	}

	private boolean computeOneRow() {
		// if the selected index is on 2nd row or the rightmost slot of 1st row, there should be two rows
		if (this.selectedIndex.get() >= 8) return false;

		// if any item is in 2nd row then there should be two rows
		for (int i = 9; i < 18; i++) {
			if (!this.wandBagInv.getStackInSlot(i).isEmpty()) return false;
		}

		// if all upper row is full then there should be two rows
		for (int i = 0; i < 9; i++) {
			if (this.wandBagInv.getStackInSlot(i).isEmpty()) return true;
		}

		return false;
	}

	@Override
	public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot.hasItem()) {
			ItemStack slotItem = slot.getItem();
			stack = slotItem.copy();

			if (index < 18) {
				if (!moveItemStackTo(slotItem, 18, 54, true)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (stack.is(ModItemTags.WANDS)) {
					if (moveItemStackTo(slotItem, 0, 18, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index < 45) {
					if (!moveItemStackTo(slotItem, 45, 54, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!moveItemStackTo(slotItem, 18, 45, false)) {
					return ItemStack.EMPTY;
				}
			}

			slot.onTake(player, stack);
		}

		return stack;
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return true;
	}

	private static class WandBagSlot extends ItemHandlerCopySlot {
		private boolean active = true;

		public WandBagSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
			super(itemHandler, index, xPosition, yPosition);
		}

		@Override
		public boolean isActive() {
			return this.active;
		}
	}

	private class ServerSideItemHandlerWrapper implements IItemHandlerModifiable {
		private final IItemHandlerModifiable delegate;

		public ServerSideItemHandlerWrapper(IItemHandlerModifiable delegate) {
			this.delegate = delegate;
		}

		@Override
		public void setStackInSlot(int slot, @NotNull ItemStack stack) {
			delegate.setStackInSlot(slot, stack);
			updateOneRow = true;
		}
		@Override
		public int getSlots() {
			return delegate.getSlots();
		}
		@Override
		public @NotNull ItemStack getStackInSlot(int slot) {
			return delegate.getStackInSlot(slot);
		}
		@Override
		public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
			ItemStack result = this.delegate.insertItem(slot, stack, simulate);
			updateOneRow = true;
			return result;
		}
		@Override
		public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
			ItemStack result = delegate.extractItem(slot, amount, simulate);
			updateOneRow = true;
			return result;
		}
		@Override
		public int getSlotLimit(int slot) {
			return delegate.getSlotLimit(slot);
		}
		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			return delegate.isItemValid(slot, stack);
		}
	}

	private class ClientSideItemHandler extends ItemStackHandler {
		public ClientSideItemHandler() {
			super(18);
		}

		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			return stack.isEmpty() || stack.is(ModItemTags.WANDS);
		}

		@Override
		protected void onContentsChanged(int slot) {
			updateOneRow = true;
		}
	}
}
