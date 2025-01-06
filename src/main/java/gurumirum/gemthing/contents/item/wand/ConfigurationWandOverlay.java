package gurumirum.gemthing.contents.item.wand;

import com.mojang.blaze3d.vertex.PoseStack;
import gurumirum.gemthing.capability.LinkSource;
import gurumirum.gemthing.capability.ModCapabilities;
import gurumirum.gemthing.client.ModRenderTypes;
import gurumirum.gemthing.client.RenderShapes;
import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.contents.Wands;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static gurumirum.gemthing.GemthingMod.MODID;
import static gurumirum.gemthing.GemthingMod.id;
import static gurumirum.gemthing.contents.item.wand.ConfigurationWandItem.ClientFunction.*;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class ConfigurationWandOverlay {
	private ConfigurationWandOverlay() {}

	private static final ResourceLocation BLOCK_HIGHLIGHT = id("textures/effect/block_highlight.png");

	private static final int TINT_SELECT = 0xee00ff00;
	private static final int TINT_MISSING = 0xeeffff00;
	private static final int TINT_REMOVE = 0xee800000;

	@SubscribeEvent
	public static void onRender(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

		visualData.overlayText.clear();

		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) return;
		LocalPlayer player = mc.player;
		if (player == null) return;

		ItemStack stack = player.getMainHandItem();
		if (stack.is(Wands.CONFIGURATION_WAND.asItem())) {
			updateAndDraw(event, player, mc.level, stack);
		} else {
			stack = player.getOffhandItem();
			if (stack.is(Wands.CONFIGURATION_WAND.asItem())) {
				updateAndDraw(event, player, mc.level, stack);
			}
		}
	}

	static final OverlayVisualData visualData = new OverlayVisualData();

	private static final Quaternionf q1 = new Quaternionf();
	private static final Quaternionf q2 = new Quaternionf();

	private static void updateAndDraw(RenderLevelStageEvent event, LocalPlayer player, Level level, ItemStack stack) {
		update(player, level, stack);

		if (visualData.boxes.isEmpty() && visualData.lines.isEmpty()) return;

		PoseStack poseStack = event.getPoseStack();
		poseStack.pushPose();
		setupCamera(poseStack, event.getCamera());
		for (Box box : visualData.boxes) drawSelectionCube(poseStack, box.pos, box.tint);
		for (Line line : visualData.lines) drawLine(poseStack, line.start, line.end, line.tint);
		poseStack.popPose();

		visualData.boxes.clear();
		visualData.lines.clear();
	}

	private static void update(LocalPlayer player, Level level, ItemStack stack) {
		Minecraft mc = Minecraft.getInstance();
		GlobalPos linkSourcePos = stack.get(Contents.LINK_SOURCE);
		@Nullable BlockPos cursorHitPos;
		@Nullable Vec3 cursorHitLocation;

		if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK &&
				mc.hitResult instanceof BlockHitResult blockHit) {
			cursorHitPos = blockHit.getBlockPos();
			cursorHitLocation = blockHit.getLocation();
		} else {
			cursorHitPos = null;
			cursorHitLocation = null;
		}

		if (linkSourcePos != null &&
				linkSourcePos.dimension().equals(level.dimension()) &&
				level.isLoaded(linkSourcePos.pos())) {
			int boxTint = TINT_SELECT;

			LinkSource linkSource = level.getCapability(ModCapabilities.LINK_SOURCE, linkSourcePos.pos());
			if (linkSource == null) {
				boxTint = TINT_MISSING;
				visualData.overlayText.add(ChatFormatting.RED + "Missing link origin at [" + linkSourcePos.pos().toShortString() + "]");
			}

			if (cursorHitPos != null && cursorHitPos.equals(linkSourcePos.pos())) {
				visualData.overlayText.add(ChatFormatting.YELLOW + "RClick" + ChatFormatting.RESET + " to unselect");
				visualData.overlayText.add(ChatFormatting.YELLOW + "SHIFT+RClick" + ChatFormatting.RESET + " to remove all links");

				if (player.isSecondaryUseActive()) {
					if (linkSource != null) {
						for (@Nullable BlockHitResult hitResult : getConnections(level, linkSource, cursorHitPos)) {
							if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
								visualData.lines.add(new Line(cursorHitPos, hitResult.getLocation(), TINT_REMOVE));
							}
						}
					}

					boxTint = TINT_REMOVE;
				}
				visualData.boxes.add(new Box(linkSourcePos.pos(), boxTint));
				return;
			} else if (linkSource != null) {
				boolean removeLink = false;

				if (cursorHitPos != null) {
					if (player.isSecondaryUseActive()) {
						LinkSource.Orientation orientation = isCtrlPressed() ?
								LinkSource.Orientation.fromPosition(linkSourcePos.pos(), cursorHitLocation) :
								LinkSource.Orientation.fromPosition(linkSourcePos.pos(), cursorHitPos);

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
							BlockHitResult hitResult = getConnection(level, linkSource, Vec3.atCenterOf(linkSourcePos.pos()), closestIndex);
							if (hitResult != null)
								visualData.lines.add(new Line(linkSourcePos.pos(), hitResult.getLocation(), TINT_REMOVE));
						}
					} else {
						@Nullable BlockHitResult[] connections = getConnections(level, linkSource, linkSourcePos.pos());
						int firstNull = -1;
						boolean skipNewLink = false;

						for (int i = 0; i < connections.length; i++) {
							BlockHitResult h = connections[i];
							if (h == null) {
								if (firstNull == -1) firstNull = i;
							} else if (h.getBlockPos().equals(cursorHitPos)) {
								// duplicate detected; remove preexisting connection instead
								visualData.lines.add(new Line(linkSourcePos.pos(), h.getLocation(), TINT_REMOVE));
								removeLink = true;
								skipNewLink = true;
								break;
							}
						}

						if (!skipNewLink) {
							if (firstNull == -1) {
								visualData.lines.add(new Line(linkSourcePos.pos(),
										Objects.requireNonNull(connections[connections.length - 1]).getLocation(),
										TINT_REMOVE));
							}
							visualData.lines.add(new Line(linkSourcePos.pos(),
									isCtrlPressed() ? cursorHitLocation : Vec3.atCenterOf(cursorHitPos), TINT_SELECT));
						}
					}
				}

				if (removeLink) {
					visualData.overlayText.add(ChatFormatting.YELLOW + "RClick" + ChatFormatting.RESET + " to remove link");
				} else {
					visualData.overlayText.add(ChatFormatting.YELLOW + "RClick" + ChatFormatting.RESET + " to set link");
					visualData.overlayText.add(ChatFormatting.YELLOW + "SHIFT+RClick" + ChatFormatting.RESET + " to remove link");
					visualData.overlayText.add(ChatFormatting.YELLOW + "Ctrl" + ChatFormatting.RESET + " to enter free aim mode");
				}

				visualData.boxes.add(new Box(linkSourcePos.pos(), boxTint));
				return;
			} else {
				visualData.boxes.add(new Box(linkSourcePos.pos(), boxTint));
				// missing src, check for cursor
			}
		}

		if (cursorHitPos != null) {
			LinkSource linkSource = level.getCapability(ModCapabilities.LINK_SOURCE, cursorHitPos);
			if (linkSource != null) {
				for (@Nullable BlockHitResult hitResult : getConnections(level, linkSource, cursorHitPos)) {
					if (hitResult != null) {
						visualData.lines.add(new Line(cursorHitPos, hitResult.getLocation(),
								player.isSecondaryUseActive() ? TINT_REMOVE :
										hitResult.getType() == HitResult.Type.BLOCK &&
												level.getCapability(ModCapabilities.LINK_SOURCE, hitResult.getBlockPos()) != null ?
												TINT_SELECT : TINT_MISSING));
					}
				}

				visualData.overlayText.add(ChatFormatting.YELLOW + "RClick" + ChatFormatting.RESET + " to start link");
				visualData.overlayText.add(ChatFormatting.YELLOW + "SHIFT+RClick" + ChatFormatting.RESET + " to remove all links");
			}
		}
	}

	private static void setupCamera(PoseStack poseStack, Camera camera) {
		Vec3 cameraPos = camera.getPosition();
		poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
	}

	private static void drawSelectionCube(PoseStack poseStack, BlockPos pos, int tint) {
		RenderShapes.texturedTintedBox(poseStack, Minecraft.getInstance().renderBuffers().bufferSource()
						.getBuffer(ModRenderTypes.blockHighlight(BLOCK_HIGHLIGHT)),
				pos.getX() + -1 / 16f, pos.getY() + -1 / 16f, pos.getZ() + -1 / 16f,
				pos.getX() + 17 / 16f, pos.getY() + 17 / 16f, pos.getZ() + 17 / 16f,
				tint);
	}

	private static void drawLine(PoseStack poseStack, BlockPos start, Vec3 end, int tint) {
		Vector3f vec = new Vector3f(start.getX() + .5f - (float)end.x,
				start.getY() + .5f - (float)end.y,
				start.getZ() + .5f - (float)end.z);

		poseStack.pushPose();
		poseStack.translate(start.getX() + .5, start.getY() + .5, start.getZ() + .5);
		poseStack.mulPose(new Vector3f(0, 0, -1).rotationTo(vec, new Quaternionf()));

		poseStack.scale(0.1f, 0.1f, 1);

		RenderShapes.untexturedZGradientBox(
				poseStack,
				Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(ModRenderTypes.BEAM),
				-.5f, -.5f, 0,
				.5f, .5f, vec.length() + .1f,
				tint, tint);

		poseStack.popPose();
	}

	public static class OverlayVisualData {
		public final List<Box> boxes = new ArrayList<>();
		public final List<Line> lines = new ArrayList<>();
		public final List<String> overlayText = new ArrayList<>();
	}

	public record Box(BlockPos pos, int tint) {}
	public record Line(BlockPos start, Vec3 end, int tint) {}
}
