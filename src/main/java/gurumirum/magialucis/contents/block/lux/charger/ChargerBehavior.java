package gurumirum.magialucis.contents.block.lux.charger;

import gurumirum.magialucis.api.capability.LuxStat;
import gurumirum.magialucis.api.luxnet.LuxNet;
import gurumirum.magialucis.api.luxnet.LuxNode;
import gurumirum.magialucis.api.luxnet.behavior.LuxConsumerNodeBehavior;
import gurumirum.magialucis.api.luxnet.behavior.LuxNodeType;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import gurumirum.magialucis.utils.LuxSampler;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class ChargerBehavior implements LuxConsumerNodeBehavior {
	public static final double REMOTE_CHARGER_STORAGE_MULTIPLIER = 10;

	public final Vector3d charge = new Vector3d();
	public final LuxSampler luxInput = new LuxSampler(5);

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
	public void consumeLux(@NotNull ServerLevel level, @NotNull LuxNet luxNet, @NotNull LuxNode node, @NotNull Vector3d receivedLux) {
		this.luxInput.nextSampler().set(receivedLux);
		this.charge.add(receivedLux).min(this.maxChargeStorage);
		receivedLux.zero();
	}

	public void reset() {
		this.charge.zero();
		this.luxInput.reset();
	}
}
