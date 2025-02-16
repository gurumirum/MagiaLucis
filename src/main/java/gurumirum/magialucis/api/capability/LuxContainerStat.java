package gurumirum.magialucis.api.capability;

public interface LuxContainerStat extends LuxStat {
	LuxContainerStat NULL = simple(0, 0, 0, 0, 0);

	long maxCharge();

	static Simple simple(long maxCharge, double minLuxThreshold, double rMaxTransfer, double gMaxTransfer, double bMaxTransfer) {
		return new Simple(maxCharge, minLuxThreshold, rMaxTransfer, gMaxTransfer, bMaxTransfer);
	}

	static Simple withBaseStat(long maxCharge, LuxStat stat) {
		return new Simple(maxCharge, stat.minLuxThreshold(), stat.rMaxTransfer(), stat.gMaxTransfer(), stat.bMaxTransfer());
	}

	static Simple copyOf(LuxContainerStat stat) {
		return new Simple(stat.maxCharge(), stat.minLuxThreshold(), stat.rMaxTransfer(), stat.gMaxTransfer(), stat.bMaxTransfer());
	}

	record Simple(
			long maxCharge,
			double minLuxThreshold,
			double rMaxTransfer,
			double gMaxTransfer,
			double bMaxTransfer
	) implements LuxContainerStat {}
}
