package gurumirum.gemthing.contents.item.wand;

import gurumirum.gemthing.client.WandEffect;
import gurumirum.gemthing.contents.item.LuxBatteryItem;
import gurumirum.gemthing.contents.item.WandEffectSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class LesserIceStaffWandItem extends LuxBatteryItem implements  WandEffectSource {
	public static final int COST_PER_ATTACK = 2;

	public LesserIceStaffWandItem(Properties properties) {
		super(properties.stacksTo(1));
	}

	@Override
	public @Nullable WandEffect getWandEffect(Player player, ItemStack stack) {
		return null;
	}
}
