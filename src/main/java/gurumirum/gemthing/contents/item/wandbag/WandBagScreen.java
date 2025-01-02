package gurumirum.gemthing.contents.item.wandbag;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.client.SharedGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class WandBagScreen extends AbstractContainerScreen<WandBagMenu> {
	public static final ResourceLocation TEXTURE = GemthingMod.id("textures/gui/wand_bag.png");

	public WandBagScreen(WandBagMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		this.imageWidth = WandBagMenu.WIDTH;
		this.imageHeight = WandBagMenu.HEIGHT;
		this.titleLabelX = WandBagMenu.WIDTH / 2;
		this.titleLabelY = -11;
		this.inventoryLabelX = 8;
		this.inventoryLabelY = WandBagMenu.PLAYER_INV_LABEL_Y;
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
			Slot slot = this.menu.wandBagSlots().get(i);
			if (!slot.isActive()) continue;
			slot(guiGraphics, slot, false);
		}

		if (selectedIndex >= 0 && selectedIndex < 18) {
			Slot slot = this.menu.wandBagSlots().get(selectedIndex);
			slot(guiGraphics, slot, true);
		}

		SharedGUI.drawInventoryBg(this, guiGraphics, WandBagMenu.PLAYER_INV_Y);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawCenteredString(this.font, this.title, this.titleLabelX, this.titleLabelY, -1);
		guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -1);
	}

	private void slot(@NotNull GuiGraphics guiGraphics, Slot slot, boolean selected) {
		guiGraphics.blit(TEXTURE, this.leftPos + slot.x - 8, this.topPos + slot.y - 8, 0, selected ? 32 : 0,
				32, 32, 32, 64);
	}
}
