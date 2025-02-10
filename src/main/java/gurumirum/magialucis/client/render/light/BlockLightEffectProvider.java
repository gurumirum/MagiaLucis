package gurumirum.magialucis.client.render.light;

import gurumirum.magialucis.client.render.RenderEffect;
import gurumirum.magialucis.contents.block.lux.LuxNodeSyncPropertyAccess;
import gurumirum.magialucis.impl.luxnet.InWorldLinkState;
import gurumirum.magialucis.impl.luxnet.LinkInfo;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class BlockLightEffectProvider<T extends BlockEntity & LuxNodeSyncPropertyAccess>
		extends RenderEffect.BlockEntityBound<T>
		implements LightEffectProvider {
	private final Vector3d luxCache = new Vector3d();

	private float sphereRadiusModifier = 1;
	private float rayRadiusModifier = 1;

	public BlockLightEffectProvider(T blockEntity) {
		super(blockEntity);
	}

	public BlockLightEffectProvider<T> sphereSize(float modifier) {
		this.sphereRadiusModifier = modifier;
		return this;
	}

	public BlockLightEffectProvider<T> raySize(float modifier) {
		this.rayRadiusModifier = modifier;
		return this;
	}

	protected @Nullable Vec3 origin() {
		return Vec3.atCenterOf(this.blockEntity.getBlockPos());
	}

	@Override
	public void getLightEffects(float partialTicks, @NotNull Collector collector) {
		Vector3d luxFlow = this.blockEntity.luxFlow(new Vector3d());

		float rm = sphereRadiusModifier(partialTicks);
		if (rm > 0) sphereEffects(partialTicks, collector, luxFlow, rm);

		rm = rayRadiusModifier(partialTicks);
		if (rm > 0) rayEffects(partialTicks, collector, luxFlow, rm);
	}

	protected void sphereEffects(float partialTicks, Collector collector, Vector3d luxFlow, float radiusModifier) {
		Vec3 origin = origin();
		if (origin == null) return;

		double sum = LuxUtils.sum(luxFlow);
		if (sum <= 1) return;

		collector.addCircularEffect(LightEffect.sphereRadius(sum) * radiusModifier, origin, LightEffect.getLightColor(luxFlow));
	}

	protected void rayEffects(float partialTicks, Collector collector, Vector3d luxFlow, float radiusModifier) {
		Vec3 origin = origin();
		if (origin == null) return;

		int totalLinkWeight = Math.max(1, this.blockEntity.totalLinkWeight());
		int color = 0;
		boolean colorInitialized = false;

		for (var e : this.blockEntity.outboundLinks().int2ObjectEntrySet()) {
			LinkInfo info = e.getValue();
			if (info.inWorld() == null) continue;

			double m = (double)info.weight() / totalLinkWeight;
			double sum = LuxUtils.sum(this.luxCache.set(luxFlow).mul(m));
			if (sum <= 1) return;

			float radius = LightEffect.rayRadius(sum) * radiusModifier;

			if (!colorInitialized) {
				colorInitialized = true;
				color = LightEffect.getLightColor(luxFlow);
			}

			collector.addCylindricalEffect(radius, origin, Vec3.atCenterOf(info.inWorld().linkPos()), color, false);
		}

		for (InWorldLinkState linkState : this.blockEntity.linkStates()) {
			if (linkState != null && !linkState.linked()) {
				double m = (double)linkState.weight() / totalLinkWeight;
				double sum = LuxUtils.sum(this.luxCache.set(luxFlow).mul(m));
				if (sum <= 1) return;

				float radius = LightEffect.rayRadius(sum) * radiusModifier;

				if (!colorInitialized) {
					colorInitialized = true;
					color = LightEffect.getLightColor(luxFlow);
				}

				collector.addCylindricalEffect(radius, origin, linkState.linkLocation(), color, true);
			}
		}
	}

	protected float sphereRadiusModifier(float partialTicks) {
		return this.sphereRadiusModifier;
	}

	protected float rayRadiusModifier(float partialTicks) {
		return this.rayRadiusModifier;
	}
}
