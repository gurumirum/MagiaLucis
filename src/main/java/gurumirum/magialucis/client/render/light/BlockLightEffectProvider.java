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
	private final float radiusModifier;

	public BlockLightEffectProvider(T blockEntity) {
		this(blockEntity, 1);
	}

	public BlockLightEffectProvider(T blockEntity, float radiusModifier) {
		super(blockEntity);
		this.radiusModifier = radiusModifier;
	}

	protected @Nullable Vec3 origin() {
		return Vec3.atCenterOf(this.blockEntity.getBlockPos());
	}

	@Override
	public void getLightEffects(float partialTicks, @NotNull Collector collector) {
		Vector3d luxFlow = this.blockEntity.luxFlow(new Vector3d());
		double sum = LuxUtils.sum(luxFlow);
		if (sum <= 1) return;

		Vec3 origin = origin();
		if (origin == null) return;

		int color = LightEffect.getLightColor(luxFlow);

		float radius = (float)(1 / 4.0 * (Math.log10(sum)));

		if (this.radiusModifier > 0) {
			collector.addCircularEffect(radius * this.radiusModifier, origin, color);
		}

		var outboundLinks = this.blockEntity.outboundLinks();
		if (outboundLinks.size() > 1) {
			luxFlow.div(outboundLinks.size());
			sum = LuxUtils.sum(luxFlow);
			if (sum <= 1) return;
		}
		radius = (float)(1 / 10.0 * Math.log10(Math.max(15, sum)));

		for (var e : outboundLinks.int2ObjectEntrySet()) {
			InWorldLinkInfo info = e.getValue();
			if (info == null) continue;
			collector.addCylindricalEffect(radius, origin, Vec3.atCenterOf(info.linkPos()), color, false);
		}

		for (InWorldLinkState linkState : this.blockEntity.linkStates()) {
			if (linkState != null && !linkState.linked()) {
				collector.addCylindricalEffect(radius, origin, linkState.linkLocation(), color, true);
			}
		}
	}
}
