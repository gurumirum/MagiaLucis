package gurumirum.magialucis.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.text.DecimalFormat;

public final class NumberFormats {
	private NumberFormats() {}

	public static final DecimalFormat DECIMAL = new DecimalFormat("#,##0.##");
	public static final DecimalFormat INTEGER = new DecimalFormat("#,##0");

	public static Component dec(double value, ChatFormatting color) {
		return Component.literal(NumberFormats.DECIMAL.format(value))
				.withStyle(color);
	}

	public static Component pct(double value, ChatFormatting color) {
		return Component.literal(NumberFormats.DECIMAL.format(value * 100) + "%")
				.withStyle(color);
	}
}
