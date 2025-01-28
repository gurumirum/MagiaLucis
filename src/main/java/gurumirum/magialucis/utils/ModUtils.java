package gurumirum.magialucis.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ModUtils {
	private ModUtils() {}

	public static void giveOrDrop(@Nullable Player player, @NotNull Level level,
	                              @NotNull ItemStack stack, @NotNull BlockPos pos) {
		giveOrDrop(player, level, stack,
				pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
				0, 0, 0);
	}

	public static void giveOrDrop(@Nullable Player player, @NotNull Level level, @NotNull ItemStack stack,
	                              double x, double y, double z,
	                              double dx, double dy, double dz) {
		if (player != null) {
			if (player.addItem(stack)) {
				level.playSound(null, player.getX(), player.getY(), player.getZ(),
						SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f,
						((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7f + 1) * 2);
				return;
			}
		}

		drop(level, stack, x, y, z, dx, dy, dz);
	}

	public static void drop(@NotNull Level level, @NotNull BlockPos pos, @NotNull ItemStack stack) {
		drop(level, stack,
				pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
				0, 0, 0);
	}

	public static void drop(@NotNull Level level, @NotNull ItemStack stack,
	                        double x, double y, double z,
	                        double dx, double dy, double dz) {
		ItemEntity itemEntity = new ItemEntity(level, x, y, z, stack);
		itemEntity.setDeltaMovement(dx, dy, dz);
		level.addFreshEntity(itemEntity);
	}
}
