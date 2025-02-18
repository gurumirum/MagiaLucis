package gurumirum.magialucis.contents.entity.templeguardian;

import gurumirum.magialucis.client.render.beam.BeamEffect;
import gurumirum.magialucis.api.item.BeamSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class TempleGuardianBeamEffect implements BeamEffect {
	private final TempleGuardian entity;

	public TempleGuardianBeamEffect(TempleGuardian entity) {
		this.entity = entity;
	}

	@Override
	public @NotNull CoordinateSystem beamStart(@NotNull Vector3f dest, float partialTicks) {
		Vec3 pos = this.entity.getEyePosition(partialTicks)
				.add(this.entity.getViewVector(partialTicks).scale(4 / 16f));
		dest.set(pos.x, pos.y, pos.z);
		return CoordinateSystem.WORLD;
	}

	@Override
	public @Nullable CoordinateSystem beamEnd(@NotNull Vector3f beamStart, @NotNull Vector3f dest, float partialTicks) {
		Vec3 start = this.entity.getEyePosition(partialTicks);
		Vec3 end = start.add(this.entity.getViewVector(partialTicks).scale(TempleGuardian.RANGE));
		BlockHitResult hitResult = BeamSource.trace(this.entity, start, end);

		if (hitResult.getType() == HitResult.Type.BLOCK) end = hitResult.getLocation();

		dest.set(end.x, end.y, end.z);
		return CoordinateSystem.WORLD;
	}

	@Override
	public float diameter(float partialTicks) {
		return Mth.lerp(this.entity.getAttackAnimationProgress(partialTicks),
				6 / 16f, 0f);
	}

	@Override
	public float rotation(float partialTicks) {
		return this.entity.getHeadRotation(partialTicks);
	}

	@Override
	public int color(float partialTicks) {
		return FastColor.ARGB32.colorFromFloat(this.entity.getAttackAnimationProgress(partialTicks),
				1, 1, 1);
	}
}
