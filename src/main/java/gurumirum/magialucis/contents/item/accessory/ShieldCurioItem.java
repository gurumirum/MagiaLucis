package gurumirum.magialucis.contents.item.accessory;

import gurumirum.magialucis.api.capability.LuxContainerStat;
import gurumirum.magialucis.contents.Augments;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.data.AugmentLogic;
import gurumirum.magialucis.impl.LuxStatTooltip;
import gurumirum.magialucis.utils.NumberFormats;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class ShieldCurioItem extends LuxContainerCurioItem {
	public static final long SHIELD_RECHARGE_COST = 15;
	public static final long EXPLOSION_COST = 250;

	public static final float DAMAGE_ABSORPTION_LIMIT = 4;

	public static final float MAX_SHIELD = 10;
	public static final float SHIELD_RECHARGE_PER_SEC = 1;

	public static final int EXPLOSION_DAMAGE = 1;

	public ShieldCurioItem(Properties properties) {
		super(properties);
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		LivingEntity entity = slotContext.entity();
		Level level = entity.level();
		if (level.isClientSide || level.getGameTime() % 20 != 0 || entity.invulnerableTime > 0) return;

		long charge = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
		if (charge < SHIELD_RECHARGE_COST) return;

		float shieldCharge = stack.getOrDefault(ModDataComponents.SHIELD_CHARGE, 0f);
		if (!(shieldCharge >= MAX_SHIELD)) {
			shieldCharge = Math.min(MAX_SHIELD, (Float.isNaN(shieldCharge) ? 0 : Math.max(0, shieldCharge)) + SHIELD_RECHARGE_PER_SEC);

			stack.set(ModDataComponents.SHIELD_CHARGE, shieldCharge);
			stack.set(ModDataComponents.LUX_CHARGE, charge - SHIELD_RECHARGE_COST);
			if (shieldCharge >= MAX_SHIELD) {
				stack.set(ModDataComponents.DEPLETED, false);
			}
		}
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		tooltip.add(Component.translatable("item.magialucis.shield_necklace.tooltip.0"));

		float shieldCharge = stack.getOrDefault(ModDataComponents.SHIELD_CHARGE, 0f);
		if (shieldCharge > 0) {
			tooltip.add(Component.translatable("item.magialucis.shield_necklace.tooltip.shield_charge",
					NumberFormats.DECIMAL.format(shieldCharge),
					NumberFormats.DECIMAL.format(MAX_SHIELD)));
		}

		super.appendHoverText(stack, context, tooltip, flag);
	}

	@Override
	protected void appendLuxContainerDescription(@NotNull ItemStack stack, @NotNull TooltipContext context,
	                                             @NotNull List<Component> tooltip, @NotNull TooltipFlag flag,
	                                             @NotNull LuxContainerStat luxContainerStat) {
		super.appendLuxContainerDescription(stack, context, tooltip, flag, luxContainerStat);
		if (AugmentLogic.getAugments(stack).has(Augments.SHIELD_NECKLACE_EXPLOSION)) {
			tooltip.add(Component.translatable("item.magialucis.shield_necklace.tooltip.lux_consumption.explosion",
					LuxStatTooltip.formatLuxCost(SHIELD_RECHARGE_COST, luxContainerStat.maxCharge()),
					LuxStatTooltip.formatLuxCost(EXPLOSION_COST, luxContainerStat.maxCharge())));
		} else {
			tooltip.add(Component.translatable("item.magialucis.shield_necklace.tooltip.lux_consumption",
					LuxStatTooltip.formatLuxCost(SHIELD_RECHARGE_COST, luxContainerStat.maxCharge())));
		}
	}
}
