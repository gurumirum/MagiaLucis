package gurumirum.magialucis.contents.item.wandbelt;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.client.SharedGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class WandBeltScreen extends AbstractContainerScreen<WandBeltMenu> {
	public static final ResourceLocation TEXTURE = MagiaLucisMod.id("textures/gui/wand_belt.png");
	public static final ResourceLocation EMPTY_SLOT_TEXTURE = MagiaLucisMod.id("textures/slot/empty_slot_wand.png");

	private final Inventory playerInventory;

	public WandBeltScreen(WandBeltMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		this.playerInventory = playerInventory;

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
			drawWandBeltSlot(guiGraphics, slot, this.leftPos, this.topPos, false, !slot.hasItem());
		}

		if (selectedIndex >= 0 && selectedIndex < 18) {
			Slot slot = this.menu.wandBeltSlots().get(selectedIndex);
			drawWandBeltSlot(guiGraphics, slot, this.leftPos, this.topPos, true, !slot.hasItem());
		}

		SharedGUI.drawInventoryBg(this, guiGraphics, WandBeltMenu.PLAYER_INV_Y);
	}

	@Override
	protected void renderSlot(@NotNull GuiGraphics guiGraphics, @NotNull Slot slot) {
		super.renderSlot(guiGraphics, slot);

		if (isLocked(slot)) {
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.disableDepthTest();
			guiGraphics.blit(SharedGUI.LOCKED_SLOT, slot.x, slot.y, 0, 0,
					16, 16, 16, 16);
			RenderSystem.disableBlend();
			RenderSystem.enableDepthTest();
		}
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawCenteredString(this.font, this.title, this.titleLabelX, this.titleLabelY, -1);
		guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -1);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.minecraft == null) return false;

		if (button != 0 && button != 1 && !this.minecraft.options.keyPickItem
				.isActiveAndMatches(InputConstants.Type.MOUSE.getOrCreate(button))) {
			if (this.menu.wandBeltInventoryIndex() < 0) return false; // offhand inv, disable offhand swap entirely
			Slot slot = findSlot(mouseX, mouseY);
			if (slot != null && isLocked(slot)) return false; // otherwise check if the slot is locked
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	private @Nullable Slot findSlot(double mouseX, double mouseY) {
		for (int i = 0; i < this.menu.slots.size(); i++) {
			Slot slot = this.menu.slots.get(i);
			if (isHovering(slot, mouseX, mouseY) && slot.isActive()) return slot;
		}

		return null;
	}

	private boolean isHovering(Slot slot, double mouseX, double mouseY) {
		return this.isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY);
	}

	private boolean isLocked(Slot slot) {
		int wandBeltInventoryIndex = this.menu.wandBeltInventoryIndex();
		return wandBeltInventoryIndex >= 0 && slot.container == this.playerInventory &&
				slot.getSlotIndex() == wandBeltInventoryIndex;
	}

	private static void drawWandBeltSlot(@NotNull GuiGraphics guiGraphics, Slot slot, int x, int y,
	                                     boolean selected, boolean empty) {
		guiGraphics.blit(TEXTURE, x + slot.x - 8, y + slot.y - 8, 0, selected ? 32 : 0,
				32, 32, 32, 64);
		if (empty) {
			guiGraphics.blit(EMPTY_SLOT_TEXTURE, x + slot.x, y + slot.y, 0, 0,
					16, 16, 16, 16);
		}
	}
}
