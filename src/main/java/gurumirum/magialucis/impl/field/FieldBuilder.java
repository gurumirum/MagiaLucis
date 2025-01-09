package gurumirum.magialucis.impl.field;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class FieldBuilder {
	private double forceRange = Field.DEFAULT_RADIUS;
	private double forceDiminishPower = Field.DEFAULT_DIMINISH_POWER;
	private int interferenceThreshold = Field.DEFAULT_INTERFERENCE_THRESHOLD;

	public double forceRange() {
		return forceRange;
	}

	public double forceDiminishPower() {
		return forceDiminishPower;
	}

	public int interferenceThreshold() {
		return interferenceThreshold;
	}

	/**
	 * Set range of the forces in field. Default value is {@link Field#DEFAULT_RADIUS}.<br>
	 * <br>
	 * Force range is the radius of an area that a force can exert influence from its origin.<br>
	 * <br>
	 * Value less than or equal to zero, or {@code NaN} will produce runtime exception on field initialization.
	 * Value of {@link Double#MAX_VALUE} or {@link Double#POSITIVE_INFINITY} will effectively make all forces of
	 * the field infinite-ranged, with latter additionally removing all forms of diminishing effect, regardless of
	 * distance and {@link #forceDiminishPower(double) diminish power}.
	 *
	 * @param value New force range of the field
	 * @return Builder
	 */
	public FieldBuilder forceRange(double value) {
		this.forceRange = value;
		return this;
	}

	/**
	 * Set diminish power of the forces in field. Default value is {@link Field#DEFAULT_DIMINISH_POWER}.<br>
	 * <br>
	 * Diminish power determines the amount of influence an element has over certain distance. If an origin of a force
	 * is distance {@code d} away from its receiver, the amount of influence the force has on the receiver is:
	 * <pre>  (1 - (d / (force range))) ^ (diminish power)</pre>
	 * Some notable behavior of this formula is listed below: <br>
	 * <ul>
	 *     <li>For a value of {@code 1}, which is the default value, the diminish rate is linear throughout
	 *     the distance.</li>
	 *     <li>For values less than {@code 1}, the diminish rate becomes gentler at start (closer distance) and
	 *     becomes steeper towards end. (farther distance)</li>
	 *     <li>For values greater than {@code 1}, the diminish rate becomes steeper at start (closer distance) and
	 *     becomes gentler towards end. (farther distance)</li>
	 *     <li>For a value of {@code 0}, the diminish rate disappears; all forces will have full influence on any
	 *     receiver inside {@link #forceRange(double) force range}.</li>
	 *     <li>For values less than {@code 0}, the "diminish rate" bends backwards and the influence actually becomes
	 *     <i>stronger</i> as receivers get farther away from source. Once they are outside of
	 *     {@link #forceRange(double) force range}, influence will drop to zero.</li>
	 * </ul>
	 * Value of {@code NaN} will produce runtime exception on field initialization.
	 *
	 * @param value New diminish power of the field
	 * @return Builder
	 */
	public FieldBuilder forceDiminishPower(double value) {
		this.forceDiminishPower = value;
		return this;
	}

	/**
	 * Set interference threshold of this field. Default value is {@link Field#DEFAULT_INTERFERENCE_THRESHOLD}.<br>
	 * <br>
	 * Interference is a source penalty on a force's power, based on the external influences from other forces present
	 * in its range. Total influence sum up to interference threshold has no effect; otherwise, the force's power is
	 * equal to the formula below, with {@code T} defined as interference threshold, {@code S} as influence sum, and
	 * {@code H(n)} as asymptotic expansion of the harmonic number:
	 * <pre>  power = (T - 1 + H(S - T + 1)) / S</pre>
	 * As a result, assuming no influence diminishings, adding a force to a fully saturated system only increases the
	 * total sum of power by a fraction of amount. Adding a force when interference is equal to the threshold
	 * results in total power only going up by 1/2, the next one 1/3, and so forth.<br>
	 * <br>
	 * Value of {@code 0} will disable interference calculations altogether. Values less than {@code 0} will be set to
	 * {@code 0} on field initialization.
	 *
	 * @param value New interference threshold
	 * @return Builder
	 */
	public FieldBuilder interferenceThreshold(int value) {
		this.interferenceThreshold = value;
		return this;
	}

	public Field build(@NotNull ResourceLocation id){
		return new Field(id, this);
	}
}
