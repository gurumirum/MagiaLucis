package gurumirum.magialucis.contents.item.wand;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import gurumirum.magialucis.capability.LinkDestination;
import gurumirum.magialucis.capability.LinkSource;
import gurumirum.magialucis.capability.ModCapabilities;
import gurumirum.magialucis.client.render.ModRenderTypes;
import gurumirum.magialucis.client.render.RenderShapes;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.block.lux.LuxNodeSyncPropertyAccess;
import gurumirum.magialucis.impl.luxnet.*;
import gurumirum.magialucis.net.msgs.SetLinkMsg;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static gurumirum.magialucis.MagiaLucisMod.id;

public final class ConfigurationWandOverlay {
	private ConfigurationWandOverlay() {}

	public static final ResourceLocation BLOCK_HIGHLIGHT = id("textures/effect/block_highlight.png");

	private static final int TINT_SELECT = 0xee00ff00;
	private static final int TINT_MISSING = 0xeeffff00;
	private static final int TINT_REMOVE = 0xee800000;
	private static final int TINT_INPUT = 0xee284ea4;

	public static void render(RenderLevelStageEvent event) {
		visualData.overlayText.clear();
		visualData.active = false;

		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null || mc.options.hideGui) return;
		LocalPlayer player = mc.player;
		if (player == null) return;

