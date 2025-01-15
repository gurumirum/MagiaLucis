package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.client.WandEffect;
import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.contents.item.LuxBatteryItem;
import gurumirum.magialucis.contents.item.WandEffectSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DiamondMaceItem extends LuxBatteryItem implements WandEffectSource {
	public static final int COST_PER_ATTACK = 10;
	public static final int DEBUFF_DURATION = 300;

	public DiamondMaceItem(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable WandEffect getWandEffect(Player player, ItemStack stack, InteractionHand hand) {
		long charge = stack.getOrDefault(Contents.LUX_CHARGE.get(), 0L);
		return charge >= COST_PER_ATTACK ? DiamondMaceEffect.INSTANCE : null;
	}

	@Override
	public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
		long charge = stack.getOrDefault(Contents.LUX_CHARGE.get(), 0L);
		return charge >= COST_PER_ATTACK ? WandAttributes.diamondMace() : WandAttributes.wand();
	}
}
