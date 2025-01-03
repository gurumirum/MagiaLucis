package gurumirum.gemthing.contents.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface BeamSource {
	int beamColor(Player player, ItemStack stack, boolean firstPersonPerspective);
	float beamDiameter(Player player, ItemStack stack, boolean firstPersonPerspective);

	default double beamRotationDegrees(Player player, ItemStack stack, int ticksUsingItem, boolean firstPersonPerspective) {
		return ticksUsingItem * 20;
	}

	int beamStartDelay(Player player, ItemStack stack);

	default boolean canProduceBeam(Player player, ItemStack stack) {
		return player.getTicksUsingItem() >= beamStartDelay(player, stack);
	}

	static @Nullable BeamSource from(ItemStack stack) {
		return stack.getItem() instanceof BeamSource beamSource ? beamSource : null;
	}
}
