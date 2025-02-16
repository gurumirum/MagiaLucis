package gurumirum.magialucis.datagen.builder;

import gurumirum.magialucis.api.capability.LuxStat;
import gurumirum.magialucis.contents.block.lux.lightbasin.LightBasinBlock;
import gurumirum.magialucis.contents.recipe.transfusion.LightBasinRecipe;
import gurumirum.magialucis.contents.recipe.transfusion.TransfusionRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LightBasinRecipeBuilder extends BaseTransfusionRecipeBuilder<LightBasinRecipeBuilder> {
	@Override
	protected TransfusionRecipe createRecipeInstance() {
		return new LightBasinRecipe(this.result, this.ingredients, this.processTicks, this.luxInput.build());
	}

	@Override
	protected @NotNull String defaultRecipePrefix() {
		return "light_basin/";
	}

	@Override
	protected @Nullable LuxStat absoluteMaxLuxInput() {
		return LightBasinBlock.STAT;
	}
}
