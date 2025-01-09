package gurumirum.magialucis.utils;

import org.joml.Vector3d;

public final class LuxUtils {
	private LuxUtils() {}

	public static void transfer(Vector3d src, Vector3d dst, Vector3d dstMaxCharge) {
		transfer(src, dst, dstMaxCharge.x, dstMaxCharge.y, dstMaxCharge.z);
	}

	public static void transfer(Vector3d src, Vector3d dst, double dstMaxChargeR, double dstMaxChargeG, double dstMaxChargeB) {
		dst.x = transfer(src, dst.x, ColorComponent.R, dstMaxChargeR);
		dst.y = transfer(src, dst.y, ColorComponent.G, dstMaxChargeG);
		dst.z = transfer(src, dst.z, ColorComponent.B, dstMaxChargeB);
	}

	public static double transfer(Vector3d src, double dst, ColorComponent component, double dstMaxCharge) {
		return transfer(src, dst, component, dstMaxCharge, Double.MAX_VALUE);
	}

	public static double transfer(Vector3d src, double dst, ColorComponent component, double dstMaxCharge, double maxTransfer) {
		double srcValue = getComponent(src, component);

		double transferLimit = Math.min(maxTransfer, srcValue);
		double receiveLimit = dstMaxCharge - dst;
		double transferAmount = Math.min(receiveLimit, transferLimit);
		if (transferAmount == 0) return dst;

		setComponent(src, component, srcValue - transferAmount);
		// to prevent floating point shenanigans
		return receiveLimit <= transferLimit ? dstMaxCharge : dst + transferAmount;
	}

	public static boolean isValid(Vector3d vec) {
		return !Double.isNaN(vec.x) && !Double.isNaN(vec.y) && !Double.isNaN(vec.z);
	}

	public static void snapComponents(Vector3d vec, double min) {
		if (!(vec.x >= min)) vec.x = 0;
		if (!(vec.y >= min)) vec.y = 0;
		if (!(vec.z >= min)) vec.z = 0;
	}

	private static double getComponent(Vector3d color, ColorComponent component) {
		return switch (component) {
			case R -> color.x;
			case G -> color.y;
			case B -> color.z;
		};
	}

	private static void setComponent(Vector3d color, ColorComponent component, double value) {
		switch (component) {
			case R -> color.x = value;
			case G -> color.y = value;
			case B -> color.z = value;
		}
	}

	public enum ColorComponent {
		R, G, B
	}
}
