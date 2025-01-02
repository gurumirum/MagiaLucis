package gurumirum.gemthing.capability;

import gurumirum.gemthing.impl.RGB332;

@FunctionalInterface
public interface LuxAcceptor {
	LuxAcceptor NULL = (r, g, b, t) -> 0;

	long accept(double red, double green, double blue, boolean test);

	default long accept(long amount, byte color, boolean test) {
		if (amount <= 0) return amount;

		double rBrightness = RGB332.rBrightness(color);
		double gBrightness = RGB332.gBrightness(color);
		double bBrightness = RGB332.bBrightness(color);

		double totalBrightness = rBrightness + gBrightness + bBrightness;

		double red = amount / totalBrightness * rBrightness;
		double green = amount / totalBrightness * bBrightness;
		double blue = amount / totalBrightness * gBrightness;

		return accept(red, green, blue, test);
	}
}
