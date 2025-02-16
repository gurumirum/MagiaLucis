package gurumirum.magialucis.contents.block.artisanrytable;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.client.BaseScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArtisanryTableScreen extends BaseScreen<ArtisanryTableMenu> {
	private static final ResourceLocation TEXTURE = MagiaLucisApi.id("textures/gui/artisanry_table.png");
	private static final ResourceLocation BUTTON = MagiaLucisApi.id("artisanry_table_button");
	private static final ResourceLocation BUTTON_PRESSED = MagiaLucisApi.id("artisanry_table_button_pressed");
	private static final ResourceLocation BUTTON_JEI = MagiaLucisApi.id("artisanry_table_button_jei");
	private static final ResourceLocation PROGRESS = MagiaLucisApi.id("textures/gui/artisanry_table_progress.png");

	private static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(BUTTON, BUTTON_PRESSED, BUTTON, BUTTON_PRESSED);

	private ImageButton button;
	private Tooltip buttonTooltip;

	public ArtisanryTableScreen(ArtisanryTableMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}

	@Override
	protected void init() {
		super.init();

		if (this.buttonTooltip == null) {
			this.buttonTooltip = Tooltip.create(Component.literal("idk"));
		}

		this.button = addRenderableWidget(new ImageButton(
				getGuiLeft() + 140, getGuiTop() + 16,
				26, 16,
				BUTTON_SPRITES, b -> {
			if (this.minecraft != null && this.minecraft.gameMode != null) {
				this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId,
						ArtisanryTableMenu.BUTTON_BEGIN_RECIPE);
			}
		}));
		this.button.setTooltip(this.buttonTooltip);
	}

	@Override
	protected int computeImageWidth() {
		return ArtisanryTableMenu.WIDTH;
	}

	@Override
	protected int computeImageHeight() {
		return ArtisanryTableMenu.HEIGHT;
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		if (this.menu.canBeginRecipe()) {
			this.button.active = true;
			this.button.setTooltip(this.buttonTooltip);
		} else {
			this.button.active = false;
			this.button.setTooltip(null);
		}

		super.render(guiGraphics, mouseX, mouseY, partialTick);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		guiGraphics.blit(TEXTURE, getGuiLeft(), getGuiTop(),
				0, 0,
				ArtisanryTableMenu.ARTISANRY_TABLE_WIDTH, ArtisanryTableMenu.ARTISANRY_TABLE_HEIGHT,
				ArtisanryTableMenu.ARTISANRY_TABLE_WIDTH, ArtisanryTableMenu.ARTISANRY_TABLE_HEIGHT);

		if (this.menu.recipeInProgress()) {
			guiGraphics.blit(PROGRESS,
					getGuiLeft() + 145 + 16, getGuiTop() + 41,
					0, 0,
					2, 16,
					4, 16);

			double p = this.menu.progress() / (double)this.menu.totalProgress();
			int h = 16 - (int)(16 - 16 * p);

			guiGraphics.blit(PROGRESS,
					getGuiLeft() + 145 + 16, getGuiTop() + 41 + 16 - h,
					2, 16 - h,
					2, h,
					4, 16);
		}

		if (ModList.get().isLoaded("jei")) {
			guiGraphics.blitSprite(BUTTON_JEI, getGuiLeft() + 140, getGuiTop() + 66, 26, 16);
		}

		super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
	}

	@Override
	protected void renderSlotContents(@NotNull GuiGraphics guiGraphics, @NotNull ItemStack stack,
	                                  @NotNull Slot slot, @Nullable String countString) {
		if (!slot.isFake()) {
			super.renderSlotContents(guiGraphics, stack, slot, countString);
			return;
		}

		if (stack.isEmpty()) return;

		int seed = slot.x + slot.y * this.imageWidth;

		if (slot.isFake()) {
			guiGraphics.renderFakeItem(stack, slot.x, slot.y, seed);
		} else {
			guiGraphics.renderItem(stack, slot.x, slot.y, seed);
		}

		RenderSystem.disableDepthTest();
		RenderSystem.setShaderColor(1, 1, 1, 0.5f);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		guiGraphics.blit(TEXTURE, slot.x, slot.y, 145, 41, 16, 16,
				ArtisanryTableMenu.ARTISANRY_TABLE_WIDTH, ArtisanryTableMenu.ARTISANRY_TABLE_HEIGHT);
		RenderSystem.enableDepthTest();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.disableBlend();

		guiGraphics.renderItemDecorations(this.font, stack, slot.x, slot.y, countString);
	}
}
