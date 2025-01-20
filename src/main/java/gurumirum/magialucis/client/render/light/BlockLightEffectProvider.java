package gurumirum.magialucis.client.render.light;

import gurumirum.magialucis.contents.block.lux.LuxNodeSyncPropertyAccess;
import gurumirum.magialucis.impl.luxnet.InWorldLinkInfo;
import gurumirum.magialucis.impl.luxnet.InWorldLinkState;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class BlockLightEffectProvider<T extends BlockEntity & LuxNodeSyncPropertyAccess> implements LightEffectProvider {
	private final T blockEntity;
	private final float radiusModifier;

	private boolean unloaded;

	public BlockLightEffectProvider(T blockEntity) {
		this(blockEntity, 1);
	}

	public BlockLightEffectProvider(T blockEntity, float radiusModifier) {
		this.blockEntity = blockEntity;
		this.radiusModifier = radiusModifier;
	}

	@Override
	public void getLightEffects(float partialTicks, @NotNull Collector collector) {
		Vector3d luxFlow = this.blockEntity.luxFlow(new Vector3d());

		if (luxFlow.x > 0 || luxFlow.y > 0 || luxFlow.z > 0) {
			Vec3 center = Vec3.atCenterOf(this.blockEntity.getBlockPos());
			int color = LightEffect.getLightColor(luxFlow);

			float radius = (float)(1 / 4.0 * (Math.log10(LuxUtils.sum(luxFlow))));

			if (this.radiusModifier > 0) {
				collector.addCircularEffect(radius * this.radiusModifier, center, color);
			}

			var outboundLinks = this.blockEntity.outboundLinks();
			if (outboundLinks.size() > 1) luxFlow.div(outboundLinks.size());
			radius = (float)(1 / 10.0 * (Math.log10(LuxUtils.sum(luxFlow))));

			for (var e : outboundLinks.int2ObjectEntrySet()) {
				InWorldLinkInfo info = e.getValue();
				if (info == null) continue;
				collector.addCylindricalEffect(radius, center, Vec3.atCenterOf(info.linkPos()), color, false);
			}

			for (InWorldLinkState linkState : this.blockEntity.linkStates()) {
				if (linkState != null && !linkState.linked()) {
					collector.addCylindricalEffect(radius, center, linkState.linkLocation(), color, true);
				}
			}
		}
	}

	@Override
	public boolean isLightEffectProviderValid() {
		return !this.unloaded && !this.blockEntity.isRemoved();
	}

	@Override
	public void onLevelUnload(LevelAccessor level) {
		if (level == this.blockEntity.getLevel()) this.unloaded = true;
	}
}
