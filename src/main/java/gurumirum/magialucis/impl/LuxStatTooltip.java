package gurumirum.magialucis.impl;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.capability.LuxContainerStat;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.capability.ModCapabilities;
import gurumirum.magialucis.utils.NumberFormats;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.ClientTooltipFlag;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@EventBusSubscriber(modid = MagiaLucisMod.MODID)
public final class LuxStatTooltip {
	private LuxStatTooltip() {}

	private static final char BLOCK = '█';
	private static final char HALF_BLOCK = '▌';
	private static final char MINI_BLOCK = '▏';

	private static @Nullable ItemStack tooltipSkippedStack;

	public static void skipAutoTooltipFor(ItemStack stack) {
		tooltipSkippedStack = stack;
	}

	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		ItemStack skipped = tooltipSkippedStack;
		if (skipped != null) {
			tooltipSkippedStack = null;
			if (stack == skipped) return;
		}

		LuxContainerStat containerStat = stack.getCapability(ModCapabilities.LUX_CONTAINER_STAT);
		if (containerStat != null) {
			formatInternal(containerStat, event.getToolTip(), 1, expandedMode(ClientTooltipFlag.of(event.getFlags())), Type.CONTAINER);
			return;
		}

		LuxStat sourceStat = stack.getCapability(ModCapabilities.GEM_STAT);
		if (sourceStat != null) {
			formatInternal(sourceStat, event.getToolTip(), 1, expandedMode(ClientTooltipFlag.of(event.getFlags())), Type.GEM);
		}
	}

	private static Mode expandedMode(TooltipFlag flag) {
		return flag.hasControlDown() ? Mode.EXPANDED : Mode.HIDDEN;
	}

	public static void formatStat(LuxStat stat, List<Component> tooltip, Type type) {
		formatStat(stat, tooltip, Mode.ALWAYS_VISIBLE, type);
	}

	public static void formatStat(LuxStat stat, List<Component> tooltip, Mode mode, Type type) {
		formatInternal(stat, tooltip, -1, mode, type);
	}

	private static void formatInternal(LuxStat stat, List<Component> tooltip, int startIndex, Mode mode, Type type) {
		int i = startIndex < 0 ? tooltip.size() : startIndex;
		String indent;

		switch (mode) {
			case HIDDEN -> {
				tooltip.add(i, Component.translatable(switch (type) {
					case GEM -> "item.magialucis.tooltip.lux_stat_hidden";
					case CONTAINER -> "item.magialucis.tooltip.lux_container_stat_hidden";
					case SOURCE -> "item.magialucis.tooltip.lux_source_stat_hidden";
				}));
				return;
			}
			case EXPANDED -> {
				tooltip.add(i, Component.translatable(switch (type) {
					case GEM -> "item.magialucis.tooltip.lux_stat_expanded";
					case CONTAINER -> "item.magialucis.tooltip.lux_container_stat_expanded";
					case SOURCE -> "item.magialucis.tooltip.lux_source_stat_expanded";
				}));
				indent = " ";
			}
			default -> indent = "";
		}

		if (stat.minLuxThreshold() > 0) {
			tooltip.add(i++, Component.literal(indent).append(Component.translatable(
					"item.magialucis.tooltip.lux_min_threshold",
					formatLuxThreshold(stat.minLuxThreshold()))));
		}

		tooltip.add(i++, Component.literal(indent).append(Component.translatable(switch (type) {
			case GEM -> "item.magialucis.tooltip.lux_transfer_rate";
			case CONTAINER -> "item.magialucis.tooltip.lux_charge_rate";
			case SOURCE -> "item.magialucis.tooltip.lux_generation_rate";
		})));

		tooltip.add(i++, Component.literal(indent + " ").append(Component.translatable(
						"item.magialucis.tooltip.lux_transfer_rate.r",
						component(stat, stat.rMaxTransfer()).withStyle(ChatFormatting.RED))
				.withStyle(ChatFormatting.GRAY)));

		tooltip.add(i++, Component.literal(indent + " ").append(Component.translatable(
						"item.magialucis.tooltip.lux_transfer_rate.g",
						component(stat, stat.gMaxTransfer()).withStyle(ChatFormatting.GREEN))
				.withStyle(ChatFormatting.GRAY)));

		tooltip.add(i++, Component.literal(indent + " ").append(Component.translatable(
						"item.magialucis.tooltip.lux_transfer_rate.b",
						component(stat, stat.bMaxTransfer()).withStyle(ChatFormatting.BLUE))
				.withStyle(ChatFormatting.GRAY)));
	}

	private static MutableComponent component(LuxStat stat, double maxTransfer) {
		if (maxTransfer <= 0 || maxTransfer < stat.minLuxThreshold() || Double.isInfinite(stat.minLuxThreshold())) {
			return Component.empty().append(Component.literal("  0").withStyle(ChatFormatting.DARK_GRAY));
		} else {
			double level = (Math.log10(maxTransfer) - 1) * 2;
			return Component.literal(bar(level)).append(String.format("  " + NumberFormats.DECIMAL.format(maxTransfer)));
		}
	}

	private static String bar(double level) {
		if (level <= .5) return MINI_BLOCK + "";

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

	public enum Type {
		GEM,
		CONTAINER,
		SOURCE
	}
}
