package gurumirum.gemthing.contents.block.lux;

import gurumirum.gemthing.capability.LuxNetComponent;
import gurumirum.gemthing.contents.block.BlockEntityUtils;
import gurumirum.gemthing.contents.block.SyncedBlockEntity;
import gurumirum.gemthing.impl.LuxNet;
import gurumirum.gemthing.impl.LuxNode;
import gurumirum.gemthing.impl.LuxNodeInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LuxNodeBlockEntity extends SyncedBlockEntity implements BlockEntityUtils, LuxNodeInterface, LuxNetComponent {
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
		if (luxNet != null) this.nodeId = luxNet.register(this, this.nodeId);
	}

	protected void unregisterLuxNode(boolean destroyed) {
		LuxNet luxNet = LuxNet.tryGet(this.level);
		if (luxNet != null) luxNet.unregister(this.nodeId, destroyed);
		this.nodeId = NO_ID;
	}

	@Override
	protected void load(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.load(tag, lookupProvider, context);
		this.nodeId = tag.getInt("nodeId");
	}

	@Override
	protected void save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider, SaveLoadContext context) {
		super.save(tag, lookupProvider, context);
		tag.putInt("nodeId", this.nodeId);
	}

	protected final @Nullable LuxNode getLuxNode() {
		LuxNet luxNet = getLuxNet();
		return luxNet != null ? luxNet.get(this.nodeId) : null;
	}

	protected final @Nullable LuxNet getLuxNet() {
		return LuxNet.tryGet(this.level);
	}
}
