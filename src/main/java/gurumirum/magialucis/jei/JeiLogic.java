package gurumirum.magialucis.jei;

import gurumirum.magialucis.utils.NumberFormats;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;

public final class JeiLogic {
	private JeiLogic() {}

	public static void processTimeWidget(IRecipeCategory<?> recipeCategory, IRecipeExtrasBuilder builder, int processTicks) {
		if (processTicks <= 0) return;

		double seconds = Math.floor((processTicks / 20.0) * 100) / 100;

		Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds",
				NumberFormats.dec(seconds, null));
		builder.addText(timeString, recipeCategory.getWidth() - 20, 10)
				.setPosition(0, 0, recipeCategory.getWidth(), recipeCategory.getHeight(),
						HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM)
				.setTextAlignment(HorizontalAlignment.RIGHT)
				.setTextAlignment(VerticalAlignment.BOTTOM)
				.setColor(0xFF808080);
	}
}
