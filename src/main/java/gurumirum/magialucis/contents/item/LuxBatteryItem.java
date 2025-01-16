package gurumirum.magialucis.contents.item;

import gurumirum.magialucis.capability.LuxContainerStat;
import gurumirum.magialucis.capability.ModCapabilities;
import gurumirum.magialucis.client.RotationLogic;
import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.impl.LuxStatTooltip;
import gurumirum.magialucis.impl.RGB332;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LuxBatteryItem extends Item {
	public LuxBatteryItem(Properties properties) {
		super(properties.stacksTo(1));
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		LuxContainerStat luxContainerStat = stack.getCapability(ModCapabilities.LUX_CONTAINER_STAT);
		if (luxContainerStat == null) return;

		long charge = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
		tooltip.add(Component.translatable("item.magialucis.tooltip.lux_charge", charge, luxContainerStat.maxCharge()));
		LuxStatTooltip.formatContainerStat(luxContainerStat, tooltip);
		LuxStatTooltip.skipAutoTooltipFor(stack);
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack) {
		LuxContainerStat luxContainerStat = stack.getCapability(ModCapabilities.LUX_CONTAINER_STAT);
		return luxContainerStat != null;
	}

	@Override
	public int getBarWidth(@NotNull ItemStack stack) {
		LuxContainerStat luxContainerStat = stack.getCapability(ModCapabilities.LUX_CONTAINER_STAT);
		if (luxContainerStat == null) return 0;

		long charge = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
		return (int)Math.round(13 * ((double)charge / luxContainerStat.maxCharge()));
	}

	@Override
	public int getBarColor(@NotNull ItemStack stack) {
		LuxContainerStat luxContainerStat = stack.getCapability(ModCapabilities.LUX_CONTAINER_STAT);
		if (luxContainerStat == null) return 0;

		double rotation = RotationLogic.staticRotation(System.currentTimeMillis(), 1000 * 3);
		int primaryColor = RGB332.toARGB32(luxContainerStat.color(), 255);

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
