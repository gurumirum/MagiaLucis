package gurumirum.gemthing.capability;

import gurumirum.gemthing.impl.RGB332;
import org.joml.Vector3d;

public interface LuxStat {
	LuxStat NULL = simple(RGB332.BLACK, 0, 0, 0, 0);

	byte color();

	double minLuxThreshold();

	double rMaxTransfer();
	double gMaxTransfer();
	double bMaxTransfer();

	default Vector3d maxTransfer(Vector3d dest) {
		return dest.set(rMaxTransfer(), gMaxTransfer(), bMaxTransfer());
	}

	static Simple simple(byte color, double minLuxThreshold, double maxLuxThreshold) {
		return new Simple(color, minLuxThreshold,
				maxLuxThreshold * RGB332.rBrightness(color),
				maxLuxThreshold * RGB332.gBrightness(color),
				maxLuxThreshold * RGB332.bBrightness(color));
	}

	static Simple simple(byte color, double minLuxThreshold, double rMaxTransfer, double gMaxTransfer, double bMaxTransfer) {
		return new Simple(color, minLuxThreshold, rMaxTransfer, gMaxTransfer, bMaxTransfer);
	}

	static LuxStat.Simple copyOf(LuxStat stat) {
		return new Simple(stat.color(), stat.minLuxThreshold(), stat.rMaxTransfer(), stat.gMaxTransfer(), stat.bMaxTransfer());
	}

	record Simple(
			byte color,
			double minLuxThreshold,
			double rMaxTransfer,
			double gMaxTransfer,
			double bMaxTransfer
	) implements LuxStat {}
}
