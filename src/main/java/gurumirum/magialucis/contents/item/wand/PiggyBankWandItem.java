package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.contents.entity.PiggyBankEntity;
import gurumirum.magialucis.contents.item.LuxBatteryItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PiggyBankWandItem extends LuxBatteryItem {
	public static final int COST_PER_PIGGY = 10;
	public static final int COST_PER_MAINTAIN = 1;

	public PiggyBankWandItem(Properties properties) {super(properties);}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		UUID piggyBankId = stack.get(Contents.PIGGY_BANK_UUID);
		if (player.isCrouching() && piggyBankId != null) {
			if (level instanceof ServerLevel serverLevel
					&& serverLevel.getEntity(piggyBankId) instanceof PiggyBankEntity piggyBankEntity)
				piggyBankEntity.kill();
			stack.set(Contents.PIGGY_BANK_UUID, null);
			return InteractionResultHolder.success(stack);
		}
		long lux = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
		if (lux < COST_PER_PIGGY) return InteractionResultHolder.fail(stack);
		if (!(level instanceof ServerLevel serverLevel)) return InteractionResultHolder.consume(stack);

		stack.set(Contents.LUX_CHARGE, lux - COST_PER_PIGGY);

		Vec3 look = player.getLookAngle();

		PiggyBankEntity piggyBank = new PiggyBankEntity(Contents.PIGGY_BANK.get(), level);
		piggyBank.setPos(player.getX(), player.getY(), player.getZ());
		piggyBank.setDeltaMovement(look);
		if (piggyBankId != null && serverLevel.getEntity(piggyBankId) instanceof PiggyBankEntity prevPiggy)
			prevPiggy.kill();
		stack.set(Contents.PIGGY_BANK_UUID, piggyBank.getUUID());
		piggyBank.setOwnerUuid(player.getUUID());
		level.addFreshEntity(piggyBank);

		return InteractionResultHolder.consume(stack);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
		if (level instanceof ServerLevel serverLevel) {
			UUID uuid = stack.get(Contents.PIGGY_BANK_UUID);
			if (uuid == null) return;
			if (serverLevel.getEntity(uuid) instanceof PiggyBankEntity piggyBank && piggyBank.isAlive()) {
				long lux = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
				if (lux < COST_PER_MAINTAIN) {
					piggyBank.kill();
					stack.set(Contents.PIGGY_BANK_UUID, null);
				} else stack.set(Contents.LUX_CHARGE, lux - COST_PER_MAINTAIN);
			}
		}
	}
}
