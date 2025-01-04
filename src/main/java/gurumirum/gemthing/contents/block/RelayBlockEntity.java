package gurumirum.gemthing.contents.block;

import gurumirum.gemthing.capability.LinkSource;
import gurumirum.gemthing.capability.LuxNodeBlock;
import gurumirum.gemthing.capability.ModCapabilities;
import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.impl.LuxNet;
import gurumirum.gemthing.impl.LuxNetEvent;
import gurumirum.gemthing.impl.LuxNode;
import gurumirum.gemthing.impl.LuxNodeInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class RelayBlockEntity extends LuxNodeBlockEntity implements LinkSource, LuxNodeInterface, LuxNodeBlock {
	public static final float REACH = 20;

	private final Vector3f direction = new Vector3f(0, 0, 1);
	private boolean hasOutboundConnection;

	public RelayBlockEntity(BlockPos pos, BlockState blockState) {
		super(Contents.RELAY_BLOCK_ENTITY.get(), pos, blockState);
	}

	public void getDirection(Vector3f dest) {
		dest.set(this.direction);
	}

	@Override
	public void link(@NotNull Vec3 target) {
		int nodeId = luxNodeId();
		if (nodeId == NO_ID) return;
		Level level = this.level;
		if (level == null) return;

		BlockPos pos = getBlockPos();
		this.direction.set(
						target.x - .5f - pos.getX(),
						target.y - .5f - pos.getY(),
						target.z - .5f - pos.getZ())
				.normalize();

		LuxNet luxNet = LuxNet.tryGet(this.level);
		if (luxNet != null) luxNet.queueLinkUpdate(nodeId);
		setChanged();
		syncToClient();
	}

	@Override
	public void updateLink() {
		LuxNet luxNet = LuxNet.tryGet(level);
		if (luxNet == null) return;

		BlockPos pos = getBlockPos();

		if (this.direction.isFinite()) {
			BlockHitResult hitResult = level.clip(new ClipContext(
					Vec3.atCenterOf(pos).add(this.direction.x,
							this.direction.y,
							this.direction.z),
					new Vec3(
							pos.getX() + .5f + this.direction.x * REACH,
							pos.getY() + .5f + this.direction.y * REACH,
							pos.getZ() + .5f + this.direction.z * REACH),
					ClipContext.Block.VISUAL, ClipContext.Fluid.ANY,
					CollisionContext.empty()));

			if (hitResult.getType() == HitResult.Type.BLOCK && !hitResult.getBlockPos().equals(pos)) {
				LuxNodeBlock luxNodeBlock = level.getCapability(ModCapabilities.LUX_NODE_BLOCK, hitResult.getBlockPos(), hitResult.getDirection());
				if (luxNodeBlock != null) {
					luxNet.link(luxNodeId(), luxNodeBlock.luxNodeId());
					return;
				}
			}
		}
		luxNet.unlink(luxNodeId());
	}

	@Override
	protected void readInitialLuxNodeData(LuxNet luxNet) {
		LuxNode node = luxNet.get(luxNodeId());
		if (node != null) {
			setHasOutboundConnection(node.outboundNode() != NO_ID);
		}
	}

	@Override
	public void connectionUpdated(LuxNetEvent.ConnectionUpdated connectionUpdated) {
		if (luxNodeId() == connectionUpdated.sourceNode()) {
			setHasOutboundConnection(connectionUpdated.newDestinationNode() != NO_ID);
		}
	}

	public boolean hasOutboundConnection() {
		return this.hasOutboundConnection;
	}

	private void setHasOutboundConnection(boolean value) {
		if (this.hasOutboundConnection == value) return;
		this.hasOutboundConnection = value;
		syncToClient();
	}

	@Override
	public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
		CompoundTag tag = new CompoundTag();
		saveAdditional(tag, registries);
		tag.putBoolean("hasOutboundConnection", this.hasOutboundConnection);
		return tag;
	}

	@Override
	public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
		super.handleUpdateTag(tag, lookupProvider);
		this.hasOutboundConnection = tag.getBoolean("hasOutboundConnection");
	}

	@Override
	protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);
		this.direction.set(
				tag.getFloat("dirX"),
				tag.getFloat("dirY"),
				tag.getFloat("dirZ"));
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);
		tag.putFloat("dirX", this.direction.x);
		tag.putFloat("dirY", this.direction.y);
		tag.putFloat("dirZ", this.direction.z);
	}
}
