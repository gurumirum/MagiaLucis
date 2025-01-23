package gurumirum.magialucis.contents.block.lux.charger;

import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.ChargerTier;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxNode;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import gurumirum.magialucis.impl.luxnet.behavior.LuxConsumerNodeBehavior;
import gurumirum.magialucis.impl.luxnet.behavior.LuxNodeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class ChargerBehavior implements LuxConsumerNodeBehavior {
	public static final double REMOTE_CHARGER_STORAGE_MULTIPLIER = 10;

	public final Vector3d charge = new Vector3d();
	public final Vector3d maxInput = new Vector3d();

	private final Vector3d maxChargeStorage = new Vector3d();
	private final ChargerTier chargerTier;
	private final boolean remote;

	public ChargerBehavior(ChargerTier chargerTier, boolean remote) {
		this.chargerTier = chargerTier;
		this.remote = remote;
		stat().maxTransfer(this.maxChargeStorage);
		if (remote) this.maxChargeStorage.mul(REMOTE_CHARGER_STORAGE_MULTIPLIER);
		LuxUtils.snapComponents(this.maxChargeStorage, 0);
	}

	@Override
	public @NotNull LuxNodeType<?> type() {
		return this.chargerTier.chargerBehaviorType(this.remote);
	}

	@Override
	public @NotNull LuxStat stat() {
		return this.chargerTier.stat();
	}

	@Override
	public void consumeLux(Level level, LuxNet luxNet, LuxNode node, Vector3d receivedLux) {
		if (node.isLoaded()) {
			this.maxInput.max(receivedLux);
			LuxUtils.transfer(receivedLux, this.charge, this.maxChargeStorage);
		}
	}

	public void reset() {
		this.charge.zero();
		this.maxInput.zero();
	}
}
