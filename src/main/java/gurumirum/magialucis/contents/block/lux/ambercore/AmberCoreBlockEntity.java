package gurumirum.magialucis.contents.block.lux.ambercore;

import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.impl.field.Field;
import gurumirum.magialucis.impl.field.Fields;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import gurumirum.magialucis.utils.ServerTickQueue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static gurumirum.magialucis.contents.block.ModBlockStateProps.OVERSATURATED;
import static gurumirum.magialucis.contents.block.ModBlockStateProps.SKYLIGHT_INTERFERENCE;

public class AmberCoreBlockEntity extends LuxNodeBlockEntity<AmberCoreBehavior> implements Ticker.Server {
	private static final int CYCLE = 50;

	private @Nullable BlockPos.MutableBlockPos mpos;
	private double power;

	public AmberCoreBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.AMBER_CORE.get(), pos, blockState);
	}

	@Override
	protected @NotNull AmberCoreBehavior createNodeBehavior() {
		return new AmberCoreBehavior();
	}

	@Override
	protected void register() {
		super.register();
		if (!getBlockState().getValue(SKYLIGHT_INTERFERENCE)) registerField(Fields.AMBER_CORE);
	}

	@Override
	protected void unregister(boolean destroyed) {
		super.unregister(destroyed);
		unregisterField(Fields.AMBER_CORE);
	}

	@Override
	protected void setFieldPower(@NotNull Field field, double power) {
		if (field == Fields.AMBER_CORE) {
			this.power = power;
			ServerTickQueue.tryEnqueue(this.level, this::updateOversaturatedProperty);
			updatePower();
		}
	}

	private void updateOversaturatedProperty() {
		if (!updateProperty(OVERSATURATED, !getBlockState().getValue(SKYLIGHT_INTERFERENCE) && this.power < 1))
			syncToClient();
	}

	private void updatePower() {
		nodeBehavior().setPower(getBlockState().getValue(SKYLIGHT_INTERFERENCE) ? 0 : this.power);
	}

	@Override
	public void updateServer(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
		if (level.getGameTime() % CYCLE != 0) return;

		if (this.mpos == null) this.mpos = new BlockPos.MutableBlockPos();
		int brightnessMax = 0;

		for (Direction d : Direction.values()) {
			this.mpos.set(getBlockPos()).move(d);
			brightnessMax = Math.max(brightnessMax, level.getBrightness(LightLayer.SKY, this.mpos));
		}

		boolean skylightInterference = brightnessMax >= 8;
		if (updateProperty(SKYLIGHT_INTERFERENCE, skylightInterference)) {
			if (skylightInterference) unregisterField(Fields.AMBER_CORE);
			else registerField(Fields.AMBER_CORE);
			updatePower();
		}
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector) {
		if (this.level == null) return;
		if (this.mpos == null) this.mpos = new BlockPos.MutableBlockPos();

		BlockPos pos = getBlockPos();
		int i = 0;

		for (Direction dir : Direction.values()) {
			this.mpos.set(pos).move(dir);
			if (LuxUtils.directLinkToInWorldNode(this, linkCollector, this.mpos, dir.getOpposite(),
					i, null, false)) {
				i++;
			}
		}
	}

	@Override
	protected void save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.save(tag, lookupProvider, context);
		if (context.isSync()) {
			tag.putDouble("power", this.power);
		}
	}

	@Override
	protected void load(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.load(tag, lookupProvider, context);
		if (context.isSync()) {
			this.power = tag.getDouble("power");
		}
	}

	@Override
	public void addDebugText(@NotNull List<String> list) {
		super.addDebugText(list);
		list.add("Power: " + this.power);
	}
}
