package gurumirum.magialucis.contents.recipe;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public final class LuxRecipeEvaluation {
	private static final LuxRecipeEvaluation FAIL = new LuxRecipeEvaluation();

	public static LuxRecipeEvaluation fail() {
		return FAIL;
	}

	private final @Nullable Supplier<ItemStack> result;
	private final int processTicks;
	private final ConsumptionRecord consumption;
	private final LuxInputCondition luxInputCondition;

	private @Nullable ItemStack resultCache;

	private LuxRecipeEvaluation() {
		this.result = null;
		this.processTicks = 0;
		this.consumption = ConsumptionRecord.none();
		this.luxInputCondition = LuxInputCondition.none();
	}

	public LuxRecipeEvaluation(@NotNull Supplier<ItemStack> result, int processTicks,
	                           @NotNull ConsumptionRecord consumption, @NotNull LuxInputCondition luxInputCondition) {
		this.result = Objects.requireNonNull(result);
		this.processTicks = processTicks;
		this.consumption = consumption.immutable();
		this.luxInputCondition = Objects.requireNonNull(luxInputCondition);
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

	public @NotNull LuxInputCondition luxInputCondition() {
		return luxInputCondition;
	}
}
