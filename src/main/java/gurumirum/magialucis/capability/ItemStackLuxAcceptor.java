package gurumirum.magialucis.capability;

import gurumirum.magialucis.api.capability.LuxAcceptor;
import gurumirum.magialucis.api.capability.LuxContainerStat;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.augment.TieredAugmentTypes;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class ItemStackLuxAcceptor implements LuxAcceptor, LuxContainerStat {
	public static final double OVERCHARGE_1_MULTIPLIER = 1.5;
	public static final double OVERCHARGE_2_MULTIPLIER = 3.0;
	public static final double OVERCHARGE_3_MULTIPLIER = 6.0;

	private final ItemStack stack;
	private final LuxContainerStat baseStat;

	public ItemStackLuxAcceptor(ItemStack stack, LuxContainerStat baseStat) {
		this.stack = stack;
		this.baseStat = baseStat;
	}

	@SuppressWarnings("lossy-conversions")
	@Override
	public long maxCharge() {
		long maxCharge = this.baseStat.maxCharge();
		switch (TieredAugmentTypes.OVERCHARGE.getTier(this.stack)) {
			case 0 -> maxCharge *= OVERCHARGE_1_MULTIPLIER;
			case 1 -> maxCharge *= OVERCHARGE_2_MULTIPLIER;
			case 2 -> maxCharge *= OVERCHARGE_3_MULTIPLIER;
		}
		return maxCharge;
	}

	@Override
	public double minLuxThreshold() {
		return this.baseStat.minLuxThreshold();
	}

	@Override
	public double rMaxTransfer() {
		return this.baseStat.rMaxTransfer();
	}

	@Override
	public double gMaxTransfer() {
		return this.baseStat.gMaxTransfer();
	}

	@Override
	public double bMaxTransfer() {
		return this.baseStat.bMaxTransfer();
	}

	@Override
	public void accept(double red, double green, double blue, boolean test, @NotNull Vector3d acceptedOut) {
		acceptedOut.zero();

		long maxCharge = maxCharge();
		if (maxCharge <= 0) return;

		long luxCharge = Math.max(0, this.stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L));
		if (luxCharge >= maxCharge) return;

		double rMaxTransfer = rMaxTransfer();
		double gMaxTransfer = gMaxTransfer();
		double bMaxTransfer = bMaxTransfer();

		acceptedOut.x = Math.min(red, rMaxTransfer);
		acceptedOut.y = Math.min(green, gMaxTransfer);
		acceptedOut.z = Math.min(blue, bMaxTransfer);
		LuxUtils.snapComponents(acceptedOut, minLuxThreshold());

		double combinedLux = acceptedOut.x + acceptedOut.y + acceptedOut.z;
		long amountToCharge = Math.min(maxCharge - luxCharge, (long)combinedLux);

		if (amountToCharge <= 0) {
			acceptedOut.zero();
			return;
		}

		double brightnessSum = rMaxTransfer + gMaxTransfer + bMaxTransfer;
		acceptedOut.x = amountToCharge * (rMaxTransfer / brightnessSum);
		acceptedOut.y = amountToCharge * (gMaxTransfer / brightnessSum);
		acceptedOut.z = amountToCharge * (bMaxTransfer / brightnessSum);

		if (!test) this.stack.set(ModDataComponents.LUX_CHARGE, luxCharge + amountToCharge);
	}
}
