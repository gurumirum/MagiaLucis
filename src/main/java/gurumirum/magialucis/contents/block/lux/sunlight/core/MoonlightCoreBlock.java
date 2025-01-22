package gurumirum.magialucis.contents.block.lux.sunlight.core;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.capability.LuxStat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MoonlightCoreBlock extends BaseSunlightCoreBlock {
	public static final LuxStat STAT = LuxStat.simple(
			GemStats.IOLITE.color(),
			0, // don't make cores just ignore foci
			GemStats.IOLITE.rMaxTransfer(),
			GemStats.IOLITE.gMaxTransfer(),
			GemStats.IOLITE.bMaxTransfer());

	public MoonlightCoreBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new MoonlightCoreBlockEntity(pos, state);
	}
}
