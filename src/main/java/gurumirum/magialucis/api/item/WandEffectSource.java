package gurumirum.magialucis.api.item;

import gurumirum.magialucis.client.render.WandEffect;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface WandEffectSource {
	@Nullable WandEffect getWandEffect(Player player, ItemStack stack, InteractionHand hand);
}
