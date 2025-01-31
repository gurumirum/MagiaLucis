package gurumirum.magialucis.contents.block.lux.ambercore;

import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.LuxNodeTypes;
import gurumirum.magialucis.impl.field.Field;
import gurumirum.magialucis.impl.field.FieldManager;
import gurumirum.magialucis.impl.field.Fields;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxNode;
import gurumirum.magialucis.impl.luxnet.behavior.LuxGeneratorNodeBehavior;
import gurumirum.magialucis.impl.luxnet.behavior.LuxNodeType;
import gurumirum.magialucis.impl.luxnet.behavior.SingleFieldLuxNodeBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class AmberCoreBehavior extends SingleFieldLuxNodeBehavior implements LuxGeneratorNodeBehavior {
	private boolean disabled;

	public AmberCoreBehavior(@NotNull BlockPos pos, boolean disabled) {
		super(pos);
		this.disabled = disabled;
	}

	public boolean isDisabled() {
		return this.disabled;
	}

	public void setDisabled(@NotNull Level level, boolean disabled) {
		if (this.disabled == disabled) return;
		this.disabled = disabled;

		FieldManager fieldManager = FieldManager.tryGet(level);
		if (fieldManager == null) return;
		if (disabled) unregisterField(fieldManager);
		else registerField(fieldManager);
	}

	@Override
	protected @NotNull Field field() {
		return Fields.AMBER_CORE;
	}

	@Override
	public void onBind(@NotNull ServerLevel level, @NotNull LuxNet luxNet, @NotNull LuxNode node) {
		if (!this.disabled) registerField(FieldManager.get(level));
	}

	@Override
	public @NotNull LuxNodeType<?> type() {
		return LuxNodeTypes.AMBER_CORE;
	}

	@Override
	public @NotNull LuxStat stat() {
		return AmberCoreBlock.STAT;
	}

	@Override
	public void generateLux(@NotNull ServerLevel level, @NotNull LuxNet luxNet, @NotNull LuxNode node, @NotNull Vector3d generatedLux) {
		if (!this.disabled) generatedLux.set(10, 5, 0).mul(fieldPower());
	}

	@Override
	public void save(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.save(tag, lookupProvider);
		tag.putBoolean("disabled", this.disabled);
	}

	public AmberCoreBehavior(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super(tag, lookupProvider);
		this.disabled = tag.getBoolean("disabled");
	}
}
