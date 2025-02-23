package gurumirum.magialucis.impl.luxnet.behavior;

import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.api.capability.LuxStat;
import gurumirum.magialucis.api.luxnet.LuxNet;
import gurumirum.magialucis.api.luxnet.LuxNode;
import gurumirum.magialucis.api.luxnet.behavior.LuxConsumerNodeBehavior;
import gurumirum.magialucis.api.luxnet.behavior.LuxNodeType;
import gurumirum.magialucis.utils.LuxSampler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import java.util.Objects;

public class SimpleConsumerBehavior implements LuxConsumerNodeBehavior {
	public static LuxNodeType.Simple<SimpleConsumerBehavior> createType(@NotNull String id, @NotNull LuxStat stat) {
		return createType(MagiaLucisApi.id(id), stat);
	}

	public static LuxNodeType.Simple<SimpleConsumerBehavior> createType(@NotNull ResourceLocation id, @NotNull LuxStat stat) {
		Objects.requireNonNull(stat);
		return new LuxNodeType.Simple<SimpleConsumerBehavior>(id,
				SimpleConsumerBehavior.class,
				t -> new SimpleConsumerBehavior(t, stat));
	}

	private final LuxNodeType<?> type;
	private final LuxStat stat;

	protected final LuxSampler luxInput = new LuxSampler(5);
	private final Vector3d cache = new Vector3d();

	protected SimpleConsumerBehavior(@NotNull LuxNodeType<?> type, @NotNull LuxStat stat) {
		this.type = type;
		this.stat = stat;
	}

	@Override
	public @NotNull LuxNodeType<?> type() {
		return this.type;
	}

	@Override
	public @NotNull LuxStat stat() {
		return this.stat;
	}

	@Override
	public void consumeLux(@NotNull ServerLevel level, @NotNull LuxNet luxNet,
	                       @NotNull LuxNode node, @NotNull Vector3d receivedLux) {
		this.luxInput.nextSampler().set(receivedLux);
		receivedLux.zero();
	}

	public @NotNull Vector3d average() {
		return this.luxInput.average(this.cache);
	}

	public @NotNull Vector3d min() {
		return this.luxInput.min(this.cache);
	}

	public @NotNull Vector3d max() {
		return this.luxInput.max(this.cache);
	}

	public @NotNull Vector3d average(@NotNull Vector3d dest) {
		return this.luxInput.average(dest);
	}

	public @NotNull Vector3d min(@NotNull Vector3d dest) {
		return this.luxInput.min(dest);
	}

	public @NotNull Vector3d max(@NotNull Vector3d dest) {
		return this.luxInput.max(dest);
	}

	public void reset() {
		this.luxInput.reset();
	}
}
