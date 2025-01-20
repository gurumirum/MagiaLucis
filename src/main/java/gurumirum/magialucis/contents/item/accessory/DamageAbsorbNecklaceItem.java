package gurumirum.magialucis.contents.item.accessory;

import gurumirum.magialucis.contents.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DamageAbsorbNecklaceItem extends LuxContainerCurioItem {
	public static final int COST_PER_IMPACT = 5;
	public static final int ABSORBABLE_DAMAGE_TOTAL = 10;
	public static final double ABSORBABLE_DAMAGE_PER_ATTACK = 1;
	public static final int COST_PER_ABSORB = 1;
	public static final int IMPACT_DAMAGE = 1;


	public DamageAbsorbNecklaceItem(Properties properties) {super(properties);}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		super.appendHoverText(stack, context, tooltip, flag);
		tooltip.add(Component.translatable("item.magialucis.tooltip.damage_absorbed", stack.getOrDefault(ModDataComponents.ABSORBED_DAMAGE, .0), ABSORBABLE_DAMAGE_TOTAL));
	}
}
