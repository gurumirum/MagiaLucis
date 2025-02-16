package gurumirum.magialucis.impl.field;

import gurumirum.magialucis.api.field.Field;
import gurumirum.magialucis.api.field.FieldElement;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ServerFieldElement implements FieldElement {
	private final ServerFieldInstance fieldInstance;
	private final BlockPos pos;

	private double range;
	private double rangeSq;
	private double diminishPower;

	private double power = 1;

	double influenceSumCache = 0;

	public ServerFieldElement(@NotNull ServerFieldInstance fieldInstance, @NotNull BlockPos pos) {
		this.fieldInstance = Objects.requireNonNull(fieldInstance);
		this.pos = Objects.requireNonNull(pos);

		Field f = fieldInstance.field();
		this.range = f.forceRange();
		this.rangeSq = f.forceRangeSquared();
		this.diminishPower = f.forceDiminishPower();
	}

	@Override
	public @NotNull ServerFieldInstance fieldInstance() {
		return this.fieldInstance;
	}

	@Override
	public @NotNull BlockPos pos() {
		return this.pos;
	}

	@Override
	public double range() {
		return this.range;
	}

	@Override
	public double rangeSq() {
		return this.rangeSq;
	}

	@Override
	public double diminishPower() {
		return this.diminishPower;
	}

	@Override
	public double power() {
		return this.power;
	}

	@Override
	public void setRange(double range) {
		this.range = range;
		this.rangeSq = range * range;
		this.fieldInstance.setDirty();
	}

	@Override
	public void setDiminishPower(double diminishPower) {
		this.diminishPower = diminishPower;
		this.fieldInstance.setDirty();
	}

	void setPower(double power) {
		this.power = power;
	}

	public void save(@NotNull CompoundTag tag) {
		Field f = fieldInstance.field();
		if (this.range != f.forceRange()) tag.putDouble("range", this.range);
		if (this.diminishPower != f.forceDiminishPower()) tag.putDouble("diminishPower", this.diminishPower);
	}

	public ServerFieldElement(@NotNull ServerFieldInstance fieldInstance, @NotNull BlockPos pos, @NotNull CompoundTag tag) {
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
