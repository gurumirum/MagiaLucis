package gurumirum.magialucis.contents.block.sunlight.core;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.impl.field.Field;
import gurumirum.magialucis.impl.field.Fields;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MoonlightCoreBlockEntity extends BaseSunlightCoreBlockEntity {
	public static final LuxStat STAT = LuxStat.simple(
			GemStats.IOLITE.color(),
			0, // don't make cores just ignore foci
			GemStats.IOLITE.rMaxTransfer(),
			GemStats.IOLITE.gMaxTransfer(),
			GemStats.IOLITE.bMaxTransfer());

	public MoonlightCoreBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.MOONLIGHT_CORE.get(), pos, blockState);
	}

	@Override
	protected @Nullable Field field() {
		return Fields.MOONLIGHT_CORE;
	}

	@Override
	public @Nullable LuxStat calculateNodeStat(LuxNet luxNet) {
		return STAT;
	}
}
