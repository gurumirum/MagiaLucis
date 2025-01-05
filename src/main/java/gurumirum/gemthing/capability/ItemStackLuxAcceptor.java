package gurumirum.gemthing.capability;

import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.impl.RGB332;
import gurumirum.gemthing.utils.LuxUtils;
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
		double maxLuxThreshold = this.stat.maxLuxThreshold();
		if (maxCharge <= 0 || maxLuxThreshold <= 0) return;

		long luxCharge = Math.max(0, this.stack.getOrDefault(Contents.LUX_CHARGE, 0L));
		if (luxCharge >= maxCharge) return;

		byte containerColor = this.stat.color();
		double rBrightness = RGB332.rBrightness(containerColor);
		double gBrightness = RGB332.gBrightness(containerColor);
		double bBrightness = RGB332.bBrightness(containerColor);

		acceptedOut.x = Math.min(red, rBrightness * maxLuxThreshold);
		acceptedOut.y = Math.min(green, gBrightness * maxLuxThreshold);
		acceptedOut.z = Math.min(blue, bBrightness * maxLuxThreshold);
		LuxUtils.snapComponents(acceptedOut, this.stat.minLuxThreshold());

		double combinedLux = acceptedOut.x + acceptedOut.y + acceptedOut.z;
		long amountToCharge = Math.min(maxCharge - luxCharge, (long)combinedLux);

		if (amountToCharge <= 0) {
			acceptedOut.zero();
			return;
		}

		double brightnessSum = rBrightness + gBrightness + bBrightness;
		acceptedOut.x = amountToCharge * (rBrightness / brightnessSum);
		acceptedOut.y = amountToCharge * (gBrightness / brightnessSum);
		acceptedOut.z = amountToCharge * (bBrightness / brightnessSum);

		if (!test) this.stack.set(Contents.LUX_CHARGE, luxCharge + amountToCharge);
	}

	@Override
	public long acceptDirect(long amount, boolean bypassThreshold, boolean test) {
		if (amount <= 0) return 0;

		long maxCharge = this.stat.maxCharge();
		if (maxCharge <= 0) return 0;

		long luxCharge = Math.max(0, this.stack.getOrDefault(Contents.LUX_CHARGE, 0L));
		if (luxCharge >= maxCharge) return 0;

		if (!bypassThreshold) {
			byte containerColor = this.stat.color();
			long maxLuxThreshold = Math.max((long)(this.stat.maxLuxThreshold() * (RGB332.rBrightness(containerColor) +
					RGB332.gBrightness(containerColor) + RGB332.bBrightness(containerColor))), 0);
			long minLuxThreshold = Math.max((long)(this.stat.minLuxThreshold() * 3), 0);

			amount = Math.min(amount, maxLuxThreshold);

			if (minLuxThreshold > amount) return 0;
		}

		long amountToCharge = Math.min(maxCharge - luxCharge, amount);
		if (!test) stack.set(Contents.LUX_CHARGE, luxCharge + amountToCharge);

		return amountToCharge;
	}
}
