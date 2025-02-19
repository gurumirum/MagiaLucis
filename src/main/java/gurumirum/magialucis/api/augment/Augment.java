package gurumirum.magialucis.api.augment;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Augment {
	@Nullable Component getDescriptionName(
			Item.@NotNull TooltipContext context, @Nullable Player player,
			@NotNull ItemStack stack, @NotNull TooltipFlag flag);

	void appendDescription(Item.@NotNull TooltipContext context, @Nullable Player player, @NotNull ItemStack stack,
	                       @NotNull List<Component> tooltip, @NotNull TooltipFlag flag);

	@OnlyIn(Dist.CLIENT)
	@Nullable ResourceLocation texture(@NotNull ItemStack stack);
}
