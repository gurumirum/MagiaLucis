package gurumirum.gemthing.contents.item;

import gurumirum.gemthing.client.WandEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface WandEffectSource {
	@Nullable WandEffect getWandEffect(Player player, ItemStack stack);
}
