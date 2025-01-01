package gurumirum.gemthing.impl;

import gurumirum.gemthing.capability.Capabilities;
import gurumirum.gemthing.capability.GemStat;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.text.DecimalFormat;
import java.util.List;

@EventBusSubscriber
public final class GemStatTooltip {
	private GemStatTooltip() {}

	private static final DecimalFormat pct = new DecimalFormat("0.0%");
	private static final DecimalFormat integer = new DecimalFormat("#,##0");

	private static final char BLOCK = '█';

	@SubscribeEvent
	public static void asdf(ItemTooltipEvent event) {
		GemStat gemStat = event.getItemStack().getCapability(Capabilities.GEM_STAT);
		if (gemStat == null) return;

		// TODO localize
		List<Component> tooltip = event.getToolTip();
		if (event.getFlags().hasShiftDown()) {
			int i = 1;

			tooltip.add(i++, Component.literal("+ Gem Stat [Shift]").withStyle(ChatFormatting.DARK_GRAY));

			double r = RGB332.rBrightness(gemStat.color);
			tooltip.add(i++, Component.literal("  R: ").withStyle(ChatFormatting.GRAY)
					.append(Component.literal(bar(r)).withStyle(ChatFormatting.RED))
					.append(" (" + pct.format(r) + ")"));

			double g = RGB332.gBrightness(gemStat.color);
			tooltip.add(i++, Component.literal("  G: ").withStyle(ChatFormatting.GRAY)
					.append(Component.literal(bar(g)).withStyle(ChatFormatting.GREEN))
					.append(" (" + pct.format(g) + ")"));

			double b = RGB332.bBrightness(gemStat.color);
			tooltip.add(i++, Component.literal("  B: ").withStyle(ChatFormatting.GRAY)
					.append(Component.literal(bar(b)).withStyle(ChatFormatting.BLUE))
					.append(" (" + pct.format(b) + ")"));

			tooltip.add(i++, Component.literal("  Min. LUX Threshold: ").withStyle(ChatFormatting.GRAY)
					.append(Component.literal(formatLuxThreshold(gemStat.minLuxThreshold)).withStyle(ChatFormatting.YELLOW)));
			if (gemStat.minLuxThreshold > 0 && gemStat.minLuxThreshold != Long.MAX_VALUE) {
				tooltip.add(i++, Component.literal("  (Effective Threshold: ").withStyle(ChatFormatting.GRAY)
						.append(Component.literal(formatLuxThreshold((long)Math.ceil(gemStat.minLuxThreshold / r))).withStyle(ChatFormatting.RED))
						.append(" ")
						.append(Component.literal(formatLuxThreshold((long)Math.ceil(gemStat.minLuxThreshold / g))).withStyle(ChatFormatting.GREEN))
						.append(" ")
						.append(Component.literal(formatLuxThreshold((long)Math.ceil(gemStat.minLuxThreshold / b))).withStyle(ChatFormatting.BLUE))
						.append(")"));
			}
			tooltip.add(i++, Component.literal("  Max. LUX Threshold: ").withStyle(ChatFormatting.GRAY)
					.append(Component.literal(formatLuxThreshold(gemStat.maxLuxThreshold)).withStyle(ChatFormatting.YELLOW)));
		} else {
			tooltip.add(1, Component.literal("- Gem Stat [Shift]").withStyle(ChatFormatting.DARK_GRAY));
		}
	}

	private static String bar(double brightness) {
		int bars = 1 + (int)(brightness * 7);
		StringBuilder stb = new StringBuilder(bars);
		while (bars-- > 0) stb.append(BLOCK);
		return stb.toString();
	}

	private static String formatLuxThreshold(long value) {
		if (value == Long.MAX_VALUE) return "∞";
		else return integer.format(value);
	}
}
