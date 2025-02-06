package gurumirum.magialucis.contents.recipe;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public sealed class ConsumptionRecord {
	private static final ConsumptionRecord none = new ConsumptionRecord();

	public static ConsumptionRecord none() {
		return none;
	}

	public static Mutable create() {
		return new Mutable();
	}

	public static ConsumptionRecord consumeAllByOne(@NotNull RecipeInput input) {
		return consumeAll(input, 1);
	}

	public static ConsumptionRecord consumeAll(@NotNull RecipeInput input, int max) {
		var consumptions = new ConsumptionRecord.Mutable();
		for (int i = 0; i < input.size(); i++) {
			consumptions.add(i, Math.min(max, input.getItem(i).getCount()));
		}
		return consumptions;
	}

	protected final Int2IntMap map = new Int2IntOpenHashMap();

	public ConsumptionRecord() {
		this.map.defaultReturnValue(0);
	}

	public ConsumptionRecord(@NotNull ConsumptionRecord copyFrom) {
		this();
		this.map.putAll(copyFrom.map());
	}

	public @NotNull @UnmodifiableView Int2IntMap map() {
		return Int2IntMaps.unmodifiable(this.map);
	}

	public int get(int index) {
		return this.map.get(index);
	}

	public @NotNull ConsumptionRecord immutable() {
		return this;
	}

	public boolean apply(@NotNull IItemHandler itemHandler) {
		for (var e : this.map.int2IntEntrySet()) {
			int index = e.getIntKey();
			if (index < 0 || index >= itemHandler.getSlots()) return false;

			int count = e.getIntValue();
			if (itemHandler.getStackInSlot(index).getCount() < count) return false;
		}

		for (var e : this.map.int2IntEntrySet()) {
			itemHandler.extractItem(e.getIntKey(), e.getIntValue(), false);
		}

		return true;
	}

	public static final class Mutable extends ConsumptionRecord {
		public void add(int index, int amount) {
			if (index < 0) throw new IndexOutOfBoundsException("index < 0");
			if (amount < 0) throw new IndexOutOfBoundsException("amount < 0");

			if (amount == 0) return;

			this.map.put(index, this.map.getOrDefault(index, 0) + amount);
		}

		@Override
		public @NotNull ConsumptionRecord immutable() {
			return new ConsumptionRecord(this);
		}
	}
}
