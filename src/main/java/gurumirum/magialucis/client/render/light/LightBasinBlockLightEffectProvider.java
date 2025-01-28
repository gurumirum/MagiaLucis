package gurumirum.magialucis.client.render.light;

import gurumirum.magialucis.contents.block.ModBlockStateProps;
import gurumirum.magialucis.contents.block.lux.lightbasin.LightBasinBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LightBasinBlockLightEffectProvider extends BlockLightEffectProvider<LightBasinBlockEntity> {
	public LightBasinBlockLightEffectProvider(LightBasinBlockEntity blockEntity) {
		super(blockEntity, 0.5f);
	}

	@Override
	protected @Nullable Vec3 origin() {
		BlockPos pos = this.blockEntity.getBlockPos();
		return Vec3.atLowerCornerWithOffset(pos, 0.5, 1.25, 0.5);
	}

	@Override
	public void getLightEffects(float partialTicks, @NotNull Collector collector) {
		if (this.blockEntity.getBlockState().getValue(ModBlockStateProps.WORKING)) {
			super.getLightEffects(partialTicks, collector);
		}
	}
}
