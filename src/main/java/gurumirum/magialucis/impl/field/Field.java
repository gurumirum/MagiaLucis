package gurumirum.magialucis.impl.field;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Block coordinate based, continuous 3-dimensional space mapped with a scalar value.<br>
 * <br>
 * A number of concepts are used in the system, which is documented below.
 * <ul>
 *     <li>A field consists of <i>elements</i>, bound to certain block coordinates, and the property of the
 *     field - {@link #forceRange() force range}, {@link #forceDiminishPower() diminish power}, and
 *     {@link #interferenceThreshold() interference threshold}.</li>
 *     <li>Two elements cannot occupy same block coordinates.</li>
 *     <li>Elements are said to <i>create force</i> in a field. A force refers to an interaction of elements and field
 *     state, where elements alter the values of surrounding area.</li>
 *     <li>A force's <i>influence</i> is a scalar value that determines how strongly the force impacts receiver. An
 *     influence typically varies with distance. If an origin of a force is distance {@code d} away from its receiver,
 *     the amount of influence the force exerts on the receiver is:
 *     <pre>  (1 - (d / (force range))) ^ (diminish power)</pre>
 *     </li>
 *     <li>A force's <i>power</i> is a scalar value representing a modifier to the force's strength. For fields without
 *     interference threshold, or elements with influence sum (sum of influences from all elements in range and itself)
 *     less than or equal to interference threshold, then their power is {@code 1}. Otherwise, the force's power is
 *     equal to the formula below, with {@code T} defined as interference threshold, {@code S} as influence sum, and
 *     {@code H(n)} as asymptotic expansion of the harmonic number:
 *     <pre>  power = (T - 1 + H(S - T + 1)) / S</pre>
 *     </li>
 * </ul>
 * An actual in-game use of fields typically emerges from things like LUX generators, where large number of same
 * generators being close together typically results in heavily diminishing returns.<br>
 * <br>
 * Lastly, to diehard science fans or actual scientists reading this, this is not an attempt to recreate physical
 * phenomena nor an attempt to make you cringe. Although for the latter I sincerely apologize. For those who are still
 * cringing, let me quickly remind you this is a mod to a block game. You use LIGHT ENERGY to fire BEAMS and shit.
 */
public class Field {
	public static final double DEFAULT_RADIUS = 64;
	public static final double DEFAULT_DIMINISH_POWER = 1;
	public static final int DEFAULT_INTERFERENCE_THRESHOLD = 0;

	public final ResourceLocation id;

	private final double forceRange;
	private final double forceRangeSquared;
	private final double forceDiminishPower;
	private final int interferenceThreshold;

	public Field(@NotNull ResourceLocation id, @NotNull FieldBuilder builder) {
		this.id = Objects.requireNonNull(id);
		this.forceRange = builder.forceRange();
		this.forceDiminishPower = builder.forceDiminishPower();
		this.interferenceThreshold = Math.max(0, builder.interferenceThreshold());

		if (Double.isNaN(this.forceRange) || this.forceRange <= 0) {
			throw new IllegalArgumentException("Invalid forceRange for field " + id + " (" + this.forceRange + ")");
		}
		if (Double.isNaN(this.forceDiminishPower)) {
			throw new IllegalArgumentException("Invalid forceDiminishPower for field " + id + " (" + this.forceDiminishPower + ")");
		}

		this.forceRangeSquared = this.forceRange * this.forceRange;
	}

	public final double forceRange() {
		return this.forceRange;
	}

	public final double forceDiminishPower() {
		return this.forceDiminishPower;
	}

	public final int interferenceThreshold() {
		return this.interferenceThreshold;
	}

	public final boolean hasInterference() {
		return this.interferenceThreshold > 0;
	}

	public final double forceRangeSquared() {
		return this.forceRangeSquared;
	}

	public @NotNull FieldInstance createInstance() {
		return new FieldInstance(this);
	}

	public @NotNull FieldInstance createInstance(@NotNull CompoundTag tag){
		return new FieldInstance(this, tag);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Field field)) return false;
		return Objects.equals(this.id, field.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.id);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + id + ']';
	}
}
