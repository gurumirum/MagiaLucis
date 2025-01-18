package gurumirum.magialucis.capability;

import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class ItemStackLuxAcceptor implements LuxAcceptor {
	private final ItemStack stack;
	private final LuxContainerStat stat;

	public ItemStackLuxAcceptor(ItemStack stack, LuxContainerStat stat) {
		this.stack = stack;
		this.stat = stat;
	}

	@Override
	public void accept(double red, double green, double blue, boolean test, @NotNull Vector3d acceptedOut) {
		acceptedOut.zero();

		long maxCharge = this.stat.maxCharge();
		if (maxCharge <= 0) return;

		long luxCharge = Math.max(0, this.stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L));
		if (luxCharge >= maxCharge) return;

		double rMaxTransfer = this.stat.rMaxTransfer();
		double gMaxTransfer = this.stat.gMaxTransfer();
		double bMaxTransfer = this.stat.bMaxTransfer();

		acceptedOut.x = Math.min(red, rMaxTransfer);
		acceptedOut.y = Math.min(green, gMaxTransfer);
		acceptedOut.z = Math.min(blue, bMaxTransfer);
		LuxUtils.snapComponents(acceptedOut, this.stat.minLuxThreshold());

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

	@Override
	public long acceptDirect(long amount, boolean bypassThreshold, boolean test) {
		if (amount <= 0) return 0;

		long maxCharge = this.stat.maxCharge();
		if (maxCharge <= 0) return 0;

		long luxCharge = Math.max(0, this.stack.getOrDefault(ModDataComponents.LUX_CHARGE, 0L));
		if (luxCharge >= maxCharge) return 0;

		if (!bypassThreshold) {
			long maxLuxThreshold = Math.max((long)(this.stat.rMaxTransfer() + this.stat.gMaxTransfer() + this.stat.bMaxTransfer()), 0);
			long minLuxThreshold = Math.max((long)(this.stat.minLuxThreshold() * 3), 0);

			amount = Math.min(amount, maxLuxThreshold);

			if (minLuxThreshold > amount) return 0;
		}

		long amountToCharge = Math.min(maxCharge - luxCharge, amount);
		if (!test) stack.set(ModDataComponents.LUX_CHARGE, luxCharge + amountToCharge);

		return amountToCharge;
	}
}
