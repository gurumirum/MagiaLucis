package gurumirum.gemthing.capability;

import gurumirum.gemthing.impl.RGB332;

public interface LuxSourceStat {
	LuxSourceStat NULL = simple(RGB332.BLACK, 0, 0);

	byte color();
	long minLuxThreshold();
	long maxLuxThreshold();

	static Simple simple(byte color, long minLuxThreshold, long maxLuxThreshold) {
		return new Simple(color, minLuxThreshold, maxLuxThreshold);
	}

	static LuxSourceStat.Simple copyOf(LuxSourceStat stat) {
		return new Simple(stat.color(), stat.minLuxThreshold(), stat.maxLuxThreshold());
	}

	record Simple(
			byte color,
			long minLuxThreshold,
			long maxLuxThreshold
	) implements LuxSourceStat {}
}
