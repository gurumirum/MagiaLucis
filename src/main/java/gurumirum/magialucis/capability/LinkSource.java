package gurumirum.magialucis.capability;

import gurumirum.magialucis.impl.luxnet.InWorldLinkState;
import gurumirum.magialucis.impl.luxnet.LinkDestinationSelector;
import gurumirum.magialucis.impl.luxnet.LuxNodeInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public interface LinkSource {
	double DEFAULT_LINK_DISTANCE = 15;

	int maxLinks();
	@Nullable Orientation getLink(int index);
	@Nullable InWorldLinkState getLinkState(int index);
	void setLink(int index, @Nullable Orientation orientation);

	@Nullable LinkDestinationSelector linkDestinationSelector();

	@NotNull Vec3 linkOrigin();

	default double linkDistance() {
		return DEFAULT_LINK_DISTANCE;
	}

	default @Nullable LuxNodeInterface clientSideInterface() {
		return this instanceof LuxNodeInterface iface ? iface : null;
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
}
