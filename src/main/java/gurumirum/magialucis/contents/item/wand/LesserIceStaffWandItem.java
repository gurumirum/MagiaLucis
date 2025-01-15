package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.client.WandEffect;
import gurumirum.magialucis.contents.item.LuxBatteryItem;
import gurumirum.magialucis.contents.item.WandEffectSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class LesserIceStaffWandItem extends LuxBatteryItem implements  WandEffectSource {
	public static final int COST_PER_ATTACK = 2;

	public LesserIceStaffWandItem(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable WandEffect getWandEffect(Player player, ItemStack stack, InteractionHand hand) {
		return null;
	}
}
