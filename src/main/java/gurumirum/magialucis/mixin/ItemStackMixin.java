package gurumirum.magialucis.mixin;

import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.data.Augment;
import gurumirum.magialucis.contents.data.ItemAugment;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	@Inject(method = "getTooltipLines",
			at = @At(shift = At.Shift.AFTER, value = "INVOKE", target = "appendHoverText"),
			locals = LocalCapture.CAPTURE_FAILSOFT)
	public void magialucis$onGetTooltipLines(Item.TooltipContext tooltipContext, @Nullable Player player,
	                                         TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> info,
	                                         List<Component> list) {
		@SuppressWarnings("DataFlowIssue")
		ItemStack self = (ItemStack)(Object)this;

		ItemAugment itemAugment = self.get(ModDataComponents.AUGMENTS);
		if (itemAugment != null) {
			for (Holder<Augment> h : itemAugment.set()) {
				Augment augment = h.value();
				list.add(augment.name());
				list.addAll(augment.description());
			}
		}
	}
}
