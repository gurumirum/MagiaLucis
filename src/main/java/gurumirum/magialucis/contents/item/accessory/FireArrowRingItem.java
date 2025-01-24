package gurumirum.magialucis.contents.item.accessory;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FireArrowRingItem extends BaseCurioItem {
	public FireArrowRingItem(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
	                            @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
		tooltipComponents.add(Component.translatable("item.magialucis.fire_arrow_ring.tooltip.0"));
	}
}
