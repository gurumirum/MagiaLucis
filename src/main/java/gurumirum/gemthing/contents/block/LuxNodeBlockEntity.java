package gurumirum.gemthing.contents.block;

import gurumirum.gemthing.capability.LuxNodeBlock;
import gurumirum.gemthing.impl.LuxNet;
import gurumirum.gemthing.impl.LuxNodeInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class LuxNodeBlockEntity extends SyncedBlockEntity implements BlockEntityUtils, LuxNodeInterface, LuxNodeBlock {
	private int nodeId;

	public LuxNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
	}

	@Override
	public int luxNodeId() {
		return this.nodeId;
	}

	@Override
	public void onLoad() {
		super.onLoad();
		registerLuxNode();
	}

	@Override
	public void onChunkUnloaded() {
		super.onChunkUnloaded();
		unregisterLuxNode(false);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		unregisterLuxNode(true);
	}

	@Override
	public void clearRemoved() {
		super.clearRemoved();
		registerLuxNode();
	}

	protected void registerLuxNode() {
		LuxNet luxNet = LuxNet.tryGet(this.level);
		if (luxNet != null) {
			this.nodeId = luxNet.register(this, this.nodeId);
			readInitialLuxNodeData(luxNet);
		}
	}

	protected void readInitialLuxNodeData(LuxNet luxNet) {}

	protected void unregisterLuxNode(boolean destroyed) {
		LuxNet luxNet = LuxNet.tryGet(this.level);
		if (luxNet != null) luxNet.unregister(this.nodeId, destroyed);
		this.nodeId = NO_ID;
	}

	@Override
	protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);
		this.nodeId = tag.getInt("nodeId");
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);
		tag.putInt("nodeId", this.nodeId);
	}
}
