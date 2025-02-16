package gurumirum.magialucis.contents.block.lux.sunlight.core;

import gurumirum.magialucis.api.luxnet.LuxNet;
import gurumirum.magialucis.api.luxnet.LuxNode;
import gurumirum.magialucis.api.luxnet.behavior.LuxSpecialNodeBehavior;
import gurumirum.magialucis.impl.luxnet.behavior.SingleFieldLuxNodeBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public abstract class BaseSunlightCoreNodeBehavior extends SingleFieldLuxNodeBehavior implements LuxSpecialNodeBehavior {
	public BaseSunlightCoreNodeBehavior(@NotNull BlockPos pos) {
		super(pos);
	}

	@Override
	public void alterIncomingLux(@NotNull ServerLevel level, @NotNull LuxNet luxNet, @NotNull LuxNode node, @NotNull Vector3d incomingLux) {
		incomingLux.mul(fieldPower());
	}

	public BaseSunlightCoreNodeBehavior(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super(tag, lookupProvider);
	}
}
