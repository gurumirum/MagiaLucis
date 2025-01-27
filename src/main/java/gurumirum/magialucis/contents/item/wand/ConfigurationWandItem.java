package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.capability.LinkSource;
import gurumirum.magialucis.capability.ModCapabilities;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import gurumirum.magialucis.net.msgs.SetLinkMsg;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.List;
import java.util.Objects;

public class ConfigurationWandItem extends Item {
	public static final double MAX_DISTANCE = 30;

	public ConfigurationWandItem(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag flags) {
		GlobalPos linkSourcePos = stack.get(ModDataComponents.LINK_SOURCE);
		if (linkSourcePos != null && context.level() != null &&
				Objects.requireNonNull(context.level()).dimension().equals(linkSourcePos.dimension())) {
			tooltip.add(Component.literal("[" + linkSourcePos.pos().toShortString() + "]")
					.withStyle(ChatFormatting.GOLD));
		}
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		GlobalPos linkSourcePos = stack.get(ModDataComponents.LINK_SOURCE.get());

		if (linkSourcePos != null &&
				linkSourcePos.dimension().equals(level.dimension()) &&
				level.isLoaded(linkSourcePos.pos())) {
			LinkSource linkSource = level.getCapability(ModCapabilities.LINK_SOURCE, linkSourcePos.pos());
			if (linkSource != null) {
				if (level.isClientSide) {
					SetLinkMsg msg = ClientLogic.calculateLink(level, player, linkSourcePos.pos(), linkSource,
							null, player.getLookAngle().scale(5).add(player.getEyePosition()));
					if (msg != null) PacketDistributor.sendToServer(msg);
				}

				stack.remove(ModDataComponents.LINK_SOURCE.get());
				return InteractionResultHolder.success(stack);
			}
		}

		return InteractionResultHolder.pass(stack);
	}

	@Override
	public @NotNull InteractionResult onItemUseFirst(ItemStack stack, @NotNull UseOnContext context) {
		GlobalPos linkSourcePos = stack.get(ModDataComponents.LINK_SOURCE.get());
		Level level = context.getLevel();

		if (linkSourcePos != null &&
				linkSourcePos.dimension().equals(level.dimension()) &&
				level.isLoaded(linkSourcePos.pos())) {
			if (context.getClickedPos().equals(linkSourcePos.pos())) {
				if (context.isSecondaryUseActive()) {
					LinkSource linkSource = level.getCapability(ModCapabilities.LINK_SOURCE, linkSourcePos.pos());
					if (linkSource != null) linkSource.unlinkAll();
				}
				stack.remove(ModDataComponents.LINK_SOURCE.get());
				return InteractionResult.SUCCESS;
			}

			LinkSource linkSource = level.getCapability(ModCapabilities.LINK_SOURCE, linkSourcePos.pos());
			if (linkSource != null) {
				if (level.isClientSide) {
					SetLinkMsg msg = ClientLogic.calculateLink(level, context.getPlayer(), linkSourcePos.pos(),
							linkSource, context.getClickedPos(), context.getClickLocation());
					if (msg != null) PacketDistributor.sendToServer(msg);
				}

				stack.remove(ModDataComponents.LINK_SOURCE.get());
				return InteractionResult.SUCCESS;
			}
		}

		LinkSource linkSource = level.getCapability(ModCapabilities.LINK_SOURCE, context.getClickedPos());
		if (linkSource != null) {
			if (!level.isClientSide) {
				if (context.isSecondaryUseActive()) {
					linkSource.unlinkAll();
				} else {
					stack.set(ModDataComponents.LINK_SOURCE.get(), new GlobalPos(level.dimension(), context.getClickedPos()));
				}
			}
			return InteractionResult.SUCCESS;
		} else {
			return InteractionResult.PASS;
		}
	}

	public static final class ClientLogic {
		private static final Quaternionf q1 = new Quaternionf();
		private static final Quaternionf q2 = new Quaternionf();

		public static @Nullable SetLinkMsg calculateLink(
				Level level, Player player, BlockPos linkSourcePos, LinkSource linkSource,
				@Nullable BlockPos cursorPos, Vec3 cursorLocation) {
			if (!(player instanceof LocalPlayer)) return null; // skip for non-local players
			if (linkSource.maxLinks() <= 0) return null;

			// abort if too far away
			double linkDistance = linkSource.linkDistance();
			double maxDistance = linkDistance + 3;
			if (maxDistance * maxDistance < linkSourcePos.distToCenterSqr(cursorLocation)) return null;

			LinkSource.Orientation orientation = cursorPos == null || Screen.hasControlDown() ?
					LinkSource.Orientation.fromPosition(linkSourcePos, cursorLocation) :
					LinkSource.Orientation.fromPosition(linkSourcePos, cursorPos);

			if (player.isSecondaryUseActive()) {
				// remove the closest link with tangent less than 90 degrees
				orientation.toQuat(q1);

				int closestIndex = -1;
				float closestAngle = Float.POSITIVE_INFINITY;

				for (int i = 0, maxLinks = linkSource.maxLinks(); i < maxLinks; i++) {
					LinkSource.Orientation o = linkSource.getLink(i);
					if (o == null) continue;

					float angle = Math.abs(o.toQuat(q2).difference(q1).angle());
					if (angle < (float)(Math.PI / 2) && angle < closestAngle) {
						closestIndex = i;
						closestAngle = angle;
					}
				}

				return closestIndex != -1 ? new SetLinkMsg(linkSourcePos, closestIndex, null) : null;
			}

			if (cursorPos == null) return null;

			// add or replace
			@Nullable BlockHitResult[] connections = getConnections(level, linkSource, linkSourcePos);
			int firstNull = -1;

			for (int i = 0; i < connections.length; i++) {
				BlockHitResult h = connections[i];
				if (h == null) {
					if (firstNull == -1) firstNull = i;
				} else if (h.getBlockPos().equals(cursorPos)) {
					// duplicate detected; remove preexisting connection instead
					return new SetLinkMsg(linkSourcePos, i, null);
				}
			}

			return new SetLinkMsg(linkSourcePos, firstNull == -1 ? connections.length - 1 : firstNull, orientation);
		}

		public static @Nullable BlockHitResult @NotNull [] getConnections(Level level, LinkSource linkSource, BlockPos originPos) {
			BlockHitResult[] arr = new BlockHitResult[linkSource.maxLinks()];
			double linkDistance = linkSource.linkDistance();
			for (int i = 0; i < arr.length; i++) {
				LinkSource.Orientation o = linkSource.getLink(i);
				arr[i] = o == null ? null : LuxUtils.traceConnection(level, originPos, o.xRot(), o.yRot(), linkDistance);
			}
			return arr;
		}

	}
}
