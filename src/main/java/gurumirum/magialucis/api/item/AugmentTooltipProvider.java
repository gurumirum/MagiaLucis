package gurumirum.magialucis.api.item;

import gurumirum.magialucis.api.augment.Augment;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public interface AugmentTooltipProvider {
	boolean appendHoverTextForAugment(
			Item.@NotNull TooltipContext context, @Nullable Player player, @NotNull ItemStack stack,
			@NotNull List<Component> tooltip, @NotNull TooltipFlag flag, @NotNull Holder<Augment> augment);
}
