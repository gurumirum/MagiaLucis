package gurumirum.gemthing.contents.item.wandbelt;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import gurumirum.gemthing.client.ModKeyMappings;
import gurumirum.gemthing.contents.ModItems;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jetbrains.annotations.NotNull;

public class WandBeltGuiLayer implements LayeredDraw.Layer {
	private float alpha;

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.player.isDeadOrDying() || mc.screen != null) {
			this.alpha = 0;
			return;
		}

		ItemStack wandBeltStack = WandBelt.get(mc.player);
		if (!wandBeltStack.is(ModItems.WAND_BELT.asItem())) {
			this.alpha = 0;
			return;
		}

		float delta = deltaTracker.getRealtimeDeltaTicks() * 0.25f;
		boolean keyDown = ModKeyMappings.CHANGE_WAND.isDown();
		if (keyDown) this.alpha = Math.min(1, this.alpha + delta);
		else this.alpha = Math.max(0, this.alpha - delta);

		if (this.alpha <= 0) return;
		boolean transparent = this.alpha < 1;

		guiGraphics.setColor(1, 1, 1, this.alpha);
		if (transparent) {
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		}

		ItemContainerContents container = wandBeltStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
		int selectedIndex = WandBeltItem.getSelectedIndex(wandBeltStack);

		boolean oneRow = WandBeltClientEvents.computeOneRow(container, selectedIndex);

		int left = (mc.getWindow().getGuiScaledWidth() - WandBeltMenu.WAND_BELT_WIDTH) / 2;
		int top = mc.getWindow().getGuiScaledHeight() / 2 + 10;

		RenderSystem.setShader(GameRenderer::getPositionTexShader);

		for (int i = 0, slots = oneRow ? 9 : 18; i < slots; i++) {
			if (selectedIndex == i) continue;

			drawWandBeltSlot(guiGraphics,
					left + 18 * (i % 9),
					top + (oneRow ? 9 : 18 * (i / 9)),
					false,
					isEmpty(container, i));
		}

		if (selectedIndex >= 0 && selectedIndex < 18) {
			drawWandBeltSlot(guiGraphics,
					left + 18 * (selectedIndex % 9),
					top + (oneRow ? 9 : 18 * (selectedIndex / 9)),
					true,
					isEmpty(container, selectedIndex));
		}

		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(0, 0, 200);

		for (int i = 0, slots = Math.min(oneRow ? 9 : 18, container.getSlots()); i < slots; i++) {
			ItemStack stack = container.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			int x = left + 18 * (i % 9) + 8;
			int y = top + (oneRow ? 9 : 18 * (i / 9)) + 8;
			guiGraphics.renderItem(stack, x, y);
			guiGraphics.renderItemDecorations(mc.font, stack, x, y, "");
		}

		guiGraphics.pose().popPose();
		if (transparent) {
			guiGraphics.setColor(1, 1, 1, 1);
			RenderSystem.disableBlend();
		}
	}

	private static boolean isEmpty(ItemContainerContents container, int slot) {
		if (slot < 0 || slot >= container.getSlots()) return true;
		ItemStack stack = container.getStackInSlot(slot);
		return stack.isEmpty();
	}

	private static void drawWandBeltSlot(@NotNull GuiGraphics guiGraphics, int x, int y, boolean selected, boolean empty) {
		RenderSystem.setShaderTexture(0, WandBeltScreen.TEXTURE);
		guiGraphics.blit(WandBeltScreen.TEXTURE, x - 8, y - 8, 0, selected ? 32 : 0,
				32, 32, 32, 64);
		if (empty) {
			guiGraphics.blit(WandBeltScreen.EMPTY_SLOT_TEXTURE, x, y, 0, 0,
					16, 16, 16, 16);
		}
	}
}
