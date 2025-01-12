package gurumirum.magialucis.impl.field;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.DoubleConsumer;

public final class FieldElement {
	private final FieldInstance fieldInstance;
	private final BlockPos pos;
	private double power = 1;

	double influenceSumCache = 0;

	private @Nullable List<DoubleConsumer> powerChangedEventListeners;

	FieldElement(FieldInstance fieldInstance, @NotNull BlockPos pos) {
		this.fieldInstance = fieldInstance;
		this.pos = Objects.requireNonNull(pos);
	}

	public @NotNull FieldInstance fieldInstance() {
		return fieldInstance;
	}

	public @NotNull BlockPos pos() {
		return this.pos;
	}

	public double power() {
		return this.power;
	}

	void setPower(double power) {
		this.power = power;
		notifyPowerChangedOnNextUpdate();
	}

	void broadcastPowerChanged() {
		if (this.powerChangedEventListeners != null) {
			for (DoubleConsumer l : this.powerChangedEventListeners) l.accept(power);
		}
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
}
