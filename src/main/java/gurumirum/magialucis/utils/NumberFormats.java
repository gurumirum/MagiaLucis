package gurumirum.magialucis.utils;

import java.text.DecimalFormat;

public final class NumberFormats {
	private NumberFormats(){}

	public static final DecimalFormat DECIMAL = new DecimalFormat("#,##0.##");
	public static final DecimalFormat INTEGER = new DecimalFormat("#,##0");
}
