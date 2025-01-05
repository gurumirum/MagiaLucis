package gurumirum.gemthing.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public interface LinkSource {
	double DEFAULT_LINK_DISTANCE = 15;

	int maxLinks();
	@Nullable Orientation getLink(int index);
	void setLink(int index, @Nullable Orientation orientation);

	default double linkDistance() {
		return DEFAULT_LINK_DISTANCE;
	}

	default void unlinkAll() {
		for (int i = 0, maxLinks = maxLinks(); i < maxLinks; i++) setLink(i, null);
	}

	record Orientation(float xRot, float yRot) {
		public static Orientation fromLong(long packagedLong) {
			return new Orientation(Float.intBitsToFloat((int)(packagedLong >> 32)), Float.intBitsToFloat((int)packagedLong));
		}

		public static LinkSource.Orientation fromPosition(BlockPos from, BlockPos to) {
			return fromPosition(
					from.getX(), from.getY(), from.getZ(),
					to.getX(), to.getY(), to.getZ());
		}

		public static LinkSource.Orientation fromPosition(BlockPos from, Vec3 to) {
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

		public long packageToLong() {
			return ((long)Float.floatToRawIntBits(this.xRot) << 32) | Integer.toUnsignedLong(Float.floatToRawIntBits(this.yRot));
		}

		public Vector3d toVector(Vector3d vector) {
			float yCos = Mth.cos(-this.yRot);
			float ySin = Mth.sin(-this.yRot);
			float xCos = Mth.cos(this.xRot);
			float xSin = Mth.sin(this.xRot);
			return vector.set(ySin * xCos, -xSin, yCos * xCos);
		}

		public Vector3f toVector(Vector3f vector) {
			float yCos = Mth.cos(-this.yRot);
			float ySin = Mth.sin(-this.yRot);
			float xCos = Mth.cos(this.xRot);
			float xSin = Mth.sin(this.xRot);
			return vector.set(ySin * xCos, -xSin, yCos * xCos);
		}

		public Quaternionf toQuat(Quaternionf dest) {
			return dest.identity().rotateYXZ(this.yRot, this.xRot, 0);
		}
	}
}
