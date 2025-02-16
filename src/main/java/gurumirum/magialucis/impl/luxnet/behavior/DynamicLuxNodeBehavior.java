package gurumirum.magialucis.impl.luxnet.behavior;

import gurumirum.magialucis.api.capability.LuxStat;
import gurumirum.magialucis.api.luxnet.behavior.LuxNodeBehavior;
import gurumirum.magialucis.api.luxnet.behavior.LuxNodeType;
import gurumirum.magialucis.contents.LuxNodeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DynamicLuxNodeBehavior implements LuxNodeBehavior, LuxStat {
	private double minLuxThreshold;
	private double rMaxTransfer;
	private double gMaxTransfer;
	private double bMaxTransfer;

	public DynamicLuxNodeBehavior() {}

	public DynamicLuxNodeBehavior(@Nullable LuxStat copyFrom) {
		if (copyFrom != null) setStats(copyFrom);
	}

	@Override
	public @NotNull LuxNodeType<?> type() {
		return LuxNodeTypes.DYNAMIC;
	}

	@Override
	public @NotNull LuxStat stat() {
		return this;
	}

	@Override
	public double minLuxThreshold() {
		return this.minLuxThreshold;
	}

	@Override
	public double rMaxTransfer() {
		return this.rMaxTransfer;
	}

	@Override
	public double gMaxTransfer() {
		return this.gMaxTransfer;
	}

	@Override
	public double bMaxTransfer() {
		return this.bMaxTransfer;
	}

	public void setMinLuxThreshold(double minLuxThreshold) {
		this.minLuxThreshold = Double.isNaN(minLuxThreshold) ? 0 : Math.max(minLuxThreshold, 0);
	}

	public void setRMaxTransfer(double rMaxTransfer) {
		this.rMaxTransfer = Double.isNaN(rMaxTransfer) ? 0 : Math.max(rMaxTransfer, 0);
	}

	public void setGMaxTransfer(double gMaxTransfer) {
		this.gMaxTransfer = Double.isNaN(gMaxTransfer) ? 0 : Math.max(gMaxTransfer, 0);
	}

	public void setBMaxTransfer(double bMaxTransfer) {
		this.bMaxTransfer = Double.isNaN(bMaxTransfer) ? 0 : Math.max(bMaxTransfer, 0);
	}

	public void setStats(@NotNull LuxStat stat) {
		setMinLuxThreshold(stat.minLuxThreshold());
		setRMaxTransfer(stat.rMaxTransfer());
		setGMaxTransfer(stat.gMaxTransfer());
		setBMaxTransfer(stat.bMaxTransfer());
	}

	public void save(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		if (this.minLuxThreshold > 0) tag.putDouble("minLuxThreshold", this.minLuxThreshold);
		if (this.rMaxTransfer > 0) tag.putDouble("rMaxTransfer", this.rMaxTransfer);
		if (this.gMaxTransfer > 0) tag.putDouble("gMaxTransfer", this.gMaxTransfer);
		if (this.bMaxTransfer > 0) tag.putDouble("bMaxTransfer", this.bMaxTransfer);
	}

	public DynamicLuxNodeBehavior(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		setMinLuxThreshold(tag.getDouble("minLuxThreshold"));
		setRMaxTransfer(tag.getDouble("rMaxTransfer"));
		setGMaxTransfer(tag.getDouble("gMaxTransfer"));
		setBMaxTransfer(tag.getDouble("bMaxTransfer"));
	}
}
