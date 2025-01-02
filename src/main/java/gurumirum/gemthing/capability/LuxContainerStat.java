package gurumirum.gemthing.capability;

import gurumirum.gemthing.impl.RGB332;

public interface LuxContainerStat {
	LuxContainerStat NULL = new Simple(0, RGB332.BLACK, 0, 0);

	long maxCharge();
	byte color();
	long minLuxThreshold();
	long maxLuxThreshold();

	record Simple(
			long maxCharge,
			byte color,
			long minLuxThreshold,
			long maxLuxThreshold
	) implements LuxContainerStat {}
}
