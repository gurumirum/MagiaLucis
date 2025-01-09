package gurumirum.magialucis.contents.block.fieldmonitor;

import gurumirum.magialucis.contents.Contents;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.block.DebugTextProvider;
import gurumirum.magialucis.contents.block.SyncedBlockEntity;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.impl.field.Field;
import gurumirum.magialucis.impl.field.FieldInstance;
import gurumirum.magialucis.impl.field.FieldManager;
import gurumirum.magialucis.impl.field.FieldRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FieldMonitorBlockEntity extends SyncedBlockEntity implements Ticker, DebugTextProvider {
	private static final double CYCLE = 5;

	private @Nullable ResourceLocation fieldId;
	private double influenceSum;

	public FieldMonitorBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.FIELD_MONITOR.get(), pos, blockState);
	}

	@Override
	public void updateServer(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
		if (level.getGameTime() % CYCLE != 0) return;
		Field f = FieldRegistry.fields().get(this.fieldId);
		if (f == null) return;
		FieldManager m = FieldManager.tryGet(level);
		if (m == null) return;
		FieldInstance fieldInstance = m.get(f);
		if (fieldInstance == null) return;
		double influenceSum = fieldInstance.influenceSum(pos);
		if (this.influenceSum == influenceSum) return;
		this.influenceSum = influenceSum;
		syncToClient();
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
		this.fieldId = componentInput.get(Contents.FIELD_ID.get());
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.@NotNull Builder components) {
		super.collectImplicitComponents(components);
		if (this.fieldId != null) components.set(Contents.FIELD_ID.get(), fieldId);
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
