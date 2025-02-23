package gurumirum.magialucis.contents.item.accessory;

import gurumirum.magialucis.api.capability.LuxContainerStat;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.impl.LuxStatTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class ConcealCurioItem extends LuxContainerCurioItem {
	public static final int COST = 1;

	public ConcealCurioItem(Properties properties) {
		super(properties);
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		LivingEntity entity = slotContext.entity();
		Level level = entity.level();

		if (level.isClientSide || level.getGameTime() % 10 != 0) return;

		boolean active = AccessoryLogic.updateCharge(slotContext, stack, 10, COST, true);

		boolean stackActive = stack.getOrDefault(ModDataComponents.ACTIVE, false);
		if (stackActive != active) {
			stack.set(ModDataComponents.ACTIVE, active);
		}
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		tooltip.add(Component.translatable("item.magialucis.conceal_ring.tooltip.0"));

		super.appendHoverText(stack, context, tooltip, flag);
	}

	@Override
	protected void appendLuxContainerDescription(@NotNull ItemStack stack, @NotNull TooltipContext context,
	                                             @NotNull List<Component> tooltip, @NotNull TooltipFlag flag,
	                                             @NotNull LuxContainerStat luxContainerStat) {
		super.appendLuxContainerDescription(stack, context, tooltip, flag, luxContainerStat);
		tooltip.add(LuxStatTooltip.subzeroLuxConsumptionPerSec());
	}
}
