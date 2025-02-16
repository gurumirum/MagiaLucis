package gurumirum.magialucis.api.capability;

import gurumirum.magialucis.impl.RGB332;
import org.joml.Vector3d;

public interface LuxStat {
	LuxStat NULL = simple(0, 0, 0, 0);

	double minLuxThreshold();

	double rMaxTransfer();
	double gMaxTransfer();
	double bMaxTransfer();

	default Vector3d maxTransfer(Vector3d dest) {
		return dest.set(rMaxTransfer(), gMaxTransfer(), bMaxTransfer());
	}

	static Simple simple(byte color, double minLuxThreshold, double maxLuxThreshold) {
		return new Simple(minLuxThreshold,
				maxLuxThreshold * RGB332.rBrightness(color),
				maxLuxThreshold * RGB332.gBrightness(color),
				maxLuxThreshold * RGB332.bBrightness(color));
	}

	static Simple simple(double minLuxThreshold, double rMaxTransfer, double gMaxTransfer, double bMaxTransfer) {
		return new Simple(minLuxThreshold, rMaxTransfer, gMaxTransfer, bMaxTransfer);
	}

	static LuxStat.Simple copyOf(LuxStat stat) {
		return new Simple(stat.minLuxThreshold(), stat.rMaxTransfer(), stat.gMaxTransfer(), stat.bMaxTransfer());
	}

	record Simple(
			double minLuxThreshold,
			double rMaxTransfer,
			double gMaxTransfer,
			double bMaxTransfer
	) implements LuxStat {}
}
