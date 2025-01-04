package gurumirum.gemthing.contents.block;

import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.impl.RelaySingleton;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class RelayBlockEntity extends BlockEntity {
	private final LinkedList<BlockPos> edges = new LinkedList<>();

	public RelayBlockEntity(BlockPos pos, BlockState blockState) {
		super(Contents.RELAY_BLOCK_ENTITY.get(), pos, blockState);
	}

	public boolean linkRelay(BlockPos origin) {
		if (level == null) return false;
		RelayBlockEntity relay = (RelayBlockEntity)level.getBlockEntity(origin);
		if (relay == null) return false;
		if (origin.equals(this.getBlockPos())) return false;
		if (!validateEdge(origin)) return false;
		RelaySingleton relaySingleton = RelaySingleton.getInstance();
		if (!relaySingleton.addEdge(origin, this.getBlockPos())) return false;
		relay.edges.add(this.getBlockPos());
		relay.setChanged();
		return true;
	}

	private boolean validateEdge(BlockPos pPos) {
		return this.getBlockPos().distSqr(pPos) < 20;
	}


	@Override
	protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);
		ListTag edgesTag = (ListTag)tag.get("edges");
		if (edgesTag == null) return;
		for (int i = 0; i < tag.getInt("edges_num"); i++) {
			int x = edgesTag.getIntArray(i)[0];
			int y = edgesTag.getIntArray(i)[1];
			int z = edgesTag.getIntArray(i)[2];
			BlockPos e = new BlockPos(x, y, z);
			edges.add(e);
			RelaySingleton.getInstance().addEdge(this.getBlockPos(), e);
		}

	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);
		ListTag edgesTag = new ListTag();
		edges.forEach(edge -> edgesTag.add(NbtUtils.writeBlockPos(edge)));
		tag.putInt("edges_num", edges.size());
		tag.put("edges", edgesTag);
	}
}
