package gurumirum.magialucis.contents.recipe;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public final class ConsumptionRecord {
	private final Int2IntMap map = new Int2IntOpenHashMap();

	public @NotNull @UnmodifiableView Int2IntMap map() {
		return Int2IntMaps.unmodifiable(this.map);
	}

	public int get(int index) {
		return this.map.get(index);
	}

	public void add(int index, int amount) {
		if (index < 0) throw new IndexOutOfBoundsException("index < 0");
		if (amount < 0) throw new IndexOutOfBoundsException("amount < 0");

		this.map.put(index, this.map.getOrDefault(index, 0) + amount);
	}
}
