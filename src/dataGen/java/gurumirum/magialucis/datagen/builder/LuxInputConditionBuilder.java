package gurumirum.magialucis.datagen.builder;

import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.recipe.LuxInputCondition;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LuxInputConditionBuilder {
	private double minR = 0;
	private double minG = 0;
	private double minB = 0;
	private double minSum = 0;
	private double maxR = Double.POSITIVE_INFINITY;
	private double maxG = Double.POSITIVE_INFINITY;
	private double maxB = Double.POSITIVE_INFINITY;
	private double maxSum = Double.POSITIVE_INFINITY;
	private double minProgress = 1;
	private double maxProgress = 1;

	public LuxInputConditionBuilder minR(double minR) {
		this.minR = minR;
		return this;
	}

	public LuxInputConditionBuilder minG(double minG) {
		this.minG = minG;
		return this;
	}

	public LuxInputConditionBuilder minB(double minB) {
		this.minB = minB;
		return this;
	}

	public LuxInputConditionBuilder minSum(double minSum) {
		this.minSum = minSum;
		return this;
	}

	public LuxInputConditionBuilder maxR(double maxR) {
		this.maxR = maxR;
		return this;
	}

	public LuxInputConditionBuilder maxG(double maxG) {
		this.maxG = maxG;
		return this;
	}

	public LuxInputConditionBuilder maxB(double maxB) {
		this.maxB = maxB;
		return this;
	}

	public LuxInputConditionBuilder maxSum(double maxSum) {
		this.maxSum = maxSum;
		return this;
	}

	public LuxInputConditionBuilder minProgress(double minProgress) {
		this.minProgress = minProgress;
		return this;
	}

	public LuxInputConditionBuilder maxProgress(double maxProgress) {
		this.maxProgress = maxProgress;
		return this;
	}

	public LuxInputCondition build() {
		return new LuxInputCondition(
				this.minR, this.minG, this.minB, this.minSum,
				this.maxR, this.maxG, this.maxB, this.maxSum,
				this.minProgress, this.maxProgress);
	}

	public void validate(@Nullable ResourceLocation recipeId, @Nullable LuxStat absoluteMaxInput) {
		LuxInputCondition c = build();

		for (LuxInputCondition.Component component : LuxInputCondition.Component.values()) {
			validateComponent(recipeId, c, component, absoluteMaxInput);
		}

		if (c.minProgress() < 0 || Double.isNaN(c.minProgress())) exception(recipeId, "has invalid minProgress value");
		if (c.maxProgress() < 0 || Double.isNaN(c.maxProgress())) exception(recipeId, "has invalid maxProgress value");
		if (c.minProgress() > c.maxProgress()) exception(recipeId, "has minProgress value greater than maxProgress");

		if (c.minR() == 0 && c.minG() == 0 && c.minB() == 0 && c.minSum() == 0) {
			if (c.minProgress() != 1)
				exception(recipeId, "has redundant minProgress value");
			if (c.maxProgress() != 1)
				exception(recipeId, "has redundant maxProgress value");
		}
	}

	private void validateComponent(@Nullable ResourceLocation recipeId, LuxInputCondition c,
	                               LuxInputCondition.Component component, @Nullable LuxStat absoluteMaxInput) {
		double min = c.min(component);
		double max = c.max(component);

		if (min < 0 || Double.isNaN(min)) exception(recipeId, "has invalid min" + component + " value");
		if (max < 0 || Double.isNaN(max)) exception(recipeId, "has invalid max" + component + " value");
		if (min > max) exception(recipeId, "has min" + component + " value greater than max" + component);

		if (absoluteMaxInput != null) {
			double absMax = switch (component) {
				case R -> absoluteMaxInput.rMaxTransfer();
				case G -> absoluteMaxInput.gMaxTransfer();
				case B -> absoluteMaxInput.bMaxTransfer();
				case SUM -> absoluteMaxInput.rMaxTransfer() +
						absoluteMaxInput.gMaxTransfer() + absoluteMaxInput.bMaxTransfer();
			};

			if (min > absMax)
				exception(recipeId, "has min" + component + " value greater than absolute max value of " + absMax);
			if (max != Double.POSITIVE_INFINITY && max > absMax)
				exception(recipeId, "has max" + component + " value greater than absolute max value of " + absMax);
		}
	}

	private void exception(@Nullable ResourceLocation recipeId, @NotNull String reason) {
		throw new IllegalStateException((recipeId != null ? "Recipe " + recipeId : "Lux input condition") + " " + reason);
	}
}
