package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.api.augment.Augment;
import gurumirum.magialucis.api.capability.LuxContainerStat;
import gurumirum.magialucis.api.item.AugmentTooltipProvider;
import gurumirum.magialucis.contents.Augments;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.data.AugmentLogic;
import gurumirum.magialucis.contents.data.ItemAugment;
import gurumirum.magialucis.contents.entity.EnderChestPortal;
import gurumirum.magialucis.contents.item.LuxContainerItem;
import gurumirum.magialucis.utils.NumberFormats;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class EnderChestPortalWandItem extends LuxContainerItem implements AugmentTooltipProvider {
	public static final double PORTAL_DISTANCE_LIMIT = 10;

	public static final int BASE_SPAWN_COST = 200;
	public static final int BASE_TICK_COST = 5;

	public static final int EXTRA_SPAWN_COST_STORAGE_1 = 200; // total 400
	public static final int EXTRA_SPAWN_COST_STORAGE_2 = 600; // total 800
	public static final int EXTRA_SPAWN_COST_STORAGE_3 = 1400; // total 1600

	public static final int EXTRA_TICK_COST_STORAGE_1 = 5; // total 10
	public static final int EXTRA_TICK_COST_STORAGE_2 = 15; // total 20
	public static final int EXTRA_TICK_COST_STORAGE_3 = 35; // total 40

	public static final int EXTRA_TICK_COST_COLLECTOR = 10;

	public EnderChestPortalWandItem(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		UUID portalId = stack.get(ModDataComponents.PORTAL_UUID);

		if (player.isSecondaryUseActive()) {
			if (portalId != null) {
				if (level instanceof ServerLevel serverLevel
						&& serverLevel.getEntity(portalId) instanceof EnderChestPortal portal)
					portal.kill();
				stack.set(ModDataComponents.PORTAL_UUID, null);
			}
			return InteractionResultHolder.consume(stack);
		}

		long lux = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
		int spawnCost = portalSpawnCost(stack);

		if (lux < spawnCost) return InteractionResultHolder.fail(stack);
		if (!(level instanceof ServerLevel serverLevel)) return InteractionResultHolder.consume(stack);

		stack.set(ModDataComponents.LUX_CHARGE, lux - spawnCost);

		Vec3 look = player.getLookAngle();

		EnderChestPortal portal = new EnderChestPortal(level);
		portal.setPos(player.getX() + look.x * 1,
				player.getY() + player.getBbHeight() / 2 + look.y * 1,
				player.getZ() + look.z * 1);
		portal.setDeltaMovement(look);
		portal.setOwnerUuid(player.getUUID());
		portal.setLife(60);

		ItemAugment augments = AugmentLogic.getAugments(stack);

		if (augments.has(Augments.EXPANSION_3)) portal.setStorageTier(EnderChestPortal.StorageTier.T3);
		else if (augments.has(Augments.EXPANSION_2)) portal.setStorageTier(EnderChestPortal.StorageTier.T2);
		else if (augments.has(Augments.EXPANSION_1)) portal.setStorageTier(EnderChestPortal.StorageTier.T1);
		else portal.setStorageTier(EnderChestPortal.StorageTier.T0);

		if (augments.has(Augments.ENDER_WAND_COLLECTOR)) portal.setCollectItems(true);

		if (portalId != null && serverLevel.getEntity(portalId) instanceof EnderChestPortal prevPortal) {
			prevPortal.kill();
		}

		Vec3 pos = portal.getPosition(0);
		if (!portal.isFree(pos.x, pos.y, pos.z)) {
			BlockPos blockPos = player.blockPosition();
			portal.setPos(blockPos.getX() + 0.5,
					blockPos.getY() + player.getBbHeight() / 2,
					blockPos.getZ() + 0.5);
		}

		if (level.addFreshEntity(portal)) {
			stack.set(ModDataComponents.PORTAL_UUID, portal.getUUID());
		}

		return InteractionResultHolder.consume(stack);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
		if ((slotId == Inventory.SLOT_OFFHAND || (slotId >= 0 && slotId < 9)) &&
				level instanceof ServerLevel serverLevel) {
			UUID portalId = stack.get(ModDataComponents.PORTAL_UUID);
			if (portalId == null) return;

			long lux = stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L);
			int spawnCost = portalTickCost(stack);

			if (lux < spawnCost) return;

			if (serverLevel.getEntity(portalId) instanceof EnderChestPortal portal &&
					portal.isAlive() &&
					portal.distanceToSqr(entity) <= PORTAL_DISTANCE_LIMIT * PORTAL_DISTANCE_LIMIT) {
				portal.setLife(60);
				stack.set(ModDataComponents.LUX_CHARGE, lux - spawnCost);
			} else {
				stack.remove(ModDataComponents.PORTAL_UUID);
			}
		}
	}

	@Override
	protected void appendLuxContainerDescription(@NotNull ItemStack stack, @NotNull TooltipContext context,
	                                             @NotNull List<Component> tooltip, @NotNull TooltipFlag flag,
	                                             @NotNull LuxContainerStat luxContainerStat) {
		super.appendLuxContainerDescription(stack, context, tooltip, flag, luxContainerStat);
		tooltip.add(Component.translatable("item.magialucis.tooltip.lux_consumption",
				Component.translatable("item.magialucis.ender_wand.tooltip.cost",
						NumberFormats.dec(portalSpawnCost(stack), null),
						NumberFormats.dec(portalTickCost(stack) * 20, null)
				)
		));
	}

	@Override
	public boolean appendHoverTextForAugment(
			@NotNull TooltipContext context, @Nullable Player player, @NotNull ItemStack stack,
			@NotNull List<Component> tooltip, @NotNull TooltipFlag flag, @NotNull Holder<Augment> augment) {
		if (Augments.EXPANSION_1.is(augment)) {
			tooltip.add(AugmentLogic.augmentDesc("item.magialucis.ender_wand.tooltip.augment.expansion_1"));
		} else if (Augments.EXPANSION_2.is(augment)) {
			tooltip.add(AugmentLogic.augmentDesc("item.magialucis.ender_wand.tooltip.augment.expansion_2"));
		} else if (Augments.EXPANSION_3.is(augment)) {
			tooltip.add(AugmentLogic.augmentDesc("item.magialucis.ender_wand.tooltip.augment.expansion_3"));
		} else if (Augments.ENDER_WAND_COLLECTOR.is(augment)) {
			tooltip.add(AugmentLogic.augmentDesc("item.magialucis.ender_wand.tooltip.augment.collector"));
		} else return false;
		return true;
	}

	public static int portalSpawnCost(@NotNull ItemStack stack) {
		ItemAugment augments = AugmentLogic.getAugments(stack);
		int cost = BASE_SPAWN_COST;

		if (augments.has(Augments.EXPANSION_3)) cost += EXTRA_SPAWN_COST_STORAGE_3;
		else if (augments.has(Augments.EXPANSION_2)) cost += EXTRA_SPAWN_COST_STORAGE_2;
		else if (augments.has(Augments.EXPANSION_1)) cost += EXTRA_SPAWN_COST_STORAGE_1;

		return cost;
	}

	public static int portalTickCost(@NotNull ItemStack stack) {
		ItemAugment augments = AugmentLogic.getAugments(stack);
		int cost = BASE_TICK_COST;

		if (augments.has(Augments.EXPANSION_3)) cost += EXTRA_TICK_COST_STORAGE_3;
		else if (augments.has(Augments.EXPANSION_2)) cost += EXTRA_TICK_COST_STORAGE_2;
		else if (augments.has(Augments.EXPANSION_1)) cost += EXTRA_TICK_COST_STORAGE_1;

		if (augments.has(Augments.ENDER_WAND_COLLECTOR)) cost += EXTRA_TICK_COST_COLLECTOR;

		return cost;
	}
}
