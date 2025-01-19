package gurumirum.magialucis.contents.item;

import gurumirum.magialucis.client.RotationLogic;
import gurumirum.magialucis.client.WandEffect;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface BeamSource {
	long DEFAULT_ROTATION_PERIOD = WandEffect.SpinningTipEffect.DEFAULT_ROTATION_PERIOD;

	int beamColor(Player player, ItemStack stack, boolean firstPersonPerspective, float partialTicks);
	float beamDiameter(Player player, ItemStack stack, boolean firstPersonPerspective, float partialTicks);

	default float beamRotation(Player player, ItemStack stack, boolean firstPersonPerspective, float partialTicks) {
		int ticksUsingItem = player.getTicksUsingItem();
		return -RotationLogic.rotation(ticksUsingItem, DEFAULT_ROTATION_PERIOD, partialTicks);
	}

	int beamStartDelay(Player player, ItemStack stack);

	default boolean canProduceBeam(Player player, ItemStack stack, InteractionHand hand) {
		return player.isUsingItem() &&
				player.getUsedItemHand() == hand &&
				player.getTicksUsingItem() >= beamStartDelay(player, stack);
	}

	static @Nullable BeamSource from(ItemStack stack) {
		return stack.getItem() instanceof BeamSource beamSource ? beamSource : null;
	}

	static BlockHitResult trace(Entity entity, Vec3 start, Vec3 end) {
		return entity.level().clip(new ClipContext(start, end,
				ClipContext.Block.VISUAL, ClipContext.Fluid.ANY,
				entity));
	}
}
