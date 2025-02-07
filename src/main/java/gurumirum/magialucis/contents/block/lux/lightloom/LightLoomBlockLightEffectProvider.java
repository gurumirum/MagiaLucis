package gurumirum.magialucis.contents.block.lux.lightloom;

import gurumirum.magialucis.client.render.light.BlockLightEffectProvider;
import gurumirum.magialucis.client.render.light.LightEffect;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class LightLoomBlockLightEffectProvider extends BlockLightEffectProvider<LightLoomBlockEntity> {
	public LightLoomBlockLightEffectProvider(LightLoomBlockEntity blockEntity) {
		super(blockEntity);
		sphereSize(0.75f);
		raySize(0.75f);
	}

	@Override
	protected void rayEffects(float partialTicks, Collector collector, Vector3d luxFlow, float radiusModifier) {
		if (!this.blockEntity.clientSideActive()) return;

		Vec3 origin = origin();
		if (origin == null) return;

		double sum = LuxUtils.sum(luxFlow);
		if (sum <= 1) return;

		Direction facing = this.blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
		collector.addCylindricalEffect(LightEffect.rayRadius(sum) * radiusModifier, origin, origin.add(
						14 / 16.0 * facing.getStepX(),
						-8 / 16.0,
						14 / 16.0 * facing.getStepZ()),
				LightEffect.getLightColor(luxFlow), false);
	}
}
