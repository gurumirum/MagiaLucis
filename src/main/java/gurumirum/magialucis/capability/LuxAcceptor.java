package gurumirum.magialucis.capability;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public interface LuxAcceptor {
	LuxAcceptor NULL = new LuxAcceptor() {
		@Override
		public void accept(double red, double green, double blue, boolean test, @NotNull Vector3d acceptedOut) {
			acceptedOut.zero();
		}

		@Override
		public long acceptDirect(long amount, boolean bypassThreshold, boolean test) {
			return 0;
		}
	};

	void accept(double red, double green, double blue, boolean test, @NotNull Vector3d acceptedOut);
	long acceptDirect(long amount, boolean bypassThreshold, boolean test);
}
