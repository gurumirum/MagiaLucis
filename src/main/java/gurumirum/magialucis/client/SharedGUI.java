package gurumirum.magialucis.client;

import gurumirum.magialucis.api.MagiaLucisApi;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public final class SharedGUI {
	private SharedGUI() {}

	public static final int PLAYER_INV_WIDTH = 176;
	public static final int PLAYER_INV_LABEL_HEIGHT = 11;
	public static final int PLAYER_INV_HEIGHT = 90;

	public static final ResourceLocation PLAYER_INV_TEXTURE = MagiaLucisApi.id("textures/gui/inventory.png");

	public static final ResourceLocation LOCKED_SLOT = MagiaLucisApi.id("textures/slot/locked_slot.png");

	public static void drawInventoryBg(AbstractContainerScreen<?> screen, @NotNull GuiGraphics guiGraphics, int x, int y) {
		guiGraphics.blit(PLAYER_INV_TEXTURE, screen.getGuiLeft() + x, screen.getGuiTop() + y, 0, 0,
				PLAYER_INV_WIDTH, PLAYER_INV_HEIGHT, PLAYER_INV_WIDTH, PLAYER_INV_HEIGHT);
	}
}
