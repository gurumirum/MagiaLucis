package gurumirum.gemthing.contents.item;

import gurumirum.gemthing.capability.Capabilities;
import gurumirum.gemthing.capability.LuxContainerStat;
import gurumirum.gemthing.contents.Contents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
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
		LuxContainerStat luxContainerStat = stack.getCapability(Capabilities.LUX_CONTAINER_STAT);
		if (luxContainerStat == null) return;

		long charge = stack.getOrDefault(Contents.LUX_CHARGE.get(), 0L);
		tooltip.add(Component.literal(charge + " / " + luxContainerStat.maxCharge()));
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack) {
		LuxContainerStat luxContainerStat = stack.getCapability(Capabilities.LUX_CONTAINER_STAT);
		return luxContainerStat != null;
	}

	@Override
	public int getBarWidth(@NotNull ItemStack stack) {
		LuxContainerStat luxContainerStat = stack.getCapability(Capabilities.LUX_CONTAINER_STAT);
		if (luxContainerStat == null) return 0;

		long charge = stack.getOrDefault(Contents.LUX_CHARGE.get(), 0L);
		return (int)Math.round(13 * ((double)charge / luxContainerStat.maxCharge()));
	}

	@Override
	public int getBarColor(@NotNull ItemStack stack) {
		double v = (System.currentTimeMillis() % 5000) / 5000.0;
		return FastColor.ARGB32.lerp((float)((Math.sin(v * 2 * Math.PI) + 1) / 2),
				0xFFFFFFFF,
				0xFF999999);
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
