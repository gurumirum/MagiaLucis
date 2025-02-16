package gurumirum.magialucis.contents.block.lux.charger;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.api.capability.LuxAcceptor;
import gurumirum.magialucis.api.capability.MagiaLucisCaps;
import gurumirum.magialucis.contents.Accessories;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.joml.Vector3d;

public final class ChargeLogic {
	private ChargeLogic() {}

	public static boolean chargeItem(Vector3d charge, ItemStack stack, Vector3d maxChargePerItem) {
		boolean success = false;

		if (stack.is(Accessories.WAND_BELT.asItem())) {
			if (stack.getCapability(Capabilities.ItemHandler.ITEM) instanceof IItemHandlerModifiable itemHandler) {
				for (int i = 0; i < itemHandler.getSlots(); i++) {
					ItemStack s = itemHandler.getStackInSlot(i);
					if (chargeItem(charge, s, maxChargePerItem)) {
						itemHandler.setStackInSlot(i, s);
						success = true;
						if (charge.x <= 0 && charge.y <= 0 && charge.z <= 0) break;
					}
				}
			}

			return success;
		}

		LuxAcceptor luxAcceptor = stack.getCapability(MagiaLucisCaps.LUX_ACCEPTOR);

		if (luxAcceptor != null) {
			Vector3d accepted = new Vector3d();
			luxAcceptor.accept(
					Math.min(maxChargePerItem.x, charge.x),
					Math.min(maxChargePerItem.y, charge.y),
					Math.min(maxChargePerItem.z, charge.z),
					false, accepted);
			if (LuxUtils.isValid(accepted)) {
				if (accepted.x > 0) {
					charge.x = Math.max(0, charge.x - accepted.x);
					success = true;
				}
				if (accepted.y > 0) {
					charge.y = Math.max(0, charge.y - accepted.y);
					success = true;
				}
				if (accepted.z > 0) {
					charge.z = Math.max(0, charge.z - accepted.z);
					success = true;
				}
			} else {
				MagiaLucisMod.LOGGER.warn("Lux acceptor capability of item stack {} returned an invalid result!", stack);
			}
		}
		return success;
	}
}
