package gurumirum.magialucis.datagen.builder;

import gurumirum.magialucis.contents.block.lux.lightbasin.LightBasinBlockEntity;
import gurumirum.magialucis.contents.recipe.transfusion.LightBasinRecipe;
import gurumirum.magialucis.contents.recipe.transfusion.TransfusionRecipe;
import org.jetbrains.annotations.NotNull;

public class LightBasinRecipeBuilder extends BaseTransfusionRecipeBuilder<LightBasinRecipeBuilder> {
	@Override
	protected TransfusionRecipe createRecipeInstance() {
		return new LightBasinRecipe(
				this.result, this.ingredients, this.processTicks,
				this.minLuxInputR, this.minLuxInputG, this.minLuxInputB, this.minLuxInputSum,
				this.maxLuxInputR, this.maxLuxInputG, this.maxLuxInputB, this.maxLuxInputSum);
	}

	@Override
	protected @NotNull String defaultRecipePrefix() {
		return "light_basin/";
	}

	@Override
	protected double absoluteMaxLuxInputR() {
		return LightBasinBlockEntity.STAT.rMaxTransfer();
	}

	@Override
	protected double absoluteMaxLuxInputG() {
		return LightBasinBlockEntity.STAT.gMaxTransfer();
	}

	@Override
	protected double absoluteMaxLuxInputB() {
		return LightBasinBlockEntity.STAT.bMaxTransfer();
	}
}
