package gurumirum.magialucis.impl.field;

import gurumirum.magialucis.MagiaLucisMod;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
		FieldElement e = this.elements.computeIfAbsent(pos.immutable(),
				p -> {
					MagiaLucisMod.LOGGER.info("Adding new field element at {}, field {}", p, this.field.id);
					return new FieldElement(this, p);
				});
		setDirty();
		return e;
	}

	public void remove(@NotNull BlockPos pos) {
		MagiaLucisMod.LOGGER.info("Removing field element at {}, field {}", pos, this.field.id);
		this.elements.remove(pos);
		setDirty();
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

	public boolean isEmpty() {
		return this.elements.isEmpty();
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
}
