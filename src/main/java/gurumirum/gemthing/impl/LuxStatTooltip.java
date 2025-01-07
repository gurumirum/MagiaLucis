package gurumirum.gemthing.impl;

import gurumirum.gemthing.capability.LuxContainerStat;
import gurumirum.gemthing.capability.LuxStat;
import gurumirum.gemthing.capability.ModCapabilities;
import gurumirum.gemthing.utils.NumberFormats;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

@EventBusSubscriber
public final class LuxStatTooltip {
	private LuxStatTooltip() {}

	private static final char BLOCK = '█';
	private static final char HALF_BLOCK = '▌';
	private static final char MINI_BLOCK = '▏';

	// TODO localize
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();

		LuxContainerStat containerStat = stack.getCapability(ModCapabilities.LUX_CONTAINER_STAT);
		if (containerStat != null) {
			formatInternal(containerStat, event.getToolTip(), 1, expandedMode(event.getFlags()), true);
			return;
		}

		LuxStat sourceStat = stack.getCapability(ModCapabilities.GEM_STAT);
		if (sourceStat != null) {
			formatInternal(sourceStat, event.getToolTip(), 1, expandedMode(event.getFlags()), false);
		}
	}

	private static Mode expandedMode(TooltipFlag flag) {
		return flag.hasControlDown() ? Mode.EXPANDED : Mode.HIDDEN;
	}

	public static void formatContainerStat(LuxContainerStat stat, List<Component> tooltip) {
		formatContainerStat(stat, tooltip, Mode.ALWAYS_VISIBLE);
	}

	public static void formatContainerStat(LuxContainerStat stat, List<Component> tooltip, Mode mode) {
		formatInternal(stat, tooltip, -1, mode, true);
	}

	public static void formatSourceStat(LuxStat stat, List<Component> tooltip) {
		formatSourceStat(stat, tooltip, Mode.ALWAYS_VISIBLE);
	}

	public static void formatSourceStat(LuxStat stat, List<Component> tooltip, Mode mode) {
		formatInternal(stat, tooltip, -1, mode, false);
	}

	private static void formatInternal(LuxStat stat, List<Component> tooltip, int startIndex, Mode mode, boolean isContainer) {
		int i = startIndex < 0 ? tooltip.size() : startIndex;
		String indent;

		switch (mode) {
			case HIDDEN -> {
				tooltip.add(i, Component.literal(isContainer ?
						"- " + ChatFormatting.WHITE + "LUX" + ChatFormatting.DARK_GRAY + " Container Stat [Ctrl]" :
						"- Gem Stat [Ctrl]").withStyle(ChatFormatting.DARK_GRAY));
				return;
			}
			case EXPANDED -> {
				tooltip.add(i++, Component.literal(isContainer ?
						"+ " + ChatFormatting.WHITE + "LUX" + ChatFormatting.DARK_GRAY + " Container Stat [Ctrl]" :
						"+ Gem Stat [Ctrl]").withStyle(ChatFormatting.DARK_GRAY));
				indent = " ";
			}
			default -> indent = "";
		}

		tooltip.add(i++, Component.literal(isContainer ?
				indent + "LUX" + ChatFormatting.GRAY + " Charge Rate: " :
				indent + "LUX" + ChatFormatting.GRAY + " Transfer Rate: "));

		tooltip.add(i++, Component.literal(indent + " R: ").withStyle(ChatFormatting.GRAY)
				.append(component(stat, stat.rMaxTransfer()).withStyle(ChatFormatting.RED)));

		tooltip.add(i++, Component.literal(indent + " G: ").withStyle(ChatFormatting.GRAY)
				.append(component(stat, stat.gMaxTransfer()).withStyle(ChatFormatting.GREEN)));

		tooltip.add(i++, Component.literal(indent + " B: ").withStyle(ChatFormatting.GRAY)
				.append(component(stat, stat.bMaxTransfer()).withStyle(ChatFormatting.BLUE)));

		tooltip.add(i++, Component.literal(indent + ChatFormatting.GRAY + "Min. " + ChatFormatting.WHITE + "LUX" + ChatFormatting.GRAY + " Threshold: ")
				.append(Component.literal(formatLuxThreshold(stat.minLuxThreshold())).withStyle(ChatFormatting.YELLOW)));
	}

	private static MutableComponent component(LuxStat stat, double maxTransfer) {
		if (maxTransfer < stat.minLuxThreshold() || Double.isInfinite(stat.minLuxThreshold())) {
			return Component.empty().append(Component.literal("  0").withStyle(ChatFormatting.DARK_GRAY));
		} else {
			double level = (Math.log10(maxTransfer) - 1) * 2;
			return Component.literal(bar(level)).append(String.format("  " + NumberFormats.DECIMAL.format(maxTransfer)));
		}
	}

	private static String bar(double level) {
		if (level <= 0) return MINI_BLOCK + "";

		StringBuilder stb = new StringBuilder();
		while (level >= 1) {
			stb.append(BLOCK);
			level--;
		}

		if (level > .5) stb.append(HALF_BLOCK);
		return stb.toString();
	}

	private static String formatLuxThreshold(double value) {
		if (value == Double.POSITIVE_INFINITY) return "∞";
		else return NumberFormats.INTEGER.format(value);
	}

	public enum Mode {
		HIDDEN,
		EXPANDED,
		ALWAYS_VISIBLE
	}
}
