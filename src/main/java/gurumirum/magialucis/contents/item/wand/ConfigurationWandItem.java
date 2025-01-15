package gurumirum.magialucis.contents.item.wand;

import gurumirum.magialucis.capability.LinkSource;
import gurumirum.magialucis.capability.ModCapabilities;
import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.net.msgs.SetLinkMsg;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3d;

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
		GlobalPos linkSourcePos = stack.get(Contents.LINK_SOURCE);
		if (linkSourcePos != null && context.level() != null &&
				Objects.requireNonNull(context.level()).dimension().equals(linkSourcePos.dimension())) {
			tooltip.add(Component.literal("[" + linkSourcePos.pos().toShortString() + "]")
					.withStyle(ChatFormatting.GOLD));
		}
	}

	@Override
	public @NotNull InteractionResult onItemUseFirst(ItemStack stack, @NotNull UseOnContext context) {
		GlobalPos linkSourcePos = stack.get(Contents.LINK_SOURCE.get());
		Level level = context.getLevel();

		if (linkSourcePos != null &&
				linkSourcePos.dimension().equals(level.dimension()) &&
				level.isLoaded(linkSourcePos.pos())) {
			if (context.getClickedPos().equals(linkSourcePos.pos())) {
				if (context.isSecondaryUseActive()) {
					LinkSource linkSource = level.getCapability(ModCapabilities.LINK_SOURCE, linkSourcePos.pos());
					if (linkSource != null) linkSource.unlinkAll();
				}
				stack.remove(Contents.LINK_SOURCE.get());
				return InteractionResult.SUCCESS;
			}

			LinkSource linkSource = level.getCapability(ModCapabilities.LINK_SOURCE, linkSourcePos.pos());
			if (linkSource != null) {
				if (level.isClientSide) {
					ClientFunction.calculateLink(level, context.getPlayer(), linkSourcePos.pos(),
							linkSource, context.getClickedPos(), context.getClickLocation());
				}

				stack.remove(Contents.LINK_SOURCE.get());
				return InteractionResult.SUCCESS;
			}
		}

		LinkSource linkSource = level.getCapability(ModCapabilities.LINK_SOURCE, context.getClickedPos());
		if (linkSource != null) {
			if (!level.isClientSide) {
				if (context.isSecondaryUseActive()) {
					linkSource.unlinkAll();
				} else {
					stack.set(Contents.LINK_SOURCE.get(), new GlobalPos(level.dimension(), context.getClickedPos()));
				}
			}
			return InteractionResult.SUCCESS;
		} else {
			return InteractionResult.PASS;
		}
	}

	public static final class ClientFunction {
		private static final Vector3d vecCache = new Vector3d();
		private static final Quaternionf q1 = new Quaternionf();
		private static final Quaternionf q2 = new Quaternionf();

		public static void calculateLink(Level level, Player player, BlockPos linkSourcePos, LinkSource linkSource,
		                                 BlockPos cursorPos, Vec3 cursorLocation) {
			if (!(player instanceof LocalPlayer)) return; // skip for non-local players
			if (linkSource.maxLinks() <= 0) return;

			// abort if too far away
			double linkDistance = linkSource.linkDistance();
			if ((linkDistance + 1) * (linkDistance + 1) < linkSourcePos.distToCenterSqr(cursorLocation)) return;

			if (player.isSecondaryUseActive()) {
				// remove the closest link with tangent less than 90 degrees
				LinkSource.Orientation orientation = isCtrlPressed() ?
						LinkSource.Orientation.fromPosition(linkSourcePos, cursorLocation) :
						LinkSource.Orientation.fromPosition(linkSourcePos, cursorPos);

				orientation.toQuat(q1);

				int closestIndex = -1;
				float closestAngle = Float.POSITIVE_INFINITY;

				for (int i = 0, maxLinks = linkSource.maxLinks(); i < maxLinks; i++) {
					LinkSource.Orientation o = linkSource.getLink(i);
					if (o == null) continue;

					float angle = Mth.wrapDegrees(o.toQuat(q2).difference(q1).angle());
					if (angle < 90 && angle < closestAngle) {
						closestIndex = i;
						closestAngle = angle;
					}
				}

				if (closestIndex != -1) {
					PacketDistributor.sendToServer(new SetLinkMsg(linkSourcePos, closestIndex, null));
				}
			} else {
				// add or replace
				@Nullable BlockHitResult[] connections = getConnections(level, linkSource, linkSourcePos);
				int firstNull = -1;

				for (int i = 0; i < connections.length; i++) {
					BlockHitResult h = connections[i];
					if (h == null) {
						if (firstNull == -1) firstNull = i;
					} else if (h.getBlockPos().equals(cursorPos)) {
						// duplicate detected; remove preexisting connection instead
						PacketDistributor.sendToServer(new SetLinkMsg(linkSourcePos, i, null));
						return;
					}
				}

				LinkSource.Orientation orientation = isCtrlPressed() ?
						LinkSource.Orientation.fromPosition(linkSourcePos, cursorLocation) :
						LinkSource.Orientation.fromPosition(linkSourcePos, cursorPos);

				PacketDistributor.sendToServer(new SetLinkMsg(linkSourcePos,
						firstNull == -1 ? connections.length - 1 : firstNull,
						orientation));
			}
		}

		public static @Nullable BlockHitResult @NotNull [] getConnections(Level level, LinkSource linkSource, BlockPos originPos) {
			Vec3 origin = Vec3.atCenterOf(originPos);
			BlockHitResult[] arr = new BlockHitResult[linkSource.maxLinks()];
			for (int i = 0; i < arr.length; i++) arr[i] = getConnection(level, linkSource, origin, i);
			return arr;
		}

		public static @Nullable BlockHitResult getConnection(Level level, LinkSource linkSource, Vec3 origin, int index) {
			double linkDistance = linkSource.linkDistance();
			LinkSource.Orientation o = linkSource.getLink(index);
			Vector3d vec = vecCache;

			if (o != null) {
				o.toVector(vec);
				return LuxUtils.safeClip(level, new ClipContext(
						origin.add(vec.x, vec.y, vec.z),
						new Vec3(
								origin.x + vec.x * linkDistance,
								origin.y + vec.y * linkDistance,
								origin.z + vec.z * linkDistance),
						ClipContext.Block.VISUAL, ClipContext.Fluid.ANY,
						CollisionContext.empty()));
			}
			return null;
		}

		public static boolean isCtrlPressed() {
			return Screen.hasControlDown();
		}
	}
}
