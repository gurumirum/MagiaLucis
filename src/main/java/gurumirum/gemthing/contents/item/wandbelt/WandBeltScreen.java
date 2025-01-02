package gurumirum.gemthing.contents.item.wandbelt;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.client.SharedGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class WandBeltScreen extends AbstractContainerScreen<WandBeltMenu> {
	public static final ResourceLocation TEXTURE = GemthingMod.id("textures/gui/wand_belt.png");

	public WandBeltScreen(WandBeltMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		this.imageWidth = WandBeltMenu.WIDTH;
		this.imageHeight = WandBeltMenu.HEIGHT;
		this.titleLabelX = WandBeltMenu.WIDTH / 2;
		this.titleLabelY = -11;
		this.inventoryLabelX = 8;
		this.inventoryLabelY = WandBeltMenu.PLAYER_INV_LABEL_Y;
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.menu.updateOneRow();
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		int selectedIndex = this.menu.selectedIndex();
		for (int i = 0; i < 18; i++) {
			if (selectedIndex == i) continue;
			Slot slot = this.menu.wandBeltSlots().get(i);
			if (!slot.isActive()) continue;
			drawWandBeltSlot(guiGraphics, slot, this.leftPos, this.topPos, false);
		}

		if (selectedIndex >= 0 && selectedIndex < 18) {
			Slot slot = this.menu.wandBeltSlots().get(selectedIndex);
			drawWandBeltSlot(guiGraphics, slot, this.leftPos, this.topPos, true);
		}

		SharedGUI.drawInventoryBg(this, guiGraphics, WandBeltMenu.PLAYER_INV_Y);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawCenteredString(this.font, this.title, this.titleLabelX, this.titleLabelY, -1);
		guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -1);
	}

	private static void drawWandBeltSlot(@NotNull GuiGraphics guiGraphics, Slot slot, int x, int y, boolean selected) {
		guiGraphics.blit(TEXTURE, x + slot.x - 8, y + slot.y - 8, 0, selected ? 32 : 0,
				32, 32, 32, 64);
	}
}
