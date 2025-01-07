package gurumirum.gemthing.capability;

import gurumirum.gemthing.impl.RGB332;

public interface LuxContainerStat extends LuxStat {
	LuxContainerStat NULL = simple(0, RGB332.BLACK, 0, 0);

	long maxCharge();

	static Simple simple(long maxCharge, byte color, double minLuxThreshold, double maxLuxThreshold) {
		return simple(maxCharge, color, minLuxThreshold,
				maxLuxThreshold * RGB332.rBrightness(color),
				maxLuxThreshold * RGB332.gBrightness(color),
				maxLuxThreshold * RGB332.bBrightness(color));
	}

	static Simple simple(long maxCharge, byte color, double minLuxThreshold, double rMaxTransfer, double gMaxTransfer, double bMaxTransfer) {
		return new Simple(maxCharge, color, minLuxThreshold, rMaxTransfer, gMaxTransfer, bMaxTransfer);
	}

	static Simple withBaseStat(long maxCharge, LuxStat stat) {
		return new Simple(maxCharge, stat.color(), stat.minLuxThreshold(), stat.rMaxTransfer(), stat.gMaxTransfer(), stat.bMaxTransfer());
	}

	static Simple copyOf(LuxContainerStat stat) {
		return new Simple(stat.maxCharge(), stat.color(), stat.minLuxThreshold(), stat.rMaxTransfer(), stat.gMaxTransfer(), stat.bMaxTransfer());
	}

	record Simple(
			long maxCharge,
			byte color,
			double minLuxThreshold,
			double rMaxTransfer,
			double gMaxTransfer,
			double bMaxTransfer
	) implements LuxContainerStat {}
}
