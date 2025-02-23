package gurumirum.magialucis.contents.item.accessory;

import gurumirum.magialucis.api.capability.LuxContainerStat;
import gurumirum.magialucis.impl.LuxStatTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ObsidianBraceletItem extends LuxContainerCurioItem {
	public static final int COST = 5;

	public ObsidianBraceletItem(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		tooltip.add(Component.translatable("item.magialucis.obsidian_bracelet.tooltip.0"));
		super.appendHoverText(stack, context, tooltip, flag);
	}

	@Override
	protected void appendLuxContainerDescription(@NotNull ItemStack stack, @NotNull TooltipContext context,
	                                             @NotNull List<Component> tooltip, @NotNull TooltipFlag flag,
	                                             @NotNull LuxContainerStat luxContainerStat) {
		super.appendLuxContainerDescription(stack, context, tooltip, flag, luxContainerStat);
		tooltip.add(LuxStatTooltip.luxConsumptionPerSec(COST * 20, luxContainerStat.maxCharge()));
	}
}
