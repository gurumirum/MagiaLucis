package gurumirum.magialucis.contents.block.lux.source;

import gurumirum.magialucis.api.capability.LuxStat;
import gurumirum.magialucis.api.luxnet.LuxNet;
import gurumirum.magialucis.api.luxnet.LuxNode;
import gurumirum.magialucis.api.luxnet.behavior.LuxGeneratorNodeBehavior;
import gurumirum.magialucis.api.luxnet.behavior.LuxNodeType;
import gurumirum.magialucis.contents.LuxNodeTypes;
import gurumirum.magialucis.impl.luxnet.behavior.DynamicLuxNodeBehavior;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class LuxSourceBehavior extends DynamicLuxNodeBehavior implements LuxGeneratorNodeBehavior {
	private double luxGeneration;

	public LuxSourceBehavior() {}

	public LuxSourceBehavior(@Nullable LuxStat copyFrom, double luxGeneration) {
		super(copyFrom);
		this.luxGeneration = luxGeneration;
	}

	@Override
	public @NotNull LuxNodeType<?> type() {
		return LuxNodeTypes.SOURCE;
	}

	@Override
	public void generateLux(@NotNull ServerLevel level, @NotNull LuxNet luxNet, @NotNull LuxNode node, @NotNull Vector3d generatedLux) {
		generatedLux.set(this.luxGeneration);
	}

	@Override
	public void save(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.save(tag, lookupProvider);
		tag.putDouble("luxGeneration", this.luxGeneration);
	}

	public LuxSourceBehavior(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super(tag, lookupProvider);
		this.luxGeneration = tag.getDouble("luxGeneration");
	}
}
