package gurumirum.magialucis.contents.block.lux.sunlight.core;

import gurumirum.magialucis.api.MagiaLucisApi;
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
	public static final LuxNodeType<SunlightCoreBehavior> NODE_TYPE = new LuxNodeType.Serializable<>(
			MagiaLucisApi.id("sunlight_core"), SunlightCoreBehavior.class,
			SunlightCoreBehavior::save, SunlightCoreBehavior::new);

	public SunlightCoreBehavior(@NotNull BlockPos pos) {
		super(pos);
	}

	@Override
	public @NotNull LuxNodeType<?> type() {
		return NODE_TYPE;
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
