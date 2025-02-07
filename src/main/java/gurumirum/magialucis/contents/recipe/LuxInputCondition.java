package gurumirum.magialucis.contents.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3d;

import static java.lang.Double.MAX_VALUE;
import static java.lang.Double.POSITIVE_INFINITY;

public record LuxInputCondition(
		double minR, double minG, double minB, double minSum,
		double maxR, double maxG, double maxB, double maxSum,
		double minProgress, double maxProgress
) {
	public double min(Component component) {
		return switch (component) {
			case R -> this.minR;
			case G -> this.minG;
			case B -> this.minB;
			case SUM -> this.minSum;
		};
	}

	public double max(Component component) {
		return switch (component) {
			case R -> this.maxR;
			case G -> this.maxG;
			case B -> this.maxB;
			case SUM -> this.maxSum;
		};
	}

	public double get(Component component, boolean min) {
		return min ? min(component) : max(component);
	}

	public double computeProgress(Vector3d luxInput) {
		return computeProgress(luxInput.x, luxInput.y, luxInput.z);
	}

	public double computeProgress(double luxInputR, double luxInputG, double luxInputB) {
		if (luxInputR > this.maxR || luxInputG > this.maxG || luxInputB > this.maxB) return 0;

		double sum = luxInputR + luxInputG + luxInputB;
		if (sum > this.maxSum) return 0;

		double rProgress = this.minR > 0 ? luxInputR / this.minR : 1;
		double gProgress = this.minG > 0 ? luxInputG / this.minG : 1;
		double bProgress = this.minB > 0 ? luxInputB / this.minB : 1;
		double sumProgress = this.minSum > 0 ? sum / this.minSum : 1;

		double progress = Math.min(rProgress, Math.min(gProgress, Math.min(bProgress, sumProgress)));
		return progress < this.minProgress ? 0 : Math.min(progress, this.maxProgress);
	}

	public static final MapCodec<LuxInputCondition> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
			Codec.doubleRange(0, MAX_VALUE).optionalFieldOf("minR", 0.0).forGetter(LuxInputCondition::minR),
			Codec.doubleRange(0, MAX_VALUE).optionalFieldOf("minG", 0.0).forGetter(LuxInputCondition::minG),
			Codec.doubleRange(0, MAX_VALUE).optionalFieldOf("minB", 0.0).forGetter(LuxInputCondition::minB),
			Codec.doubleRange(0, MAX_VALUE).optionalFieldOf("minSum", 0.0).forGetter(LuxInputCondition::minSum),
			Codec.doubleRange(0, POSITIVE_INFINITY).optionalFieldOf("maxR", POSITIVE_INFINITY).forGetter(LuxInputCondition::maxR),
			Codec.doubleRange(0, POSITIVE_INFINITY).optionalFieldOf("maxG", POSITIVE_INFINITY).forGetter(LuxInputCondition::maxG),
			Codec.doubleRange(0, POSITIVE_INFINITY).optionalFieldOf("maxB", POSITIVE_INFINITY).forGetter(LuxInputCondition::maxB),
			Codec.doubleRange(0, POSITIVE_INFINITY).optionalFieldOf("maxSum", POSITIVE_INFINITY).forGetter(LuxInputCondition::maxSum),
			Codec.doubleRange(0, POSITIVE_INFINITY).optionalFieldOf("minProgress", 1.0).forGetter(LuxInputCondition::minProgress),
			Codec.doubleRange(0, POSITIVE_INFINITY).optionalFieldOf("maxProgress", 1.0).forGetter(LuxInputCondition::maxProgress)
	).apply(b, LuxInputCondition::new));

	public static final StreamCodec<FriendlyByteBuf, LuxInputCondition> STREAM_CODEC = StreamCodec.of((buffer, c) -> {
		if (c.minR != 0.0) buffer.writeByte(0).writeDouble(c.minR);
		if (c.minG != 0.0) buffer.writeByte(1).writeDouble(c.minG);
		if (c.minB != 0.0) buffer.writeByte(2).writeDouble(c.minB);
		if (c.minSum != 0.0) buffer.writeByte(3).writeDouble(c.minSum);
		if (c.maxR != POSITIVE_INFINITY) buffer.writeByte(4).writeDouble(c.maxR);
		if (c.maxG != POSITIVE_INFINITY) buffer.writeByte(5).writeDouble(c.maxG);
		if (c.maxB != POSITIVE_INFINITY) buffer.writeByte(6).writeDouble(c.maxB);
		if (c.maxSum != POSITIVE_INFINITY) buffer.writeByte(7).writeDouble(c.maxSum);
		if (c.minProgress != 1.0) buffer.writeByte(8).writeDouble(c.minProgress);
		if (c.maxProgress != 1.0) buffer.writeByte(9).writeDouble(c.maxProgress);
		buffer.writeByte(-1);
	}, buffer -> {
		double minR = 0, minG = 0, minB = 0, minSum = 0,
				maxR = POSITIVE_INFINITY, maxG = POSITIVE_INFINITY,
				maxB = POSITIVE_INFINITY, maxSum = POSITIVE_INFINITY,
				minProgress = 1, maxProgress = 1;

		LOOP:
		while (true) {
			switch (buffer.readByte()) {
				case 0 -> minR = buffer.readDouble();
				case 1 -> minG = buffer.readDouble();
				case 2 -> minB = buffer.readDouble();
				case 3 -> minSum = buffer.readDouble();
				case 4 -> maxR = buffer.readDouble();
				case 5 -> maxG = buffer.readDouble();
				case 6 -> maxB = buffer.readDouble();
				case 7 -> maxSum = buffer.readDouble();
				case 8 -> minProgress = buffer.readDouble();
				case 9 -> maxProgress = buffer.readDouble();
				default -> {
					break LOOP;
				}
			}
		}

		return new LuxInputCondition(minR, minG, minB, minSum, maxR, maxG, maxB, maxSum, minProgress, maxProgress);
	});

	private static final LuxInputCondition none = new LuxInputCondition(
			0, 0, 0, 0,
			POSITIVE_INFINITY, POSITIVE_INFINITY, POSITIVE_INFINITY, POSITIVE_INFINITY,
			1, 1);

	public static LuxInputCondition none() {
		return none;
	}

	public enum Component {
		R, G, B, SUM;

		@Override public String toString() {
			return switch (this) {
				case R -> "R";
				case G -> "G";
				case B -> "B";
				case SUM -> "Sum";
			};
		}
	}
}
