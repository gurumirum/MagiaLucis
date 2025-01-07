package gurumirum.gemthing.contents.item.wand;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.gemthing.utils.NumberFormats;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationWandGuiLayer implements LayeredDraw.Layer {
	public static final ResourceLocation BORDER = GemthingMod.id("textures/gui/config_wand_overlay_border.png");

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level != null &&
				mc.hitResult instanceof BlockHitResult blockHitResult &&
				blockHitResult.getType() == HitResult.Type.BLOCK &&
				mc.level.getBlockEntity(blockHitResult.getBlockPos()) instanceof LuxNodeBlockEntity be) {
			List<String> list = new ArrayList<>();

			list.add("Node: #" + be.luxNodeId() + " [" + be.getBlockPos().toShortString() + "]");

			list.add("Outbound Links:");
			for (var e : be.outboundLinks().int2ObjectEntrySet()) {
				if (e.getValue() == null) continue;
				list.add("#" + e.getIntKey() + " [" + BlockPos.containing(e.getValue().linkLocation()).toShortString() + "]");
			}
			list.add("Inbound Links:");
			for (var e : be.inboundLinks().int2ObjectEntrySet()) {
				if (e.getValue() == null) continue;
				list.add("#" + e.getIntKey() + " [" + e.getValue().origin().toShortString() + "]");
			}
			list.add("LUX Flow: " + be.influx(new Vector3d()).toString(NumberFormats.DECIMAL));
			list.add("color = " + be.color());
			list.add("minLuxThreshold = " + be.minLuxThreshold());
			list.add("rMaxTransfer = " + be.rMaxTransfer());
			list.add("gMaxTransfer = " + be.gMaxTransfer());
			list.add("bMaxTransfer = " + be.bMaxTransfer());

			drawDebugShit(guiGraphics, list);
		}

		List<String> overlayText = ConfigurationWandOverlay.visualData.overlayText;
		if (overlayText.isEmpty()) return;

		drawOverlayBox(guiGraphics, overlayText);

		overlayText.clear();
	}

	private static void drawDebugShit(@NotNull GuiGraphics guiGraphics, List<String> text) {
		Minecraft mc = Minecraft.getInstance();

		int left = mc.getWindow().getGuiScaledWidth() / 2 - 50;
		int top = mc.getWindow().getGuiScaledHeight() / 2;

		int width = 0;
		for (String t : text) {
			width = Math.max(width, mc.font.width(t));
		}
		width += 10;
		int height = 10 + (mc.font.lineHeight) * text.size() + 2 * (text.size() - 1);

		left -= width;
		top -= height / 2;

		drawBorder(guiGraphics, left, top, width, height);

		for (int i = 0; i < text.size(); i++) {
			guiGraphics.drawString(mc.font, text.get(i), left + 5, top + 5 + (mc.font.lineHeight + 2) * i,
					-1, true);
		}
	}

	private static void drawOverlayBox(@NotNull GuiGraphics guiGraphics, List<String> text) {
		Minecraft mc = Minecraft.getInstance();

		int left = mc.getWindow().getGuiScaledWidth() / 2 + 20;
		int top = mc.getWindow().getGuiScaledHeight() / 2 + 5;

		int width = 0;
		for (String t : text) width = Math.max(width, mc.font.width(t));
		width += 10;
		int height = 10 + (mc.font.lineHeight) * text.size() + 2 * (text.size() - 1);

		drawBorder(guiGraphics, left, top, width, height);

		for (int i = 0; i < text.size(); i++) {
			guiGraphics.drawString(mc.font, text.get(i), left + 5, top + 5 + (mc.font.lineHeight + 2) * i,
					-1, true);
		}
	}

	private static void drawBorder(@NotNull GuiGraphics guiGraphics, int left, int top, int width, int height) {
		int x1 = left + 4;
		int y1 = top + 4;
		int x2 = left + width - 4;
		int y2 = top + height - 4;

		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		guiGraphics.blit(BORDER, left, top, 4, 4, 0, 0, 4, 4, 9, 9);
		guiGraphics.blit(BORDER, left, y2, 4, 4, 0, 5, 4, 4, 9, 9);
		guiGraphics.blit(BORDER, x2, top, 4, 4, 5, 0, 4, 4, 9, 9);
		guiGraphics.blit(BORDER, x2, y2, 4, 4, 5, 5, 4, 4, 9, 9);

		guiGraphics.blit(BORDER, left, y1, 4, y2 - y1, 0, 4, 4, 1, 9, 9);
		guiGraphics.blit(BORDER, x2, y1, 4, y2 - y1, 5, 4, 4, 1, 9, 9);

		guiGraphics.blit(BORDER, x1, top, x2 - x1, 4, 4, 0, 1, 4, 9, 9);
		guiGraphics.blit(BORDER, x1, y2, x2 - x1, 4, 4, 5, 1, 4, 9, 9);

		guiGraphics.blit(BORDER, x1, y1, x2 - x1, y2 - y1, 4, 4, 1, 1, 9, 9);
		RenderSystem.disableBlend();
	}
}
