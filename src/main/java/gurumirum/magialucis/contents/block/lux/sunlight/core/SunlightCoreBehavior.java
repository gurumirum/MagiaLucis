package gurumirum.magialucis.contents.block.lux.sunlight.core;

import gurumirum.magialucis.api.capability.LuxStat;
import gurumirum.magialucis.contents.LuxNodeTypes;
import gurumirum.magialucis.api.field.Field;
import gurumirum.magialucis.impl.field.Fields;
import gurumirum.magialucis.api.luxnet.behavior.LuxNodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class SunlightCoreBehavior extends BaseSunlightCoreNodeBehavior {
	public SunlightCoreBehavior(@NotNull BlockPos pos) {
		super(pos);
	}

	@Override
	public @NotNull LuxNodeType<?> type() {
		return LuxNodeTypes.SUNLIGHT_CORE;
	}

	@Override
	public @NotNull LuxStat stat() {
		return SunlightCoreBlock.STAT;
	}

	@Override
	protected @NotNull Field field() {
		return Fields.SUNLIGHT_CORE;
	}

	public SunlightCoreBehavior(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super(tag, lookupProvider);
	}
}
