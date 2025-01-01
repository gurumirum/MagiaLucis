package gurumirum.gemthing.capability;

@FunctionalInterface
public interface LuxAcceptor {
	LuxAcceptor NULL = (a, c, t) -> 0;

	long accept(long amount, byte color, boolean test);
}
