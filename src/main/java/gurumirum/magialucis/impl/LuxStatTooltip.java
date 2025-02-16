package gurumirum.magialucis.impl;

import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.api.capability.LuxContainerStat;
import gurumirum.magialucis.api.capability.LuxStat;
import gurumirum.magialucis.api.capability.MagiaLucisCaps;
import gurumirum.magialucis.contents.data.GemStat;
import gurumirum.magialucis.contents.data.GemStatLogic;
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

@EventBusSubscriber(modid = MagiaLucisApi.MODID)
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

		LuxContainerStat containerStat = stack.getCapability(MagiaLucisCaps.LUX_CONTAINER_STAT);
		if (containerStat != null) {
			formatInternal(containerStat, event.getToolTip(), 1, expandedMode(ClientTooltipFlag.of(event.getFlags())), Type.CONTAINER);
			return;
		}

		GemStat gemStat = GemStatLogic.get(stack);
		if (gemStat != null) {
			formatInternal(gemStat, event.getToolTip(), 1, expandedMode(ClientTooltipFlag.of(event.getFlags())), Type.GEM);
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
					case CONSUMER -> "item.magialucis.tooltip.lux_consumer_stat_hidden";
				}));
				return;
			}
			case EXPANDED -> {
				tooltip.add(i, Component.translatable(switch (type) {
					case GEM -> "item.magialucis.tooltip.lux_stat_expanded";
					case CONTAINER -> "item.magialucis.tooltip.lux_container_stat_expanded";
					case SOURCE -> "item.magialucis.tooltip.lux_source_stat_expanded";
					case CONSUMER -> "item.magialucis.tooltip.lux_consumer_stat_expanded";
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
			case CONSUMER -> "item.magialucis.tooltip.lux_consumption_rate";
		})));

		tooltip.add(i++, Component.literal(indent + " ").append(Component.translatable(
						"item.magialucis.tooltip.lux_transfer_rate.r",
						componentMeter(stat.rMaxTransfer(), stat).withStyle(ChatFormatting.RED))
				.withStyle(ChatFormatting.GRAY)));

		tooltip.add(i++, Component.literal(indent + " ").append(Component.translatable(
						"item.magialucis.tooltip.lux_transfer_rate.g",
						componentMeter(stat.gMaxTransfer(), stat).withStyle(ChatFormatting.GREEN))
				.withStyle(ChatFormatting.GRAY)));

		tooltip.add(i++, Component.literal(indent + " ").append(Component.translatable(
						"item.magialucis.tooltip.lux_transfer_rate.b",
						componentMeter(stat.bMaxTransfer(), stat).withStyle(ChatFormatting.BLUE))
				.withStyle(ChatFormatting.GRAY)));
	}

	public static MutableComponent componentMeter(double maxTransfer) {
		return componentMeter(maxTransfer, null);
	}

	public static MutableComponent componentMeter(double maxTransfer, @Nullable LuxStat stat) {
		if (!Double.isNaN(maxTransfer) && maxTransfer > 0) {
			if (stat == null || (maxTransfer >= stat.minLuxThreshold() && !Double.isInfinite(stat.minLuxThreshold()))) {
				double level = (Math.log10(maxTransfer) - 1) * 2;
				return Component.literal(bar(level)).append(String.format("  " + NumberFormats.DECIMAL.format(maxTransfer)));
			}
		}
		return Component.empty().append(Component.literal("  0").withStyle(ChatFormatting.DARK_GRAY));
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
		SOURCE,
		CONSUMER
	}
}
