package gurumirum.magialucis.impl.field;

import org.jetbrains.annotations.NotNull;

public final class FieldMath {
	private FieldMath() {}

	private static final double EULER_MASCHERONI = 0.57721566490153286060651209008240243104215933593992359880576723488486772677;

	public static double getInfluence(@NotNull FieldElement element, int x, int y, int z) {
		x -= element.pos().getX();
		y -= element.pos().getY();
		z -= element.pos().getZ();
		double distSq = (double)x * x + (double)y * y + (double)z * z;
		if (element.rangeSq() < distSq) return 0;
		else return Math.pow(1 - Math.sqrt(distSq) / element.range(), element.diminishPower());
	}

	public static double power(int interferenceThreshold, double influenceSum) {
		return influenceSum <= interferenceThreshold ? 1 :
				(interferenceThreshold - 1 + harmonic(influenceSum - interferenceThreshold + 1)) / influenceSum;
	}

	public static double harmonic(double n) {
		double nn = n * n;
		return EULER_MASCHERONI + Math.log(n) + 1.0 / (2 * n) - 1.0 / (12 * nn) + 1.0 / (120 * nn * nn);
	}
}
