package gurumirum.magialucis.impl.field;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class FieldInstance {
	private final Field field;

	private final Map<BlockPos, FieldElement> elements = new Object2ObjectOpenHashMap<>();
	private final Map<BlockPos, FieldElement> elementsView = Collections.unmodifiableMap(this.elements);

	private final Set<BlockPos> notifyPowerChangedOnNextUpdate = new ObjectOpenHashSet<>();

	private boolean dirty;

	public FieldInstance(@NotNull Field field) {
		this.field = Objects.requireNonNull(field);
	}

	public @NotNull Field field() {
		return this.field;
	}

	public @NotNull @UnmodifiableView Map<BlockPos, FieldElement> elements() {
		return this.elementsView;
	}

	public @NotNull FieldElement add(@NotNull BlockPos pos) {
		FieldElement e = new FieldElement(this, pos.immutable());
		this.elements.put(e.pos(), e);
		this.dirty = true;
		return e;
	}

	public void remove(@NotNull BlockPos pos) {
		this.elements.remove(pos);
		this.dirty = true;
	}

	public @Nullable FieldElement elementAt(@NotNull BlockPos pos) {
		return this.elements.get(pos);
	}

	public double value(@NotNull BlockPos pos) {
		return value(pos.getX(), pos.getY(), pos.getZ());
	}
	public double value(double x, double y, double z) {
		return value(Mth.floor(x), Mth.floor(y), Mth.floor(z));
	}

	public double value(int x, int y, int z) {
		double sum = 0;
		for (FieldElement e : this.elements.values()) {
			sum += FieldMath.getInfluence(this.field, e, x, y, z) * e.power();
		}
		return sum;
	}

	public double influenceSum(@NotNull BlockPos pos) {
		return influenceSum(pos.getX(), pos.getY(), pos.getZ());
	}
	public double influenceSum(double x, double y, double z) {
		return influenceSum(Mth.floor(x), Mth.floor(y), Mth.floor(z));
	}

	public double influenceSum(int x, int y, int z) {
		double sum = 0;
		for (FieldElement e : this.elements.values()) {
			sum += FieldMath.getInfluence(this.field, e, x, y, z);
		}
		return sum;
	}

	void notifyPowerChangedOnNextUpdate(BlockPos pos) {
		this.notifyPowerChangedOnNextUpdate.add(pos);
	}

	void update() {
		if (!this.dirty) return;
		this.dirty = false;

		if (this.field.hasInterference()) {
			for (FieldElement e : this.elements.values()) {
				double prevSum = e.influenceSumCache;
				double newSum = influenceSum(e.pos());
				if (prevSum == newSum) continue;
				e.influenceSumCache = newSum;
				e.setPower(FieldMath.power(this.field.interferenceThreshold(), newSum));
			}
		}

		this.notifyPowerChangedOnNextUpdate.forEach(pos -> {
			FieldElement element = this.elements.get(pos);
			if (element != null) element.broadcastPowerChanged();
		});
		this.notifyPowerChangedOnNextUpdate.clear();
	}
}
