package gurumirum.magialucis.utils;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public final class LuxSampler {
	private final Vector3d[] samplers;
	private int cursor;

	public LuxSampler(int size) {
		if (size <= 0) throw new IllegalArgumentException("size <= 0");
		this.samplers = new Vector3d[size];
		for (int i = 0; i < size; i++) {
			this.samplers[i] = new Vector3d();
		}
	}

	public int size() {
		return this.samplers.length;
	}

	public @NotNull Vector3d nextSampler() {
		Vector3d sampler = this.samplers[this.cursor];
		this.cursor = (this.cursor + 1) % size();
		return sampler.zero();
	}

	public @NotNull Vector3d average(@NotNull Vector3d dest) {
		dest.zero();
		for (Vector3d sampler : this.samplers) {
			dest.add(sampler);
		}
		dest.div(size());
		return dest;
	}

	public @NotNull Vector3d min(@NotNull Vector3d dest) {
		dest.set(Double.POSITIVE_INFINITY);
		for (Vector3d sampler : this.samplers) {
			dest.min(sampler);
		}
		return dest;
	}

	public @NotNull Vector3d max(@NotNull Vector3d dest) {
		dest.set(Double.NEGATIVE_INFINITY);
		for (Vector3d sampler : this.samplers) {
			dest.max(sampler);
		}
		return dest;
	}

	public void reset() {
		for (Vector3d sampler : this.samplers) sampler.zero();
		this.cursor = 0;
	}
}
