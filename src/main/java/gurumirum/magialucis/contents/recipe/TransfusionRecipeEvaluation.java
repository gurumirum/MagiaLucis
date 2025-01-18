package gurumirum.magialucis.contents.recipe;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector3d;

import java.util.function.Supplier;

public final class TransfusionRecipeEvaluation {
	private static final TransfusionRecipeEvaluation FAIL = new TransfusionRecipeEvaluation();

	public static TransfusionRecipeEvaluation fail() {
		return FAIL;
	}

	private final @Nullable Supplier<ItemStack> result;
	private final int processTicks;
	private final Int2IntMap consumption;

	private final double minLuxInputR;
	private final double minLuxInputG;
	private final double minLuxInputB;
	private final double minLuxInputSum;
	private final double maxLuxInputR;
	private final double maxLuxInputG;
	private final double maxLuxInputB;
	private final double maxLuxInputSum;

	private TransfusionRecipeEvaluation() {
		this.result = null;
		this.processTicks = 0;
		this.consumption = Int2IntMaps.EMPTY_MAP;

		this.minLuxInputR = 0;
		this.minLuxInputG = 0;
		this.minLuxInputB = 0;
		this.minLuxInputSum = 0;
		this.maxLuxInputR = Double.POSITIVE_INFINITY;
		this.maxLuxInputG = Double.POSITIVE_INFINITY;
		this.maxLuxInputB = Double.POSITIVE_INFINITY;
		this.maxLuxInputSum = Double.POSITIVE_INFINITY;
	}

	public TransfusionRecipeEvaluation(@NotNull Supplier<ItemStack> result, int processTicks, @NotNull ConsumptionRecord consumption,
	                                   double minLuxInputR, double minLuxInputG, double minLuxInputB, double minLuxInputSum,
	                                   double maxLuxInputR, double maxLuxInputG, double maxLuxInputB, double maxLuxInputSum) {
		this.result = result;
		this.processTicks = processTicks;
		this.consumption = consumption.map();

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
		return this.result != null ? this.result.get() : ItemStack.EMPTY;
	}

	public int processTicks() {
		return processTicks;
	}

	public @NotNull @UnmodifiableView Int2IntMap consumption() {
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
