package gurumirum.magialucis.contents.block.lux.ambercore;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.capability.LuxNetLinkDestination;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.capability.ModCapabilities;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.impl.field.Field;
import gurumirum.magialucis.impl.field.Fields;
import gurumirum.magialucis.impl.luxnet.LinkContext;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxSourceNodeInterface;
import gurumirum.magialucis.utils.ServerTickQueue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.List;

import static gurumirum.magialucis.contents.block.ModBlockStateProps.OVERSATURATED;
import static gurumirum.magialucis.contents.block.ModBlockStateProps.SKYLIGHT_INTERFERENCE;

public class AmberCoreBlockEntity extends LuxNodeBlockEntity implements LuxSourceNodeInterface, Ticker.Server {
	private static final int CYCLE = 50;

	private @Nullable BlockPos.MutableBlockPos mpos;
	private double power;

	public AmberCoreBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.AMBER_CORE.get(), pos, blockState);
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
		}
	}

	private void updateOversaturatedProperty() {
		if (!updateProperty(OVERSATURATED, !getBlockState().getValue(SKYLIGHT_INTERFERENCE) && this.power < 1))
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
			if (skylightInterference) unregisterField(Fields.AMBER_CORE);
			else registerField(Fields.AMBER_CORE);
		}
	}

	@Override
	public @Nullable LuxStat calculateNodeStat(LuxNet luxNet) {
		return GemStats.AMBER;
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector) {
		if (this.level == null) return;
		if (this.mpos == null) this.mpos = new BlockPos.MutableBlockPos();

		BlockPos pos = getBlockPos();
		int i = 0;

		for (Direction dir : Direction.values()) {
			this.mpos.set(pos).move(dir);
			BlockState state = this.level.getBlockState(this.mpos);
			if (state.is(ModBlocks.RELAY.block()) && state.getValue(BlockStateProperties.FACING) == dir) {
				LuxNetLinkDestination dest = this.level.getCapability(ModCapabilities.LUX_NET_LINK_DESTINATION, this.mpos, null);
				if (dest != null) {
					linkCollector.inWorldLink(i++,
							dest.linkWithSource(new LinkContext(this.level, getLuxNet(), luxNodeId(), null)).nodeId(),
							pos, this.mpos.immutable(), Vec3.atCenterOf(this.mpos));
				}
			}
		}
	}

	@Override
	public void generateLux(Vector3d dest) {
		if (!getBlockState().getValue(SKYLIGHT_INTERFERENCE)) dest.set(10, 5, 0).mul(this.power);
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