		ItemStack stack = player.getMainHandItem();
		if (stack.getItem() instanceof ConfigurationWandItem) {
			visualData.active = true;
			updateAndDraw(event, player, mc.level, stack);
		} else {
			stack = player.getOffhandItem();
			if (stack.getItem() instanceof ConfigurationWandItem) {
				visualData.active = true;
				updateAndDraw(event, player, mc.level, stack);
			}
		}
	}

	static final OverlayVisualData visualData = new OverlayVisualData();

	private static void updateAndDraw(RenderLevelStageEvent event, LocalPlayer player, Level level, ItemStack stack) {
		update(player, level, stack);

		if (visualData.boxes.isEmpty() && visualData.lines.isEmpty()) return;

		PoseStack poseStack = event.getPoseStack();
		poseStack.pushPose();
		Vec3 cameraPos = event.getCamera().getPosition();
		poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

		MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

		if (!visualData.boxes.isEmpty()) {
			VertexConsumer vc = bufferSource.getBuffer(ModRenderTypes.BLOCK_HIGHLIGHT_BOX);

			for (Box box : visualData.boxes) {
				RenderShapes.texturedTintedBox(poseStack, vc,
						box.pos.getX() + -1 / 16f, box.pos.getY() + -1 / 16f, box.pos.getZ() + -1 / 16f,
						box.pos.getX() + 17 / 16f, box.pos.getY() + 17 / 16f, box.pos.getZ() + 17 / 16f,
						box.tint);
			}
		}

		if (!visualData.lines.isEmpty()) {
			VertexConsumer vc = bufferSource.getBuffer(ModRenderTypes.BLOCK_HIGHLIGHT_LINE);

			for (int i = 0; i < visualData.lines.size(); i++) {
				Line line = visualData.lines.get(i);

				Vector3f vec = new Vector3f(line.start.getX() + .5f - (float)line.end.x,
						line.start.getY() + .5f - (float)line.end.y,
						line.start.getZ() + .5f - (float)line.end.z);

				poseStack.pushPose();
				poseStack.translate(line.start.getX() + .5, line.start.getY() + .5, line.start.getZ() + .5);
				poseStack.mulPose(new Vector3f(0, 0, -1).rotationTo(vec, new Quaternionf()));
				poseStack.mulPose(Axis.ZP.rotation(i));

				poseStack.scale(0.1f, 0.1f, 1);

				RenderShapes.untexturedZGradientBox(
						poseStack,
						vc,
						-.5f, -.5f, 0,
						.5f, .5f, vec.length() + .1f,
						line.tint, line.tint);

				poseStack.popPose();
			}
		}

		poseStack.popPose();

		visualData.boxes.clear();
		visualData.lines.clear();
	}

	private static void update(LocalPlayer player, Level level, ItemStack stack) {
		Minecraft mc = Minecraft.getInstance();
		GlobalPos linkSourcePos = stack.get(ModDataComponents.LINK_SOURCE);
		@Nullable BlockHitResult cursor;

		if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK &&
				mc.hitResult instanceof BlockHitResult _blockHit) {
			cursor = _blockHit;
		} else {
			cursor = null;
		}

		if (linkSourcePos != null &&
				linkSourcePos.dimension().equals(level.dimension()) &&
				level.isLoaded(linkSourcePos.pos())) {

			LinkSource linkSource = level.getCapability(ModCapabilities.LINK_SOURCE, linkSourcePos.pos());
			if (linkSource == null) {
				visualData.boxes.add(new Box(linkSourcePos.pos(), TINT_MISSING));
				visualData.overlayText.add(Component.translatable("gui.magialucis.configuration_wand.missing_link",
						ChatFormatting.RED + linkSourcePos.pos().toShortString()));
				return;
			}

			if (cursor != null && cursor.getBlockPos().equals(linkSourcePos.pos())) {
				visualData.overlayText.add(Component.translatable("gui.magialucis.configuration_wand.on_block.1"));
				visualData.overlayText.add(Component.translatable("gui.magialucis.configuration_wand.on_block.2"));

				if (player.isSecondaryUseActive()) {
					visualData.boxes.add(new Box(linkSourcePos.pos(), TINT_REMOVE));
					addAllConnections(level, linkSourcePos.pos(), linkSource, true);
				} else {
					visualData.boxes.add(new Box(linkSourcePos.pos(), TINT_SELECT));
				}
				return;
			} else if (appendLinkPreview(player, level, linkSourcePos, linkSource, cursor)) {
				return;
			}
		}

		if (cursor != null) {
			LinkSource linkSource = level.getCapability(ModCapabilities.LINK_SOURCE, cursor.getBlockPos());
			addAllConnections(level, cursor.getBlockPos(), linkSource, player.isSecondaryUseActive());
			if (linkSource != null) {
				visualData.overlayText.add(Component.translatable("gui.magialucis.configuration_wand.start_link.1"));
				visualData.overlayText.add(Component.translatable("gui.magialucis.configuration_wand.start_link.2"));
			}
		}
	}

	private static boolean appendLinkPreview(LocalPlayer player, Level level, GlobalPos linkSourcePos,
	                                         LinkSource linkSource, @Nullable BlockHitResult cursor) {
		SetLinkMsg msg = ConfigurationWandItem.ClientLogic.calculateLink(level, player, linkSourcePos.pos(), linkSource,
				cursor != null ? cursor.getBlockPos() : null,
				cursor != null ? cursor.getLocation() : player.getLookAngle().scale(5).add(player.getEyePosition()));

		if (msg != null) {
			@Nullable InWorldLinkState linkState = linkSource.getLinkState(msg.index());

			if (msg.orientation() == null) {
				if (linkState != null) {
					visualData.lines.add(new Line(linkState.origin(), linkState.linkLocation(), TINT_REMOVE));
				}
			} else {
				if (linkState != null) {
					visualData.lines.add(new Line(linkState.origin(), linkState.linkLocation(), TINT_REMOVE));
				}

				BlockHitResult connection = LuxUtils.traceConnection(level, linkSourcePos.pos(),
						msg.orientation().xRot(), msg.orientation().yRot(),
						linkSource.linkDistance());

				LinkDestinationSelector dstSelector = linkSource.linkDestinationSelector();
				if (dstSelector == null) dstSelector = LinkDestinationSelector.DEFAULT;
				LinkDestination dst = dstSelector.chooseLinkDestination(level, null, connection);

				boolean linkable = dst != null && dst.linkWithSource(
								new LinkContext(level, linkSource.clientSideInterface(), connection))
						.isLinkable();
				visualData.lines.add(new Line(linkSourcePos.pos(), connection.getLocation(),
						linkable ? TINT_SELECT : TINT_MISSING));
			}
		}

		if (player.isSecondaryUseActive()) {
			visualData.overlayText.add(Component.translatable("gui.magialucis.configuration_wand.remove_link"));
		} else {
			visualData.overlayText.add(Component.translatable("gui.magialucis.configuration_wand.set_link.1"));
			visualData.overlayText.add(Component.translatable("gui.magialucis.configuration_wand.set_link.2"));
			visualData.overlayText.add(Component.translatable("gui.magialucis.configuration_wand.set_link.3"));
		}

		visualData.boxes.add(new Box(linkSourcePos.pos(), TINT_SELECT));
		return true;
	}

	private static void addAllConnections(Level level, BlockPos pos, @Nullable LinkSource linkSource, boolean remove) {
		if (!(level.getBlockEntity(pos) instanceof LuxNodeSyncPropertyAccess props)) return;

		for (var e : props.outboundLinks().int2ObjectEntrySet()) {
			InWorldLinkInfo linkInfo = e.getValue();
			if (linkInfo == null) continue;
			visualData.lines.add(new Line(linkInfo.origin(), linkInfo.linkLocation(),
					remove && linkSource != null ? TINT_REMOVE : TINT_SELECT));
		}

		for (InWorldLinkState linkState : props.linkStates()) {
			if (linkState.linked()) continue;
			visualData.lines.add(new Line(linkState.origin(), linkState.linkLocation(),
					remove ? TINT_REMOVE : TINT_MISSING));
		}

		for (var e : props.inboundLinks().int2ObjectEntrySet()) {
			InWorldLinkInfo linkInfo = e.getValue();
			if (linkInfo == null) continue;
			visualData.lines.add(new Line(linkInfo.origin(), linkInfo.linkLocation(), TINT_INPUT));
		}
	}

	public static final class OverlayVisualData {
		public boolean active;
		public final List<Box> boxes = new ArrayList<>();
		public final List<Line> lines = new ArrayList<>();
		public final List<Component> overlayText = new ArrayList<>();
	}

	public record Box(BlockPos pos, int tint) {}
	public record Line(BlockPos start, Vec3 end, int tint) {}
}
