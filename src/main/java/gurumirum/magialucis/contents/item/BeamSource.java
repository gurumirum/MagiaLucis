package gurumirum.magialucis.contents.item;

import gurumirum.magialucis.client.RotationLogic;
import gurumirum.magialucis.client.WandEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface BeamSource {
	long DEFAULT_ROTATION_PERIOD = WandEffect.SpinningTipEffect.DEFAULT_ROTATION_PERIOD;

	int beamColor(Player player, ItemStack stack, boolean firstPersonPerspective);
	float beamDiameter(Player player, ItemStack stack, boolean firstPersonPerspective);

	default float beamRotationDegrees(Player player, ItemStack stack, int ticksUsingItem,
	                                   boolean firstPersonPerspective, float partialTicks) {
		return -RotationLogic.rotation(ticksUsingItem, DEFAULT_ROTATION_PERIOD, partialTicks);
	}

	int beamStartDelay(Player player, ItemStack stack);

	default boolean canProduceBeam(Player player, ItemStack stack) {
		return player.getTicksUsingItem() >= beamStartDelay(player, stack);
	}

	static @Nullable BeamSource from(ItemStack stack) {
		return stack.getItem() instanceof BeamSource beamSource ? beamSource : null;
	}
}
