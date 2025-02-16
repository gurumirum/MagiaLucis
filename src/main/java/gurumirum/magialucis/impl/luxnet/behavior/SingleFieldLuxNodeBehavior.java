package gurumirum.magialucis.impl.luxnet.behavior;

import gurumirum.magialucis.api.field.Field;
import gurumirum.magialucis.api.field.FieldElement;
import gurumirum.magialucis.api.luxnet.LuxNet;
import gurumirum.magialucis.api.luxnet.LuxNode;
import gurumirum.magialucis.api.luxnet.behavior.LuxNodeBehavior;
import gurumirum.magialucis.impl.field.FieldManager;
import gurumirum.magialucis.impl.field.ServerFieldInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SingleFieldLuxNodeBehavior implements LuxNodeBehavior {
	public final BlockPos pos;

	private @Nullable FieldElement fieldElement;

	public SingleFieldLuxNodeBehavior(@NotNull BlockPos pos) {
		this.pos = pos.immutable();
	}

	protected abstract @NotNull Field field();

	protected @Nullable FieldElement fieldElement() {
		return this.fieldElement;
	}

	public double fieldPower() {
		FieldElement element = fieldElement();
		return element != null ? element.power() : 0;
	}

	protected void registerField(@NotNull FieldManager fieldManager) {
		ServerFieldInstance inst = fieldManager.getOrCreate(field());
		this.fieldElement = inst.add(this.pos);
	}

	protected void unregisterField(@NotNull FieldManager fieldManager) {
		ServerFieldInstance inst = fieldManager.get(field());
		if (inst != null) inst.remove(this.pos);
		this.fieldElement = null;
	}

	@Override
	public void onBind(@NotNull ServerLevel level, @NotNull LuxNet luxNet, @NotNull LuxNode node) {
		registerField(FieldManager.get(level));
	}

	@Override
	public void onUnbind(@NotNull ServerLevel level, @NotNull LuxNet luxNet, @NotNull LuxNode node) {
		unregisterField(FieldManager.get(level));
	}

	public void save(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		tag.put("pos", NbtUtils.writeBlockPos(this.pos));
	}

	public SingleFieldLuxNodeBehavior(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		this(NbtUtils.readBlockPos(tag, "pos").orElse(BlockPos.ZERO));
	}
}
