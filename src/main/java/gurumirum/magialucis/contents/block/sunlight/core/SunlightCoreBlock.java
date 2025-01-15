package gurumirum.magialucis.contents.block.sunlight.core;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SunlightCoreBlock extends BaseSunlightCoreBlock {
	public SunlightCoreBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new SunlightCoreBlockEntity(pos, state);
	}
}
