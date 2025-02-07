package gurumirum.magialucis.jei;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.contents.recipe.LuxInputCondition;
import gurumirum.magialucis.impl.LuxStatTooltip;
import gurumirum.magialucis.utils.NumberFormats;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class LuxInputWidget implements IRecipeWidget {
	public static final int WIDTH = 14;
	public static final int HEIGHT = 14;

	public static final ResourceLocation ICON = MagiaLucisMod.id("textures/gui/jei_light_icon.png");
	public static final ResourceLocation ICON_BG = MagiaLucisMod.id("textures/gui/jei_light_icon_bg.png");

	private final LuxInputCondition luxInputCondition;
	private final int x;
	private final int y;

	private final Vector3f color = new Vector3f();

	private @Nullable List<FormattedText> tooltip;

	public LuxInputWidget(LuxInputCondition luxInputCondition, int x, int y) {
		this.luxInputCondition = luxInputCondition;
		this.x = x;
		this.y = y;

		double r = luxInputCondition.minR(), g = luxInputCondition.minG(), b = luxInputCondition.minB();
		double maxComponent = Math.max(Math.max(r, g), b);

		if (maxComponent > 0) {
			this.color.set(r / maxComponent, g / maxComponent, b / maxComponent);
			this.color.add(1, 1, 1);
			this.color.mul(.5f);
		} else {
			this.color.set(1);
		}
	}

	@Override
	public @NotNull ScreenPosition getPosition() {
		return new ScreenPosition(this.x, this.y);
	}

	@Override
	public void drawWidget(@NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
		guiGraphics.setColor(1, 1, 1, 1);
		guiGraphics.blit(ICON_BG, 0, 0, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
		guiGraphics.setColor(this.color.x, this.color.y, this.color.z, 1);
		guiGraphics.blit(ICON, 0, 0, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
		guiGraphics.setColor(1, 1, 1, 1);
	}

	@Override
	public void getTooltip(@NotNull ITooltipBuilder tooltip, double mouseX, double mouseY) {
		if (mouseX >= 0 && mouseX < WIDTH && mouseY >= 0 && mouseY < HEIGHT) {
			if (this.tooltip == null) {
				this.tooltip = new ArrayList<>();

				format(this.tooltip, luxInputCondition, LuxInputCondition.Component.R, true);
				format(this.tooltip, luxInputCondition, LuxInputCondition.Component.G, true);
				format(this.tooltip, luxInputCondition, LuxInputCondition.Component.B, true);
				format(this.tooltip, luxInputCondition, LuxInputCondition.Component.SUM, true);

				if (!this.tooltip.isEmpty()) this.tooltip.addFirst(Component.literal("Min. LUX Input:"));

				int index = this.tooltip.size();

				format(this.tooltip, luxInputCondition, LuxInputCondition.Component.R, false);
				format(this.tooltip, luxInputCondition, LuxInputCondition.Component.G, false);
				format(this.tooltip, luxInputCondition, LuxInputCondition.Component.B, false);
				format(this.tooltip, luxInputCondition, LuxInputCondition.Component.SUM, false);

				if (this.tooltip.size() != index) this.tooltip.add(index, Component.literal("Max. LUX Input:"));

				if (!this.tooltip.isEmpty()) {
					if (luxInputCondition.minProgress() != 1) {
						this.tooltip.add(Component.literal("Min. Progress: " +
								NumberFormats.pct(luxInputCondition.minProgress(), null)));
					}
					if (luxInputCondition.maxProgress() != 1) {
						this.tooltip.add(Component.literal("Max. Progress: " +
								NumberFormats.pct(luxInputCondition.maxProgress(), null)));
					}
				}
			}
			tooltip.addAll(this.tooltip);
		}
	}

	private static void format(List<FormattedText> text,
	                           LuxInputCondition luxInputCondition,
	                           LuxInputCondition.Component component,
	                           boolean min) {
		double value = luxInputCondition.get(component, min);
		double defaultValue = LuxInputCondition.none().get(component, min);

		if (value != defaultValue) {
			MutableComponent componentMeter = LuxStatTooltip.componentMeter(value);

			switch (component) {
				case R -> componentMeter.withStyle(ChatFormatting.RED);
				case G -> componentMeter.withStyle(ChatFormatting.GREEN);
				case B -> componentMeter.withStyle(ChatFormatting.BLUE);
			}

			text.add(Component.literal(" ").append(Component.translatable(
					switch (component) {
						case R -> "item.magialucis.tooltip.lux_transfer_rate.r";
						case G -> "item.magialucis.tooltip.lux_transfer_rate.g";
						case B -> "item.magialucis.tooltip.lux_transfer_rate.b";
						case SUM -> "item.magialucis.tooltip.lux_transfer_rate.sum";
					}, componentMeter)));
		}
	}
}
