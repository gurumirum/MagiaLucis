package gurumirum.gemthing.contents.item.wand;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import gurumirum.gemthing.GemthingMod;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfigurationWandGuiLayer implements LayeredDraw.Layer {
	public static final ResourceLocation BORDER = GemthingMod.id("config_wand_overlay_border");

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
		List<String> overlayText = ConfigurationWandOverlayRender.overlayText;
		if (overlayText.isEmpty()) return;

		drawOverlayBox(guiGraphics, overlayText);

		overlayText.clear();
	}

	private static void drawOverlayBox(@NotNull GuiGraphics guiGraphics, List<String> text) {
		Minecraft mc = Minecraft.getInstance();

		int left = mc.getWindow().getGuiScaledWidth() / 2 + 20;
		int top = mc.getWindow().getGuiScaledHeight() / 2 + 5;

		int width = 0;
		for (String t : text) width = Math.max(width, mc.font.width(t));
		width += 10;
		int height = 10 + (mc.font.lineHeight) * text.size() + 2 * (text.size() - 1);

		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		guiGraphics.blitSprite(BORDER, left, top, width, height);
		RenderSystem.disableBlend();

		for (int i = 0; i < text.size(); i++) {
			guiGraphics.drawString(mc.font, text.get(i), left + 5, top + 5 + (mc.font.lineHeight + 2) * i,
					-1, true);
		}
	}
}
