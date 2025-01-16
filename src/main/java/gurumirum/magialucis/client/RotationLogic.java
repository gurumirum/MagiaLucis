package gurumirum.magialucis.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public final class RotationLogic {
	private RotationLogic() {}

	private static float staticRotation(long gameTime, long period, boolean wrapAtZero) {
		long t = Long.remainderUnsigned(gameTime, period);
		if (t == 0 && !wrapAtZero) t = period;
		return (t / (float)period) * (float)(-Math.PI * 2);
	}

	public static float staticRotation(long gameTime, long period) {
		return staticRotation(gameTime, period, true);
	}

	public static float rotation(long gameTime, long period, float partialTicks) {
		Minecraft mc = Minecraft.getInstance();
		return mc.isPaused() ? staticRotation(gameTime, period) :
				Mth.lerp(partialTicks,
						staticRotation(gameTime, period),
						staticRotation(gameTime + 1, period, false));
	}
}
