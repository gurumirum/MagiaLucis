package gurumirum.magialucis.utils;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.Month;

// This is the single greatest code I've ever written. It's all downhill from now.
public final class AprilFoolsUtils {
	private AprilFoolsUtils() {}

	public static boolean APRIL_FOOLS = isAprilFools();

	private static boolean isAprilFools() {
		LocalDateTime now = LocalDateTime.now();
		return now.getMonth() == Month.APRIL && now.getDayOfMonth() == 1;
	}

	public static boolean isDank(@NotNull BlockPos pos) {
		return isDank(pos.getX()) && isDank(pos.getY());
	}

	public static boolean isDank(int number) {
		return switch (number) {
			case 69, 420, -69, -420, 69420, -69420 -> true;
			default -> false;
		};
	}
}
