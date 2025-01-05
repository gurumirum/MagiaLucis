package gurumirum.gemthing.capability;

import gurumirum.gemthing.impl.RGB332;

public interface LuxContainerStat extends LuxStat {
	LuxContainerStat NULL = simple(0, RGB332.BLACK, 0, 0);

	long maxCharge();

	static LuxContainerStat.Simple simple(long maxCharge,
	                                      byte color,
	                                      double minLuxThreshold,
	                                      double maxLuxThreshold) {
		return new Simple(maxCharge, color, minLuxThreshold, maxLuxThreshold);
	}

	static LuxContainerStat.Simple withSourceStat(long maxCharge, GemStats gem) {
		return new Simple(maxCharge, gem.color, gem.minLuxThreshold, gem.maxLuxThreshold);
	}

	static LuxContainerStat.Simple copyOf(LuxContainerStat stat) {
		return new Simple(stat.maxCharge(), stat.color(), stat.minLuxThreshold(), stat.maxLuxThreshold());
	}

	record Simple(
			long maxCharge,
			byte color,
			double minLuxThreshold,
			double maxLuxThreshold
	) implements LuxContainerStat {}
}
