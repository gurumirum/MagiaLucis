package gurumirum.magialucis.contents.block.lux.charger;

import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.LuxNodeTypes;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxNode;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import gurumirum.magialucis.impl.luxnet.behavior.LuxConsumerNodeBehavior;
import gurumirum.magialucis.impl.luxnet.behavior.LuxNodeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public abstract class RemoteChargerBehavior implements LuxConsumerNodeBehavior {
	public final Vector3d charge = new Vector3d();

	private final Vector3d maxChargeStorage = new Vector3d();

	public RemoteChargerBehavior() {
		stat().maxTransfer(this.maxChargeStorage).mul(100);
		LuxUtils.snapComponents(this.maxChargeStorage, 0);
	}

	@Override
	public void consumeLux(Level level, LuxNet luxNet, LuxNode node, Vector3d receivedLux) {
		if (node.isLoaded()) LuxUtils.transfer(receivedLux, this.charge, this.maxChargeStorage);
	}

	public static class Basic extends RemoteChargerBehavior {
		@Override
		public @NotNull LuxNodeType<?> type() {
			return LuxNodeTypes.BASIC_CHARGER;
		}

		@Override
		public @NotNull LuxStat stat() {
			return RemoteChargerBlock.BASIC_STAT;
		}
	}

	public static class Advanced extends RemoteChargerBehavior {
		@Override
		public @NotNull LuxNodeType<?> type() {
			return LuxNodeTypes.ADVANCED_CHARGER;
		}

		@Override
		public @NotNull LuxStat stat() {
			return RemoteChargerBlock.ADVANCED_STAT;
		}
	}
}
