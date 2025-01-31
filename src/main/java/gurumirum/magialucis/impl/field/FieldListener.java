package gurumirum.magialucis.impl.field;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.function.DoubleConsumer;

public final class FieldListener {
	private static final FieldListener invalid = new FieldListener();

	public static FieldListener invalid() {
		return invalid;
	}

	private @Nullable FieldInstance fieldInstance;

	private boolean fieldChanged;
	private @Nullable Set<BlockPos> powerChanged;

	private FieldListener() {}

	FieldListener(@NotNull FieldInstance fieldInstance) {
		this.fieldInstance = Objects.requireNonNull(fieldInstance);
	}

	public void invalidate() {
		if (this.fieldInstance != null) {
			if (this.fieldChanged) this.fieldInstance.fieldChangedInvalidated();
			if (this.powerChanged != null) this.fieldInstance.powerChangedInvalidated(this.powerChanged);
			this.fieldInstance = null;
		}
	}

	public boolean isValid() {
		return this.fieldInstance != null;
	}

	public FieldListener fieldChanged(@NotNull FieldChanged changed) {
		checkFieldInstance().listenFieldChanged(this, changed);
		this.fieldChanged = true;
		return this;
	}

	public FieldListener powerChanged(@NotNull BlockPos pos, @NotNull DoubleConsumer changed) {
		pos = pos.immutable();
		checkFieldInstance().listenPowerChanged(this, pos, changed);
		if (this.powerChanged == null) this.powerChanged = new ObjectArraySet<>();
		this.powerChanged.add(pos);
		return this;
	}

	private @NotNull FieldInstance checkFieldInstance() {
		if (this.fieldInstance == null) {
			throw new IllegalStateException("Invalid listener");
		}
		return this.fieldInstance;
	}
}
