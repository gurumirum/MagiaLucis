package gurumirum.magialucis.contents.block.lux.ambercore;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.capability.LuxNetLinkDestination;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.capability.ModCapabilities;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.impl.field.FieldElement;
import gurumirum.magialucis.impl.field.FieldInstance;
import gurumirum.magialucis.impl.field.FieldManager;
import gurumirum.magialucis.impl.field.Fields;
import gurumirum.magialucis.impl.luxnet.InWorldLinkState;
import gurumirum.magialucis.impl.luxnet.LinkContext;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxSourceNodeInterface;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector3d;

import java.util.List;

public class AmberCoreBlockEntity extends LuxNodeBlockEntity implements LuxSourceNodeInterface {
	private @Nullable BlockPos.MutableBlockPos mpos;

	private double power;

	public AmberCoreBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.AMBER_CORE.get(), pos, blockState);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		registerField();
	}

	@Override
	public void onChunkUnloaded() {
		super.onChunkUnloaded();
		unregisterField();
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		unregisterField();
	}

	@Override
	public void clearRemoved() {
		super.clearRemoved();
		registerField();
	}

	private void registerField() {
		FieldInstance fieldInstance = FieldManager.tryGetField(this.level, Fields.AMBER_CORE);
		if (fieldInstance != null) {
			FieldElement e = fieldInstance.add(getBlockPos()).listenPowerChange(power -> {
				this.power = power;
				syncToClient();
			});
			this.power = e.power();
		}
	}

	private void unregisterField() {
		FieldInstance fieldInstance = FieldManager.tryGetField(this.level, Fields.AMBER_CORE);
		if (fieldInstance != null) fieldInstance.remove(getBlockPos());
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
	public void syncLinkStatus(@NotNull @UnmodifiableView Int2ObjectMap<InWorldLinkState> linkIndexToState) {}

	@Override
	public void generateLux(Vector3d dest) {
		dest.set(10, 5, 0).mul(this.power);
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
