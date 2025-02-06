package gurumirum.magialucis.utils;

import gurumirum.magialucis.mixin.AbstractContainerMenuInvoker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class QuickMoveLogic {
	public static Builder builder(int containerSlots) {
		return new Builder(containerSlots);
	}

	private final int containerSlots;
	private final List<InsertionLogic> insertionLogics;

	private QuickMoveLogic(int containerSlots, List<InsertionLogic> insertionLogics) {
		this.containerSlots = containerSlots;
		this.insertionLogics = insertionLogics;
	}

	public @NotNull ItemStack perform(AbstractContainerMenu menu, @NotNull Player player, int index) {
		Slot slot = menu.slots.get(index);
		if (!slot.hasItem() || !slot.mayPickup(player)) return ItemStack.EMPTY;

		ItemStack slotItem = slot.getItem();
		ItemStack stack = slotItem.copy();

		AbstractContainerMenuInvoker invoker = (AbstractContainerMenuInvoker)menu;
		if (index < this.containerSlots) {
			if (!invoker.invokeMoveItemStackTo(slotItem, this.containerSlots, this.containerSlots + 36, true)) {
				return ItemStack.EMPTY;
			}
		} else {
			boolean processed = false;
			for (InsertionLogic logic : this.insertionLogics) {
				if (logic.condition.test(stack)) {
					if (!invoker.invokeMoveItemStackTo(slotItem, logic.start, logic.end, logic.reverse)) {
						if (logic.consumeTransfer) return ItemStack.EMPTY;
					} else {
						if (logic.consumeTransfer || slotItem.isEmpty()) processed = true;
					}
					break;
				}
			}

			if (!processed) {
				int quickSlotStart = this.containerSlots + 27;
				if (index < quickSlotStart) {
					if (!invoker.invokeMoveItemStackTo(slotItem, quickSlotStart, quickSlotStart + 9, false)) {
						return ItemStack.EMPTY;
					}
				} else {
					if (!invoker.invokeMoveItemStackTo(slotItem, this.containerSlots, quickSlotStart, false)) {
						return ItemStack.EMPTY;
					}
				}
			}
		}

		if (slotItem.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
		else slot.setChanged();

		if (slotItem.getCount() == stack.getCount()) return ItemStack.EMPTY;

		slot.onTake(player, stack);
		return stack;
	}

	public record InsertionLogic(@NotNull Predicate<ItemStack> condition, int start, int end, boolean reverse,
	                             boolean consumeTransfer) {}

	public static final class Builder {
		private final int containerSlots;
		private final List<InsertionLogic> insertionLogics = new ArrayList<>();

		public Builder(int containerSlots) {
			if (containerSlots < 0) throw new IllegalArgumentException("containerSlots < 0");
			this.containerSlots = containerSlots;
		}

		public Builder insertion(@NotNull Predicate<ItemStack> condition, int index) {
			return insertion(condition, index, 1);
		}

		public Builder insertion(@NotNull Predicate<ItemStack> condition, int start, int count) {
			return insertion(condition, start, count, false);
		}

		public Builder insertion(@NotNull Predicate<ItemStack> condition, int start, int count, boolean reverse) {
			return insertion(condition, start, count, reverse, true);
		}

		public Builder insertion(@NotNull Predicate<ItemStack> condition, int start, int count, boolean reverse, boolean consumeTransfer) {
			if (start < 0) throw new IllegalArgumentException("start < 0");
			if (count <= 0) throw new IllegalArgumentException("count <= 0");

			int end = start + count;
			if (end > this.containerSlots) throw new IllegalArgumentException("end > containerSlots");
			this.insertionLogics.add(new InsertionLogic(Objects.requireNonNull(condition), start, end, reverse, consumeTransfer));
			return this;
		}

		public QuickMoveLogic build() {
			return new QuickMoveLogic(this.containerSlots, List.copyOf(this.insertionLogics));
		}
	}
}
