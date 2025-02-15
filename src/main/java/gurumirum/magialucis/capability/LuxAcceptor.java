package gurumirum.magialucis.capability;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public interface LuxAcceptor {
	LuxAcceptor NULL = new NullAcceptor();

	void accept(double red, double green, double blue, boolean test, @NotNull Vector3d acceptedOut);

	final class NullAcceptor implements LuxAcceptor {
		private NullAcceptor() {}

		@Override
		public void accept(double red, double green, double blue, boolean test, @NotNull Vector3d acceptedOut) {
			acceptedOut.zero();
		}

		@Override
		public String toString() {
			return "NullAcceptor";
		}
	}
}
