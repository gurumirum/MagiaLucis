package gurumirum.magialucis.contents.item.accessory;

import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.utils.NumberFormats;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class ShieldCurioItem extends LuxContainerCurioItem implements ICurioItem {
	public static final int COST_PER_IMPACT = 250;
	public static final int IMPACT_DAMAGE = 1;

	public static final float DAMAGE_ABSORPTION_LIMIT = 4;

	public static final float MAX_SHIELD = 10;
	public static final float SHIELD_RECHARGE_PER_SEC = 1;

	public static final long COST_PER_SHIELD_RECHARGE = 15;

	public ShieldCurioItem(Properties properties) {
		super(properties);
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		LivingEntity entity = slotContext.entity();
		Level level = entity.level();
		if (level.isClientSide || level.getGameTime() % 20 != 0 || entity.invulnerableTime > 0) return;

		long charge = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
		if (charge < COST_PER_SHIELD_RECHARGE) return;

		float shieldCharge = stack.getOrDefault(ModDataComponents.SHIELD_CHARGE, 0f);
		if (!(shieldCharge >= MAX_SHIELD)) {
			shieldCharge = Math.min(MAX_SHIELD, (Float.isNaN(shieldCharge) ? 0 : Math.max(0, shieldCharge)) + SHIELD_RECHARGE_PER_SEC);

			stack.set(ModDataComponents.SHIELD_CHARGE, shieldCharge);
			stack.set(ModDataComponents.LUX_CHARGE, charge - COST_PER_SHIELD_RECHARGE);
			if (shieldCharge >= MAX_SHIELD) {
				stack.set(ModDataComponents.DEPLETED, false);
			}
		}
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		tooltip.add(Component.translatable("item.magialucis.shield_necklace.tooltip.0",
				NumberFormats.dec(MAX_SHIELD, ChatFormatting.YELLOW)));
		tooltip.add(Component.translatable("item.magialucis.shield_necklace.tooltip.1"));

		float shieldCharge = stack.getOrDefault(ModDataComponents.SHIELD_CHARGE, 0f);
		if (shieldCharge > 0) {
			tooltip.add(Component.translatable("item.magialucis.shield_necklace.tooltip.shield_charge",
					NumberFormats.DECIMAL.format(shieldCharge),
					NumberFormats.DECIMAL.format(MAX_SHIELD)));
		}

		super.appendHoverText(stack, context, tooltip, flag);
	}
}
