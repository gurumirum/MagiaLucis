package gurumirum.magialucis.contents.block.lux.sunlight.core;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.capability.LuxStat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SunlightCoreBlock extends BaseSunlightCoreBlock {
	public static final LuxStat STAT = LuxStat.simple(
			GemStats.CITRINE.color(),
			0, // don't make cores just ignore foci
			GemStats.CITRINE.rMaxTransfer(),
			GemStats.CITRINE.gMaxTransfer(),
			0); // regular sunlight cores cannot receive blue light

	public SunlightCoreBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new SunlightCoreBlockEntity(pos, state);
	}
}
