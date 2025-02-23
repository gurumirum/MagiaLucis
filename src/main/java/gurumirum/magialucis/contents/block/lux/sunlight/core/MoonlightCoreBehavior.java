package gurumirum.magialucis.contents.block.lux.sunlight.core;

import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.api.capability.LuxStat;
import gurumirum.magialucis.api.luxnet.LuxNet;
import gurumirum.magialucis.api.luxnet.LuxNode;
import gurumirum.magialucis.api.luxnet.behavior.LuxNodeType;
import gurumirum.magialucis.api.field.Field;
import gurumirum.magialucis.impl.field.Fields;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class MoonlightCoreBehavior extends BaseSunlightCoreNodeBehavior {
	public static final LuxNodeType<MoonlightCoreBehavior> NODE_TYPE = new LuxNodeType.Serializable<>(
			MagiaLucisApi.id("moonlight_core"), MoonlightCoreBehavior.class,
			MoonlightCoreBehavior::save, MoonlightCoreBehavior::new);

	public MoonlightCoreBehavior(@NotNull BlockPos pos) {
		super(pos);
	}

	@Override
	public @NotNull LuxNodeType<?> type() {
		return NODE_TYPE;
	}

	@Override
	public @NotNull LuxStat stat() {
		return MoonlightCoreBlock.STAT;
	}

	@Override
	public void alterIncomingLux(@NotNull ServerLevel level, @NotNull LuxNet luxNet, @NotNull LuxNode node, @NotNull Vector3d incomingLux) {
		super.alterIncomingLux(level, luxNet, node, incomingLux);
		incomingLux.z -= incomingLux.x + incomingLux.y;
		incomingLux.x = incomingLux.y = 0;
	}

	@Override
	protected @NotNull Field field() {
		return Fields.MOONLIGHT_CORE;
	}

	public MoonlightCoreBehavior(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super(tag, lookupProvider);
	}
}
