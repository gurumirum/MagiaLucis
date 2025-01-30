package gurumirum.magialucis.impl.field;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.DoubleConsumer;

public final class FieldElement {
	private final FieldInstance fieldInstance;
	private final BlockPos pos;

	private double range;
	private double rangeSq;
	private double diminishPower;

	private double power = 1;

	double influenceSumCache = 0;

	private @Nullable List<DoubleConsumer> powerChangedEventListeners;

	FieldElement(@NotNull FieldInstance fieldInstance, @NotNull BlockPos pos) {
		this.fieldInstance = Objects.requireNonNull(fieldInstance);
		this.pos = Objects.requireNonNull(pos);

		Field f = fieldInstance.field();
		this.range = f.forceRange();
		this.rangeSq = f.forceRangeSquared();
		this.diminishPower = f.forceDiminishPower();
	}

	public @NotNull FieldInstance fieldInstance() {
		return this.fieldInstance;
	}

	public @NotNull BlockPos pos() {
		return this.pos;
	}

	public double range() {
		return this.range;
	}

	public double rangeSq() {
		return this.rangeSq;
	}

	public double diminishPower() {
		return this.diminishPower;
	}

	public double power() {
		return this.power;
	}

	public FieldElement setRange(double range) {
		this.range = range;
		this.rangeSq = range * range;
		this.fieldInstance.setDirty();
		return this;
	}

	public FieldElement setDiminishPower(double diminishPower) {
		this.diminishPower = diminishPower;
		this.fieldInstance.setDirty();
		return this;
	}

	public FieldElement listenPowerChange(@NotNull DoubleConsumer listener) {
		if (this.powerChangedEventListeners == null) this.powerChangedEventListeners = new ArrayList<>();
		this.powerChangedEventListeners.add(listener);
		return this;
	}

	public FieldElement notifyPowerChangedOnNextUpdate() {
		this.fieldInstance.notifyPowerChangedOnNextUpdate(this.pos);
		return this;
	}

	void setPower(double power) {
		this.power = power;
		notifyPowerChangedOnNextUpdate();
	}

	void broadcastPowerChanged() {
		if (this.powerChangedEventListeners != null) {
			for (DoubleConsumer l : this.powerChangedEventListeners) l.accept(this.power);
		}
	}

	public void save(@NotNull CompoundTag tag) {
		Field f = fieldInstance.field();
		if (f.forceRange() != this.range) tag.putDouble("range", this.range);
		if (f.forceDiminishPower() != this.diminishPower) tag.putDouble("diminishPower", this.diminishPower);
	}

	public FieldElement(@NotNull FieldInstance fieldInstance, @NotNull BlockPos pos, @NotNull CompoundTag tag) {
		this(fieldInstance, pos);

		if (tag.contains("range", Tag.TAG_DOUBLE)) {
			this.range = tag.getDouble("range");
			this.rangeSq = this.range * this.range;
		}
		if (tag.contains("diminishPower", Tag.TAG_DOUBLE)) {
			this.diminishPower = tag.getDouble("diminishPower");
		}
	}
}
