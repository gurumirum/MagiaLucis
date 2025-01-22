package gurumirum.magialucis.contents.block.sunlight.core;

import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.impl.field.Field;
import gurumirum.magialucis.impl.field.Fields;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SunlightCoreBlockEntity extends BaseSunlightCoreBlockEntity<SunlightCoreBehavior> {
	public SunlightCoreBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.SUNLIGHT_CORE.get(), pos, blockState);
	}

	@Override
	protected @Nullable Field field() {
		return Fields.SUNLIGHT_CORE;
	}

	@Override
	protected double maxLuxInput() {
		return SunlightCoreBlock.STAT.rMaxTransfer() +
				SunlightCoreBlock.STAT.gMaxTransfer() +
				SunlightCoreBlock.STAT.bMaxTransfer();
	}

	@Override
	protected @NotNull SunlightCoreBehavior createNodeBehavior() {
		return new SunlightCoreBehavior();
	}
}
