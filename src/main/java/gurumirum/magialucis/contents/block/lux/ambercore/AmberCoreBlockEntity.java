package gurumirum.magialucis.contents.block.lux.ambercore;

import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.impl.field.FieldInstance;
import gurumirum.magialucis.impl.field.FieldListener;
import gurumirum.magialucis.impl.field.FieldManager;
import gurumirum.magialucis.impl.field.Fields;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxUtils;
import gurumirum.magialucis.utils.ServerTickQueue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
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
	private FieldListener listener = FieldListener.invalid();
	private double clientSidePower;

	public AmberCoreBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.AMBER_CORE.get(), pos, blockState);
	}

	@Override
	protected @NotNull AmberCoreBehavior createNodeBehavior() {
		return new AmberCoreBehavior(getBlockPos(), getBlockState().getValue(SKYLIGHT_INTERFERENCE));
	}

	@Override
	protected void onRegister(@NotNull ServerLevel serverLevel) {
		this.listener.invalidate();

		super.onRegister(serverLevel);

		FieldInstance inst = FieldManager.get(serverLevel).getOrCreate(Fields.AMBER_CORE);
		this.listener = inst.listener()
				.powerChanged(getBlockPos(), power -> {
					ServerTickQueue.tryEnqueue(this.level, this::updateOversaturatedProperty);
				});
	}

	@Override
	protected void onUnregister(@NotNull ServerLevel serverLevel, @NotNull UnregisterContext context) {
		super.onUnregister(serverLevel, context);
		this.listener.invalidate();
	}

	private void updateOversaturatedProperty() {
		if (!updateProperty(OVERSATURATED, !getBlockState().getValue(SKYLIGHT_INTERFERENCE) && nodeBehavior().fieldPower() < 1))
			syncToClient();
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
			nodeBehavior().setDisabled(level, skylightInterference);
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
			tag.putDouble("power", nodeBehavior().fieldPower());
		}
	}

	@Override
	protected void load(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.load(tag, lookupProvider, context);
		if (context.isSync()) {
			this.clientSidePower = tag.getDouble("power");
		}
	}

	@Override
	public void addDebugText(@NotNull List<String> list) {
		super.addDebugText(list);
		list.add("Power: " + this.clientSidePower);
	}
}
