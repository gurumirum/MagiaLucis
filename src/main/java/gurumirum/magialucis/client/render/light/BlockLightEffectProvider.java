package gurumirum.magialucis.client.render.light;

import gurumirum.magialucis.capability.LinkSource;
import gurumirum.magialucis.contents.block.lux.LuxNodeSyncPropertyAccess;
import gurumirum.magialucis.impl.luxnet.InWorldLinkInfo;
import gurumirum.magialucis.impl.luxnet.InWorldLinkState;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class BlockLightEffectProvider<T extends BlockEntity & LuxNodeSyncPropertyAccess> implements LightEffectProvider {
	private final T blockEntity;

	public BlockLightEffectProvider(T blockEntity) {
		this.blockEntity = blockEntity;
	}

	@Override
	public void getLightEffects(float partialTicks, @NotNull Collector collector) {
		Vector3d luxFlow = this.blockEntity.luxFlow(new Vector3d());

		if (luxFlow.x > 0 || luxFlow.y > 0 || luxFlow.z > 0) {
			Vec3 center = Vec3.atCenterOf(this.blockEntity.getBlockPos());
			int color = LightEffectRender.getLightColor(luxFlow);

			float radius = (float)(1 / 4.0 * (Math.log10(LuxUtils.sum(luxFlow))));

			collector.addCircularEffect(radius, center, color);

			var outboundLinks = this.blockEntity.outboundLinks();
			if (outboundLinks.size() > 1) luxFlow.div(outboundLinks.size());
			radius = (float)(1 / 10.0 * (Math.log10(LuxUtils.sum(luxFlow))));

			for (var e : outboundLinks.int2ObjectEntrySet()) {
				InWorldLinkInfo info = e.getValue();
				if (info == null) continue;
				collector.addCylindricalEffect(radius, center, Vec3.atCenterOf(info.linkPos()), color, false);
			}

			if (this.blockEntity instanceof LinkSource linkSource) {
				for (int i = 0; i < linkSource.maxLinks(); i++) {
					InWorldLinkState linkState = linkSource.getLinkState(i);
					if (linkState != null && !linkState.linked()) {
						collector.addCylindricalEffect(radius, center, linkState.linkLocation(), color, true);
					}
				}
			}
		}
	}

	@Override
	public boolean isLightEffectProviderValid() {
		return !this.blockEntity.isRemoved();
	}
}
