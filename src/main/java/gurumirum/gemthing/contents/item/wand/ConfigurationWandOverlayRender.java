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
public final class ConfigurationWandOverlayRender {
	private ConfigurationWandOverlayRender() {}

	private static final ResourceLocation BLOCK_HIGHLIGHT = id("textures/effect/block_highlight.png");

	private static final int TINT_SELECT = 0xee00ff00;
	private static final int TINT_MISSING = 0xeeffff00;
	private static final int TINT_REMOVE = 0xee800000;

	@SubscribeEvent
	public static void onRender(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

		overlayText.clear();

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

	static List<String> overlayText = new ArrayList<>();

	private static final Quaternionf q1 = new Quaternionf();
	private static final Quaternionf q2 = new Quaternionf();

	private static void updateAndDraw(RenderLevelStageEvent event, LocalPlayer player, Level level, ItemStack stack) {
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
			PoseStack poseStack = event.getPoseStack();
			poseStack.pushPose();
			setupCamera(poseStack, event.getCamera());

			int boxTint = TINT_SELECT;

			if (cursorHitPos != null && cursorHitPos.equals(linkSourcePos.pos())) {
				overlayText.add(ChatFormatting.YELLOW + "RClick" + ChatFormatting.RESET + " to unselect");
				overlayText.add(ChatFormatting.YELLOW + "SHIFT+RClick" + ChatFormatting.RESET + " to remove all links");

				if (player.isSecondaryUseActive()) {
					LinkSource linkSource = level.getCapability(ModCapabilities.LINK_SOURCE, linkSourcePos.pos());
					if (linkSource != null) {
						for (@Nullable BlockHitResult hitResult : getConnections(level, linkSource, cursorHitPos)) {
							if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
								drawLine(poseStack, cursorHitPos, hitResult.getLocation(),
										player.isSecondaryUseActive() ? TINT_REMOVE :
												level.getCapability(ModCapabilities.LINK_SOURCE, cursorHitPos) != null ?
														TINT_SELECT : TINT_MISSING);
							}
						}
					}

					boxTint = TINT_REMOVE;
				}
			} else {
				LinkSource linkSource = level.getCapability(ModCapabilities.LINK_SOURCE, linkSourcePos.pos());
				if (linkSource == null) {
					boxTint = TINT_MISSING;

					overlayText.add(ChatFormatting.RED + "Missing link origin at [" + linkSourcePos.pos().toShortString() + "]");
				} else {
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
									drawLine(poseStack, linkSourcePos.pos(), hitResult.getLocation(), TINT_REMOVE);
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
									drawLine(poseStack, linkSourcePos.pos(), h.getLocation(), TINT_REMOVE);
									removeLink = true;
									skipNewLink = true;
									break;
								}
							}

							if (!skipNewLink) {
								if (firstNull == -1) {
									drawLine(poseStack, linkSourcePos.pos(),
											Objects.requireNonNull(connections[connections.length - 1]).getLocation(),
											TINT_REMOVE);
								}
								drawLine(poseStack, linkSourcePos.pos(),
										isCtrlPressed() ? cursorHitLocation : Vec3.atCenterOf(cursorHitPos), TINT_SELECT);
							}
						}
					}

					if (removeLink) {
						overlayText.add(ChatFormatting.YELLOW + "RClick" + ChatFormatting.RESET + " to remove link");
					} else {
						overlayText.add(ChatFormatting.YELLOW + "RClick" + ChatFormatting.RESET + " to set link");
						overlayText.add(ChatFormatting.YELLOW + "SHIFT+RClick" + ChatFormatting.RESET + " to remove link");
						overlayText.add(ChatFormatting.YELLOW + "Ctrl" + ChatFormatting.RESET + " to enter free aim mode");
					}
				}
			}

			drawSelectionCube(poseStack, linkSourcePos.pos(), boxTint);
			poseStack.popPose();
		} else {
			if (cursorHitPos != null) {
				LinkSource linkSource = level.getCapability(ModCapabilities.LINK_SOURCE, cursorHitPos);
				if (linkSource != null) {
					PoseStack poseStack = event.getPoseStack();
					boolean setup = false;

					for (@Nullable BlockHitResult hitResult : getConnections(level, linkSource, cursorHitPos)) {
						if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
							if (!setup) {
								setup = true;
								poseStack.pushPose();
								setupCamera(poseStack, event.getCamera());
							}
							drawLine(poseStack, cursorHitPos, hitResult.getLocation(),
									player.isSecondaryUseActive() ? TINT_REMOVE :
											level.getCapability(ModCapabilities.LINK_SOURCE, cursorHitPos) != null ?
													TINT_SELECT : TINT_MISSING);
						}
					}

					if (setup) poseStack.popPose();

					overlayText.add(ChatFormatting.YELLOW + "RClick" + ChatFormatting.RESET + " to start link");
					overlayText.add(ChatFormatting.YELLOW + "SHIFT+RClick" + ChatFormatting.RESET + " to remove all links");
				}
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
}
