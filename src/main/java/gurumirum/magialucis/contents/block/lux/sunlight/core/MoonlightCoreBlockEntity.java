package gurumirum.magialucis.contents.block.lux.sunlight.core;

import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.impl.field.Field;
import gurumirum.magialucis.impl.field.Fields;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MoonlightCoreBlockEntity extends BaseSunlightCoreBlockEntity<MoonlightCoreBehavior> {
	public MoonlightCoreBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.MOONLIGHT_CORE.get(), pos, blockState);
	}

	@Override
	protected @Nullable Field field() {
		return Fields.MOONLIGHT_CORE;
	}

	@Override
	protected double maxLuxInput() {
		return MoonlightCoreBlock.STAT.bMaxTransfer();
	}

	@Override
	protected @NotNull MoonlightCoreBehavior createNodeBehavior() {
		return new MoonlightCoreBehavior();
	}
}
