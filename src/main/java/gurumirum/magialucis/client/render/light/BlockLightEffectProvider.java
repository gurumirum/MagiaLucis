package gurumirum.magialucis.client.render.light;

import gurumirum.magialucis.client.render.RenderEffect;
import gurumirum.magialucis.contents.block.lux.LuxNodeSyncPropertyAccess;
import gurumirum.magialucis.impl.luxnet.InWorldLinkInfo;
import gurumirum.magialucis.impl.luxnet.InWorldLinkState;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class BlockLightEffectProvider<T extends BlockEntity & LuxNodeSyncPropertyAccess>
		extends RenderEffect.BlockEntityBound<T>
		implements LightEffectProvider {
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

		var outboundLinks = this.blockEntity.outboundLinks();
		if (outboundLinks.size() > 1) {
			luxFlow.div(outboundLinks.size());
		}

		int color = 0;
		boolean colorInitialized = false;

		double sum = LuxUtils.sum(luxFlow);
		if (sum <= 1) return;

		float radius = LightEffect.rayRadius(sum) * radiusModifier;

		for (var e : outboundLinks.int2ObjectEntrySet()) {
			InWorldLinkInfo info = e.getValue();
			if (info == null) continue;

			if (!colorInitialized) {
				colorInitialized = true;
				color = LightEffect.getLightColor(luxFlow);
			}

			collector.addCylindricalEffect(radius, origin, Vec3.atCenterOf(info.linkPos()), color, false);
		}

		for (InWorldLinkState linkState : this.blockEntity.linkStates()) {
			if (linkState != null && !linkState.linked()) {
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
