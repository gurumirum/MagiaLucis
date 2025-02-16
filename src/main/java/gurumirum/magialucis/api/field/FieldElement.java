package gurumirum.magialucis.api.field;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

public interface FieldElement {
	@NotNull FieldInstance fieldInstance();
	@NotNull BlockPos pos();

	double range();
	double rangeSq();

	double diminishPower();
	double power();

	void setRange(double range);
	void setDiminishPower(double diminishPower);
}

