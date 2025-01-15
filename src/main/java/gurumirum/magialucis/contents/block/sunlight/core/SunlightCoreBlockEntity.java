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

public class SunlightCoreBlockEntity extends BaseSunlightCoreBlockEntity {
	public static final LuxStat STAT = LuxStat.simple(
			GemStats.CITRINE.color(),
			0, // don't make cores just ignore foci
			GemStats.CITRINE.rMaxTransfer(),
			GemStats.CITRINE.gMaxTransfer(),
			0); // regular sunlight cores cannot receive blue light

	public SunlightCoreBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.SUNLIGHT_CORE.get(), pos, blockState);
	}

	@Override
	protected @Nullable Field field() {
		return Fields.SUNLIGHT_CORE;
	}

	@Override
	public @Nullable LuxStat calculateNodeStat(LuxNet luxNet) {
		return STAT;
	}
}
