package gurumirum.magialucis.api.field;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;

public interface FieldInstance {
	@NotNull Field field();
	@NotNull @UnmodifiableView Map<BlockPos, FieldElement> elements();

	@Nullable FieldElement elementAt(@NotNull BlockPos pos);
	boolean isEmpty();

	@NotNull FieldElement add(@NotNull BlockPos pos);
	void remove(@NotNull BlockPos pos);

	double value(int x, int y, int z);
	double influenceSum(int x, int y, int z);
	boolean hasInfluence(int x, int y, int z);

	default double value(@NotNull BlockPos pos) {
		return value(pos.getX(), pos.getY(), pos.getZ());
	}
	default double value(double x, double y, double z) {
		return value(Mth.floor(x), Mth.floor(y), Mth.floor(z));
	}

	default double influenceSum(@NotNull BlockPos pos) {
		return influenceSum(pos.getX(), pos.getY(), pos.getZ());
	}
	default double influenceSum(double x, double y, double z) {
		return influenceSum(Mth.floor(x), Mth.floor(y), Mth.floor(z));
	}

	default boolean hasInfluence(@NotNull BlockPos pos) {
		return hasInfluence(pos.getX(), pos.getY(), pos.getZ());
	}
	default boolean hasInfluence(double x, double y, double z) {
		return hasInfluence(Mth.floor(x), Mth.floor(y), Mth.floor(z));
	}
}

