package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.contents.entity.EnderChestPortal;
import gurumirum.magialucis.contents.item.LuxBatteryItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EnderChestPortalWandItem extends LuxBatteryItem {
	public static final int COST_PER_PORTAL_SPAWN = 10;
	public static final int COST_PER_PORTAL_TICK = 1;

	public EnderChestPortalWandItem(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		UUID portalId = stack.get(Contents.PORTAL_UUID);

		if (player.isSecondaryUseActive()) {
			if (portalId != null) {
				if (level instanceof ServerLevel serverLevel
						&& serverLevel.getEntity(portalId) instanceof EnderChestPortal portal)
					portal.kill();
				stack.set(Contents.PORTAL_UUID, null);
			}
			return InteractionResultHolder.consume(stack);
		}

		long lux = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
		if (lux < COST_PER_PORTAL_SPAWN) return InteractionResultHolder.fail(stack);
		if (!(level instanceof ServerLevel serverLevel)) return InteractionResultHolder.consume(stack);

		stack.set(Contents.LUX_CHARGE, lux - COST_PER_PORTAL_SPAWN);

		Vec3 look = player.getLookAngle();

		EnderChestPortal portal = new EnderChestPortal(level);
		portal.setPos(player.getX(), player.getY(), player.getZ());
		portal.setDeltaMovement(look);
		portal.setOwnerUuid(player.getUUID());
		portal.setLife(60);

		if (portalId != null && serverLevel.getEntity(portalId) instanceof EnderChestPortal prevPortal) {
			prevPortal.kill();
		}

		if (level.addFreshEntity(portal)) {
			stack.set(Contents.PORTAL_UUID, portal.getUUID());
		}

		return InteractionResultHolder.consume(stack);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
		if ((slotId == Inventory.SLOT_OFFHAND || (slotId >= 0 && slotId < 9)) &&
				level instanceof ServerLevel serverLevel) {
			UUID portalId = stack.get(Contents.PORTAL_UUID);
			if (portalId == null) return;

			long lux = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
			if (lux < COST_PER_PORTAL_TICK) return;

			if (serverLevel.getEntity(portalId) instanceof EnderChestPortal portal &&
					portal.isAlive()) {
				portal.setLife(60);
				stack.set(Contents.LUX_CHARGE, lux - COST_PER_PORTAL_TICK);
			} else {
				stack.remove(Contents.PORTAL_UUID);
			}
		}
	}
}
