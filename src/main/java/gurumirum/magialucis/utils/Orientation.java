package gurumirum.magialucis.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.EnumMap;

public record Orientation(float xRot, float yRot) {
	private static final EnumMap<Direction, Orientation> FACING_ORIENTATIONS = new EnumMap<>(Direction.class);

	public static Orientation of(Direction direction) {
		return FACING_ORIENTATIONS.computeIfAbsent(direction, dir -> {
			float xRot = 0, yRot = 0;

			switch (direction) {
				case DOWN -> xRot = Mth.HALF_PI;
				case UP -> xRot = -Mth.HALF_PI;
				case NORTH -> yRot = Mth.PI;
				case SOUTH -> {}
				case WEST -> yRot = Mth.HALF_PI;
				case EAST -> yRot = -Mth.HALF_PI;
			}

			return new Orientation(xRot, yRot);
		});
	}

	public static Orientation fromLong(long packagedLong) {
		return new Orientation(Float.intBitsToFloat((int)(packagedLong >> 32)), Float.intBitsToFloat((int)packagedLong));
	}

	public static Orientation fromPosition(BlockPos from, BlockPos to) {
		return fromPosition(
				from.getX(), from.getY(), from.getZ(),
				to.getX(), to.getY(), to.getZ());
	}

	public static Orientation fromPosition(BlockPos from, Vec3 to) {
		return fromPosition(
				from.getX() + .5, from.getY() + .5, from.getZ() + .5,
				to.x, to.y, to.z);
	}

	public static Orientation fromPosition(double fromX, double fromY, double fromZ,
	                                       double toX, double toY, double toZ) {
		double x = toX - fromX;
		double y = toY - fromY;
		double z = toZ - fromZ;
		return new Orientation(
				(float)-Mth.atan2(y, Math.sqrt(x * x + z * z)),
				(float)(Mth.atan2(z, x) - Math.PI / 2));
	}

	public static Vector3d toVector(float xRot, float yRot, Vector3d vector) {
		float yCos = Mth.cos(-yRot);
		float ySin = Mth.sin(-yRot);
		float xCos = Mth.cos(xRot);
		float xSin = Mth.sin(xRot);
		return vector.set(ySin * xCos, -xSin, yCos * xCos);
	}

	public static Vector3f toVector(float xRot, float yRot, Vector3f vector) {
		float yCos = Mth.cos(-yRot);
		float ySin = Mth.sin(-yRot);
		float xCos = Mth.cos(xRot);
		float xSin = Mth.sin(xRot);
		return vector.set(ySin * xCos, -xSin, yCos * xCos);
	}

	public long packageToLong() {
		return ((long)Float.floatToRawIntBits(this.xRot) << 32) | Integer.toUnsignedLong(Float.floatToRawIntBits(this.yRot));
	}

	public Vector3d toVector(Vector3d vector) {
		return toVector(this.xRot, this.yRot, vector);
	}

	public Vector3f toVector(Vector3f vector) {
		return toVector(this.xRot, this.yRot, vector);
	}

	public Quaternionf toQuat(Quaternionf dest) {
		return dest.identity().rotateYXZ(this.yRot, this.xRot, 0);
	}
}
