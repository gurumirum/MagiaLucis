package gurumirum.magialucis.contents.block.lux.lightbasin;

import gurumirum.magialucis.client.render.light.BlockLightEffectProvider;
import gurumirum.magialucis.contents.block.ModBlockStates;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LightBasinBlockLightEffectProvider extends BlockLightEffectProvider<LightBasinBlockEntity> {
	public LightBasinBlockLightEffectProvider(LightBasinBlockEntity blockEntity) {
		super(blockEntity);
		sphereSize(0.5f);
	}

	@Override
	protected @Nullable Vec3 origin() {
		BlockPos pos = this.blockEntity.getBlockPos();
		return Vec3.atLowerCornerWithOffset(pos, 0.5, 1.25, 0.5);
	}

	@Override
	public void getLightEffects(float partialTicks, @NotNull Collector collector) {
		if (this.blockEntity.getBlockState().getValue(ModBlockStates.WORKING)) {
			super.getLightEffects(partialTicks, collector);
		}
	}
}
