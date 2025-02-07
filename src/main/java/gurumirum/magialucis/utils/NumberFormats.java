package gurumirum.magialucis.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

public final class NumberFormats {
	private NumberFormats() {}

	public static final DecimalFormat DECIMAL = new DecimalFormat("#,##0.##");
	public static final DecimalFormat INTEGER = new DecimalFormat("#,##0");

	public static Component dec(double value, @Nullable ChatFormatting color) {
		MutableComponent component = Component.literal(NumberFormats.DECIMAL.format(value));
		if (color != null) component.withStyle(color);
		return component;
	}

	public static Component pct(double value, @Nullable ChatFormatting color) {
		MutableComponent component = Component.literal(NumberFormats.DECIMAL.format(value * 100) + "%");
		if (color != null) component.withStyle(color);
		return component;
	}
}
