package gurumirum.gemthing.impl;

import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

/**
 * 3-3-2 RGB color format
 */
public final class RGB332 {
	private RGB332() {}

	public static byte WHITE = of(7, 7, 3);

	public static byte of(int r, int g, int b) {
		r = Mth.clamp(r, 0, 7);
		g = Mth.clamp(g, 0, 7);
		b = Mth.clamp(b, 0, 3);
		return (byte)(r << 5 | g << 2 | b);
	}

	public static int toRGB32(byte rgb332) {
		return toARGB32(rgb332, 255);
	}

	public static int toARGB32(byte rgb332, int alpha) {
		alpha = Mth.clamp(alpha, 0, 255);
		return FastColor.ARGB32.color(alpha, rComponentValue(rgb332), gComponentValue(rgb332), bComponentValue(rgb332));
	}

	public static int rComponentValue(byte rgb332) {
		return rValue(rComponent(rgb332));
	}
	public static int gComponentValue(byte rgb332) {
		return gValue(gComponent(rgb332));
	}
	public static int bComponentValue(byte rgb332) {
		return bValue(bComponent(rgb332));
	}

	public static int rComponent(byte rgb332) {
		return (rgb332 >> 5) & 0b111;
	}
	public static int gComponent(byte rgb332) {
		return (rgb332 >> 2) & 0b111;
	}
	public static int bComponent(byte rgb332) {
		return rgb332 & 0b11;
	}

	public static int rValue(int r) {
		return (int)(16 + (255 - 16) * (r / 7.0));
	}
	public static int gValue(int g) {
		return (int)(16 + (255 - 16) * (g / 7.0));
	}
	public static int bValue(int b) {
		return (int)(16 + (255 - 16) * (b / 3.0));
	}

	public static double rBrightness(byte rgb332){
		return rComponentValue(rgb332) / 255f;
	}
	public static double gBrightness(byte rgb332){
		return gComponentValue(rgb332) / 255f;
	}
	public static double bBrightness(byte rgb332){
		return bComponentValue(rgb332) / 255f;
	}
}
