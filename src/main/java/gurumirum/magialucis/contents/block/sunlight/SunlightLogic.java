package gurumirum.magialucis.contents.block.sunlight;

import gurumirum.magialucis.impl.RGB332;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectSortedMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectSortedMaps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.Arrays;

public final class SunlightLogic {
	private SunlightLogic() {}

	public static final double DEFAULT_BASE_INTENSITY = 12.5;

	public static final Long2ObjectSortedMap<SkyColor> SKY_TICKS_TO_COLOR;

	private static final long[] skyColorTicks;
	private static final SkyColor[] skyColors;

	static {
		Long2ObjectSortedMap<SkyColor> m = new Long2ObjectAVLTreeMap<>();
		m.put(0, new SkyColor(RGB332.of(3, 2, 2), true)); // sunrise start
		m.put(500, new SkyColor(RGB332.of(7, 3, 1), false)); // sunrise
		m.put(1000, new SkyColor(RGB332.of(4, 5, 3), false)); // sunrise end
		m.put(4000, new SkyColor(RGB332.WHITE, false)); // noon start
		m.put(8000, new SkyColor(RGB332.WHITE, false)); // noon end
		m.put(12000, new SkyColor(RGB332.of(4, 5, 3), false)); // sunset start
		m.put(12500, new SkyColor(RGB332.of(7, 3, 1), false)); // sunset
		m.put(13000, new SkyColor(RGB332.of(3, 2, 2), true)); // sunset end
		m.put(16000, new SkyColor(RGB332.of(0, 0, 3), true)); // midnight start
		m.put(20000, new SkyColor(RGB332.of(0, 0, 3), true)); // midnight end
		m.put(24000, new SkyColor(RGB332.of(3, 2, 2), true)); // sunrise start
		SKY_TICKS_TO_COLOR = Long2ObjectSortedMaps.unmodifiable(m);
		skyColorTicks = SKY_TICKS_TO_COLOR.keySet().toLongArray();
		skyColors = SKY_TICKS_TO_COLOR.values().toArray(SkyColor[]::new);
	}

	public static long timeFraction(Level level) {
		long dayTime = level.dimensionType().fixedTime().orElse(level.getDayTime()) % 24000;
		return dayTime - dayTime % 250;
	}

	public static int index(long time, boolean upperBound) {
		int i = Arrays.binarySearch(skyColorTicks, time);
		if (i >= 0) return i;
		return upperBound ? -(i + 1) : -(i + 1) - 1;
	}

	// TODO support for custom stats?
	public static void getColor(@Nullable Level level, BlockPos pos, double baseIntensity, Vector3d dest) {
		if (level == null) {
			dest.zero();
			return;
		}

		long timeFraction = timeFraction(level);
		int index1 = index(timeFraction, false);
		int index2 = index(timeFraction, true);

		if (index1 == index2) {
			skyColors[index1].getIntensity(level, dest);
		} else {
			skyColors[index1].getIntensity(level, dest);
			double r1 = dest.x;
			double g1 = dest.y;
			double b1 = dest.z;
			skyColors[index2].getIntensity(level, dest);
			double r2 = dest.x;
			double g2 = dest.y;
			double b2 = dest.z;

			double frac = (double)(timeFraction - skyColorTicks[index1]) / (skyColorTicks[index2] - skyColorTicks[index1]);

			dest.set(
					Mth.lerp(frac, r1, r2),
					Mth.lerp(frac, g1, g2),
					Mth.lerp(frac, b1, b2));
		}
		dest.mul(baseIntensity);

		float rainLevel = level.getRainLevel(0);
		if (rainLevel > 0) dest.mul(Mth.lerp(rainLevel, 1, 0.5));
		float thunderLevel = level.getThunderLevel(0);
		if (thunderLevel > 0) dest.mul(Mth.lerp(thunderLevel, 1, 0.5));
	}

	public static int calculateSkyVisibility(Level level, BlockPos pos, BlockState state) {
		if (!state.canOcclude()) {
			if (!level.canSeeSky(pos)) return 0;
		}

		BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

		if (state.canOcclude()) {
			mpos.set(pos).move(Direction.UP);
			if (!level.canSeeSky(mpos) || level.getBlockState(mpos).canOcclude()) return 0;
		}

		int visibility = 15;

		for (int i = 0; i < 9; i++) {
			if (i == 4) continue;

			mpos.set(pos).move(
					(i % 3) - 1,
					0,
					(i / 3) - 1);
			if (!level.canSeeSky(mpos) || level.getBlockState(mpos).canOcclude())
				visibility -= i % 2 == 1 ? 2 : 1; // odd indices are block positions at cardinal directions
		}

		return visibility;
	}

	public record SkyColor(byte color, boolean isNight) {
		public void getIntensity(Level level, Vector3d dest) {
			dest.set(RGB332.rBrightness(this.color), RGB332.gBrightness(this.color), RGB332.bBrightness(this.color));
			if (this.isNight) {
				dest.mul(level.getMoonBrightness());
			}
		}
	}
}
