package gurumirum.magialucis.contents.item;

import gurumirum.magialucis.api.capability.LuxContainerStat;
import gurumirum.magialucis.api.capability.MagiaLucisCaps;
import gurumirum.magialucis.client.RotationLogic;
import gurumirum.magialucis.client.render.light.LightEffect;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.impl.LuxStatTooltip;
import gurumirum.magialucis.utils.NumberFormats;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LuxContainerItem extends Item {
	public LuxContainerItem(Properties properties) {
		super(properties.stacksTo(1));
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		LuxContainerStat luxContainerStat = stack.getCapability(MagiaLucisCaps.LUX_CONTAINER_STAT);
		if (luxContainerStat != null) {
			appendLuxContainerDescription(stack, context, tooltip, flag, luxContainerStat);
			LuxStatTooltip.formatStat(luxContainerStat, tooltip, LuxStatTooltip.Type.CONTAINER);
			LuxStatTooltip.skipAutoTooltipFor(stack);
		}
	}

	protected void appendLuxContainerDescription(@NotNull ItemStack stack, @NotNull TooltipContext context,
	                                             @NotNull List<Component> tooltip, @NotNull TooltipFlag flag,
	                                             @NotNull LuxContainerStat luxContainerStat) {
		long charge = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
		tooltip.add(Component.translatable("item.magialucis.tooltip.lux_charge",
				NumberFormats.INTEGER.format(charge),
				NumberFormats.INTEGER.format(luxContainerStat.maxCharge())));
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack) {
		LuxContainerStat luxContainerStat = stack.getCapability(MagiaLucisCaps.LUX_CONTAINER_STAT);
		return luxContainerStat != null;
	}

	@Override
	public int getBarWidth(@NotNull ItemStack stack) {
		LuxContainerStat luxContainerStat = stack.getCapability(MagiaLucisCaps.LUX_CONTAINER_STAT);
		if (luxContainerStat == null) return 0;

		long charge = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
		return (int)Math.round(13 * ((double)charge / luxContainerStat.maxCharge()));
	}

	@Override
	public int getBarColor(@NotNull ItemStack stack) {
		LuxContainerStat stat = stack.getCapability(MagiaLucisCaps.LUX_CONTAINER_STAT);
		if (stat == null) return 0;

		double rotation = RotationLogic.staticRotation(System.currentTimeMillis(), 1000 * 3);
		int primaryColor = LightEffect.getLightColor(stat.rMaxTransfer(), stat.gMaxTransfer(), stat.bMaxTransfer());

		double brightness = (ARGB32.red(primaryColor) + ARGB32.green(primaryColor) + ARGB32.blue(primaryColor)) / 3.0 / 255.0;
		boolean dark = brightness <= 0.4;
		int secondaryColor = ARGB32.average(primaryColor, dark ? 0xFFFFFFFF : 0xFF333333);

		double oscillation = (Math.sin(rotation) + 1) / 2;

		return ARGB32.lerp((float)(dark ? oscillation * 0.5 + 0.25 : oscillation * 0.75),
				primaryColor, secondaryColor);
	}

	@Override
	public boolean shouldCauseReequipAnimation(@NotNull ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged) {
		return slotChanged ? !oldStack.equals(newStack) : !newStack.is(oldStack.getItem());
	}

	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return !newStack.is(oldStack.getItem());
	}
}
