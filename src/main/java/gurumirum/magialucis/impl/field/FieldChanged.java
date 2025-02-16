package gurumirum.magialucis.impl.field;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface FieldChanged {
	void onFieldChanged(@NotNull BlockPos pos, @NotNull ServerFieldElement element, boolean removed);
}
