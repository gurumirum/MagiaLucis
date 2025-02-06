package gurumirum.magialucis.contents.recipe;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.function.Supplier;

public final class LuxRecipeEvaluation {
	private static final LuxRecipeEvaluation FAIL = new LuxRecipeEvaluation();

	public static LuxRecipeEvaluation fail() {
		return FAIL;
	}

	private final @Nullable Supplier<ItemStack> result;
	private final int processTicks;
	private final ConsumptionRecord consumption;

	private final double minLuxInputR;
	private final double minLuxInputG;
	private final double minLuxInputB;
	private final double minLuxInputSum;
	private final double maxLuxInputR;
	private final double maxLuxInputG;
	private final double maxLuxInputB;
	private final double maxLuxInputSum;

	private @Nullable ItemStack resultCache;

	private LuxRecipeEvaluation() {
		this.result = null;
		this.processTicks = 0;
		this.consumption = ConsumptionRecord.none();

		this.minLuxInputR = 0;
		this.minLuxInputG = 0;
		this.minLuxInputB = 0;
		this.minLuxInputSum = 0;
		this.maxLuxInputR = Double.POSITIVE_INFINITY;
		this.maxLuxInputG = Double.POSITIVE_INFINITY;
		this.maxLuxInputB = Double.POSITIVE_INFINITY;
		this.maxLuxInputSum = Double.POSITIVE_INFINITY;
	}

	public LuxRecipeEvaluation(@NotNull Supplier<ItemStack> result, int processTicks, @NotNull ConsumptionRecord consumption,
	                           double minLuxInputR, double minLuxInputG, double minLuxInputB, double minLuxInputSum,
	                           double maxLuxInputR, double maxLuxInputG, double maxLuxInputB, double maxLuxInputSum) {
		this.result = result;
		this.processTicks = processTicks;
		this.consumption = consumption.immutable();

		this.minLuxInputR = minLuxInputR;
		this.minLuxInputG = minLuxInputG;
		this.minLuxInputB = minLuxInputB;
		this.minLuxInputSum = minLuxInputSum;
		this.maxLuxInputR = maxLuxInputR;
		this.maxLuxInputG = maxLuxInputG;
		this.maxLuxInputB = maxLuxInputB;
		this.maxLuxInputSum = maxLuxInputSum;
	}

	public boolean isSuccess() {
		return this.result != null;
	}

	public @NotNull ItemStack result() {
		if (this.resultCache == null) {
			this.resultCache = this.result != null ? this.result.get() : ItemStack.EMPTY;
		}
		return this.resultCache;
	}

	public int processTicks() {
		return processTicks;
	}

	public @NotNull ConsumptionRecord consumption() {
		return this.consumption;
	}

	public boolean testLuxInput(Vector3d luxInput) {
		return testLuxInput(luxInput.x, luxInput.y, luxInput.z);
	}

	public boolean testLuxInput(double luxInputR, double luxInputG, double luxInputB) {
		if (luxInputR < this.minLuxInputR || luxInputR > this.maxLuxInputR) return false;
		if (luxInputG < this.minLuxInputG || luxInputG > this.maxLuxInputG) return false;
		if (luxInputB < this.minLuxInputB || luxInputB > this.maxLuxInputB) return false;

		double sum = luxInputR + luxInputG + luxInputB;
		return sum >= this.minLuxInputSum && sum <= this.maxLuxInputSum;
	}
}
