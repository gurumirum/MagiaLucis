package gurumirum.magialucis.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

public abstract class BaseScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
	private int invX, invY;

	public BaseScreen(T menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}

	public final int invX() {
		return this.invX;
	}
	public final int invY() {
		return this.invY;
	}

	protected abstract int computeImageWidth();
	protected abstract int computeImageHeight();

	protected int computeInventoryX() {
		return 0;
	}

	protected int computeInventoryY() {
		return this.imageHeight - SharedGUI.PLAYER_INV_HEIGHT;
	}

	protected int computeInventoryLabelX() {
		return invX() + 8;
	}
	protected int computeInventoryLabelY() {
		return invY() - SharedGUI.PLAYER_INV_LABEL_HEIGHT;
	}

	@Override
	protected void init() {
		this.imageWidth = computeImageWidth();
		this.imageHeight = computeImageHeight();
		this.invX = computeInventoryX();
		this.invY = computeInventoryY();

		this.titleLabelX = this.imageWidth / 2;
		this.titleLabelY = -11;

		this.inventoryLabelX = computeInventoryLabelX();
		this.inventoryLabelY = computeInventoryLabelY();

		super.init();
	}

	@Override
	protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		SharedGUI.drawInventoryBg(this, guiGraphics, invX(), invY());
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawCenteredString(this.font, this.title, this.titleLabelX, this.titleLabelY, -1);
		guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -1);
	}
}
