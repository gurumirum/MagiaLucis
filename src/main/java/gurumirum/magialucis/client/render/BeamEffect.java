package gurumirum.magialucis.client.render;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public interface BeamEffect {
	@NotNull BeamEffect.CoordinateSystem beamStart(@NotNull Vector3f dest, float partialTicks);
	@Nullable BeamEffect.CoordinateSystem beamEnd(@NotNull Vector3f dest, float partialTicks);

	float diameter(float partialTicks);
	float rotation(float partialTicks);
	int color(float partialTicks);

	default @NotNull Lifetime lifetime() {
		return Lifetime.TICK;
	}

	enum CoordinateSystem {
		VIEW,
		WORLD,
		MODEL
	}

	enum Lifetime {
		FRAME,
		TICK,
		EVERGREEN
	}
}
