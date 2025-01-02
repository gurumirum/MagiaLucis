package gurumirum.gemthing.capability;

import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.impl.RGB332;
import net.minecraft.world.item.ItemStack;

public class LuxAcceptorImpl implements LuxAcceptor {
	private final ItemStack stack;
	private final LuxContainerStat stat;

	public LuxAcceptorImpl(ItemStack stack, LuxContainerStat stat) {
		this.stack = stack;
		this.stat = stat;
	}

	@Override
	public long accept(double red, double green, double blue, boolean test) {
		red = Math.max(0, red);
		green = Math.max(0, green);
		blue = Math.max(0, blue);

		long maxCharge = stat.maxCharge();
		long maxLuxThreshold = stat.maxLuxThreshold();
		if (maxCharge <= 0 || maxLuxThreshold <= 0) return 0;

		long luxCharge = Math.max(0, stack.getOrDefault(Contents.LUX_CHARGE.get(), 0L));
		if (luxCharge >= maxCharge) return 0;

		byte containerColor = stat.color();
		long minLuxThreshold = stat.minLuxThreshold();

		red *= RGB332.rBrightness(containerColor);
		green *= RGB332.gBrightness(containerColor);
		blue *= RGB332.bBrightness(containerColor);

		long combinedLux = (long)(red + green + blue);
		if (minLuxThreshold > combinedLux) return 0;

		long amountToCharge = Math.min(maxCharge - luxCharge, combinedLux);

		if (!test) stack.set(Contents.LUX_CHARGE.get(), luxCharge + amountToCharge);

		return amountToCharge;
	}
}
