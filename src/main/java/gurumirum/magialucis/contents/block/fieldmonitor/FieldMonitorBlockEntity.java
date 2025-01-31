package gurumirum.magialucis.contents.block.fieldmonitor;

import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.block.BlockEntityBase;
import gurumirum.magialucis.contents.block.DebugTextProvider;
import gurumirum.magialucis.impl.field.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class FieldMonitorBlockEntity extends BlockEntityBase implements DebugTextProvider {
	private @Nullable ResourceLocation fieldId;
	private double influenceSum;

	private FieldListener listener = FieldListener.invalid();

	public FieldMonitorBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.FIELD_MONITOR.get(), pos, blockState);
	}

	public @Nullable ResourceLocation fieldId() {
		return fieldId;
	}

	public void setFieldId(@Nullable ResourceLocation fieldId) {
		if (Objects.equals(this.fieldId, fieldId)) return;
		this.fieldId = fieldId;
		registerField();
		setChanged();
	}

	@Override protected void register() {
		registerField();
	}

	@Override protected void unregister(boolean destroyed) {
		unregisterField();
	}

	private void registerField() {
		this.listener.invalidate();

		Field f = FieldRegistry.fields().get(this.fieldId);
		if (f == null) return;

		FieldInstance inst = FieldManager.tryGetField(this.level, f);
		if (inst == null) return;

		this.listener = inst.listener().fieldChanged((pos, element, removed) -> {
			update(element.fieldInstance());
		});

		update(inst);
	}

	private void unregisterField() {
		this.listener.invalidate();
	}

	private void update(FieldInstance fieldInstance) {
		double newInfluenceSum = fieldInstance.influenceSum(getBlockPos());
		if (this.influenceSum != newInfluenceSum) {
			this.influenceSum = newInfluenceSum;
			syncToClient();
		}
	}

	@Override
	protected void save(CompoundTag tag, HolderLookup.Provider lookupProvider, SaveLoadContext context) {
		super.save(tag, lookupProvider, context);
		if (this.fieldId != null) tag.putString("fieldId", this.fieldId.toString());
		if (context.isSync()) {
			tag.putDouble("influenceSum", this.influenceSum);
		}
	}

	@Override
	protected void load(CompoundTag tag, HolderLookup.Provider lookupProvider, SaveLoadContext context) {
		super.load(tag, lookupProvider, context);
		this.fieldId = tag.contains("fieldId", Tag.TAG_STRING) ?
				ResourceLocation.tryParse(tag.getString("fieldId")) : null;
		if (context.isSync()) {
			this.influenceSum = tag.getDouble("influenceSum");
		}
	}

	@Override
	protected void applyImplicitComponents(@NotNull DataComponentInput componentInput) {
		super.applyImplicitComponents(componentInput);
		setFieldId(componentInput.get(ModDataComponents.FIELD_ID.get()));
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.@NotNull Builder components) {
		super.collectImplicitComponents(components);
		if (this.fieldId != null) components.set(ModDataComponents.FIELD_ID.get(), this.fieldId);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void removeComponentsFromTag(@NotNull CompoundTag tag) {
		super.removeComponentsFromTag(tag);
		tag.remove("fieldId");
	}

	@Override
	public void addDebugText(@NotNull List<String> list) {
		if (this.fieldId == null) return;
		list.add("Field Monitor at [" + getBlockPos().toShortString() + "]");
		list.add("Field: " + this.fieldId);
		list.add("Field Influence Sum: " + this.influenceSum);
	}
}
