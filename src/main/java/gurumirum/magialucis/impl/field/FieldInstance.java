package gurumirum.magialucis.impl.field;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.function.DoubleConsumer;

public class FieldInstance {
	private final Field field;

	private final Map<BlockPos, FieldElement> elements = new Object2ObjectOpenHashMap<>();
	private final Map<BlockPos, FieldElement> elementsView = Collections.unmodifiableMap(this.elements);

	private final List<FieldChangedListener> fieldChanged = new ArrayList<>();
	private final Map<BlockPos, List<DoubleTracker>> powerChanged = new Object2ObjectOpenHashMap<>();

	private boolean fieldChangedInvalidated;
	private final Set<BlockPos> powerChangedInvalidated = new ObjectOpenHashSet<>();

	private boolean dirty;

	@Nullable FieldManager manager;

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
		return this.elements.computeIfAbsent(pos.immutable(), p -> {
			FieldElement element = new FieldElement(this, p);
			setDirty();
			dispatchFieldChanged(p, element, false);
			return element;
		});
	}

	public void remove(@NotNull BlockPos pos) {
		FieldElement element = this.elements.remove(pos);
		if (element == null) return;

		setDirty();
		dispatchFieldChanged(pos, element, true);
		dispatchPowerChange(pos, 0);
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
			sum += FieldMath.getInfluence(e, x, y, z) * e.power();
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
			sum += FieldMath.getInfluence(e, x, y, z);
		}
		return sum;
	}

	public boolean hasInfluence(@NotNull BlockPos pos) {
		return hasInfluence(pos.getX(), pos.getY(), pos.getZ());
	}
	public boolean hasInfluence(double x, double y, double z) {
		return hasInfluence(Mth.floor(x), Mth.floor(y), Mth.floor(z));
	}

	public boolean hasInfluence(int x, int y, int z) {
		for (FieldElement e : this.elements.values()) {
			if (FieldMath.getInfluence(e, x, y, z) > 0) return true;
		}
		return false;
	}

	public boolean isEmpty() {
		return this.elements.isEmpty();
	}

	public FieldListener listener() {
		return new FieldListener(this);
	}

	void listenFieldChanged(@NotNull FieldListener listener, @NotNull FieldChanged changed) {
		this.fieldChanged.add(new FieldChangedListener(listener, changed));
	}

	void listenPowerChanged(@NotNull FieldListener listener, @NotNull BlockPos pos, @NotNull DoubleConsumer changed) {
		FieldElement element = this.elements.get(pos);

		List<DoubleTracker> list = this.powerChanged.computeIfAbsent(pos, p -> new ArrayList<>());
		list.add(new DoubleTracker(listener, changed, element != null ? element.power() : 0));
	}

	void fieldChangedInvalidated() {
		this.fieldChangedInvalidated = true;
		this.dirty = true;
	}

	void powerChangedInvalidated(@NotNull Collection<BlockPos> pos) {
		this.powerChangedInvalidated.addAll(pos);
		this.dirty = true;
	}

	void update() {
		if (!this.dirty) return;
		this.dirty = false;

		if (this.fieldChangedInvalidated) {
			this.fieldChangedInvalidated = false;
			this.fieldChanged.removeIf(EventListener::isInvalid);
		}

		for (BlockPos pos : this.powerChangedInvalidated) {
			List<DoubleTracker> trackers = this.powerChanged.get(pos);
			if (trackers != null && trackers.removeIf(EventListener::isInvalid) && trackers.isEmpty()) {
				this.powerChanged.remove(pos);
			}
		}
		this.powerChangedInvalidated.clear();

		if (this.field.hasInterference()) {
			for (FieldElement e : this.elements.values()) {
				double prevSum = e.influenceSumCache;
				double newSum = influenceSum(e.pos());
				if (prevSum == newSum) continue;
				e.influenceSumCache = newSum;
				double power = FieldMath.power(this.field.interferenceThreshold(), newSum);
				e.setPower(power);
				dispatchPowerChange(e.pos(), power);
			}
		}
	}

	private void dispatchFieldChanged(@NotNull BlockPos pos, @NotNull FieldElement element, boolean removed) {
		for (FieldChangedListener l : this.fieldChanged) {
			if (l.isValid()) l.changed.onFieldChanged(pos, element, removed);
		}
	}

	private void dispatchPowerChange(@NotNull BlockPos pos, double value) {
		List<DoubleTracker> trackers = this.powerChanged.get(pos);
		if (trackers != null) {
			for (DoubleTracker t : trackers) {
				if (t.isValid()) t.set(value);
			}
		}
	}

	public void setDirty() {
		this.dirty = true;
		if (this.manager != null) {
			this.manager.setDirty();
		}
	}

	public void save(@NotNull CompoundTag tag) {
		var list = new ListTag();
		for (var e : this.elements.entrySet()) {
			CompoundTag tag2 = new CompoundTag();
			tag2.putInt("x", e.getKey().getX());
			tag2.putInt("y", e.getKey().getY());
			tag2.putInt("z", e.getKey().getZ());
			list.add(tag2);
		}
		tag.put("elements", list);
	}

	public FieldInstance(@NotNull Field field, @NotNull CompoundTag tag) {
		this(field);

		ListTag list = tag.getList("elements", ListTag.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			CompoundTag tag2 = list.getCompound(i);
			BlockPos pos = new BlockPos(tag2.getInt("x"), tag2.getInt("y"), tag2.getInt("z"));
			this.elements.put(pos, new FieldElement(this, pos, tag2));
		}
	}

	private static sealed abstract class EventListener {
		public final FieldListener listener;

		private EventListener(@NotNull FieldListener listener) {
			this.listener = Objects.requireNonNull(listener);
		}

		public boolean isValid() {
			return this.listener.isValid();
		}

		public boolean isInvalid() {
			return !this.listener.isValid();
		}
	}

	private static final class FieldChangedListener extends EventListener {
		private final FieldChanged changed;

		private FieldChangedListener(@NotNull FieldListener listener, @NotNull FieldChanged changed) {
			super(listener);
			this.changed = Objects.requireNonNull(changed);
		}
	}

	private static final class DoubleTracker extends EventListener {
		private final DoubleConsumer changed;
		private double value;

		private DoubleTracker(@NotNull FieldListener listener, @NotNull DoubleConsumer changed, double value) {
			super(listener);
			this.changed = Objects.requireNonNull(changed);
			this.value = value;
		}

		public void set(double value) {
			if (!(this.value != value)) return;
			this.value = value;
			this.changed.accept(value);
		}
	}
}
