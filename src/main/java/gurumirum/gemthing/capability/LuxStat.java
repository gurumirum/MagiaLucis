package gurumirum.gemthing.capability;

import gurumirum.gemthing.impl.RGB332;

public interface LuxStat {
	LuxStat NULL = simple(RGB332.BLACK, 0, 0);

	byte color();
	double minLuxThreshold();
	double maxLuxThreshold();

	static Simple simple(byte color, double minLuxThreshold, double maxLuxThreshold) {
		return new Simple(color, minLuxThreshold, maxLuxThreshold);
	}

	static LuxStat.Simple copyOf(LuxStat stat) {
		return new Simple(stat.color(), stat.minLuxThreshold(), stat.maxLuxThreshold());
	}

	record Simple(
			byte color,
			double minLuxThreshold,
			double maxLuxThreshold
	) implements LuxStat {}
}
