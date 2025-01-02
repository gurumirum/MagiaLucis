package gurumirum.gemthing.contents.block;

import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.impl.RelaySingleton;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedList;

public class RelayBlockEntity extends BlockEntity {
    private final LinkedList<BlockPos> edges = new LinkedList<>();

    public RelayBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public RelayBlockEntity(BlockPos pPos, BlockState pBlockState) {
        this(Contents.RELAY_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public boolean linkRelay(BlockPos pPos) {
        RelayBlockEntity relay = (RelayBlockEntity) level.getBlockEntity(pPos);
        if(relay == null) return false;
        if(pPos.equals(this.getBlockPos())) return false;
        if(!validateEdge(pPos)) return false;
        RelaySingleton relaySingleton = RelaySingleton.getInstance();
        if(!relaySingleton.addEdge(pPos, this.getBlockPos())) return false;
        edges.add(pPos);
        relay.edges.add(this.getBlockPos());
        setChanged();
        relay.setChanged();
        return true;
    }

    private boolean validateEdge(BlockPos pPos) {
        return this.getBlockPos().distSqr(pPos) < 20;
    }


    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        ListTag edgesTag = (ListTag) pTag.get("edges");
        if(edgesTag == null) return;
        for(int i=0; i<pTag.getInt("edges_num"); i++) {
            int x = edgesTag.getIntArray(i)[0];
            int y = edgesTag.getIntArray(i)[1];
            int z = edgesTag.getIntArray(i)[2];
            BlockPos e = new BlockPos(x, y, z);
            edges.add(e);
            RelaySingleton.getInstance().addEdge(this.getBlockPos(), e);
        }

    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        ListTag edgesTag = new ListTag();
        edges.forEach(edge -> edgesTag.add(NbtUtils.writeBlockPos(edge)));
        pTag.putInt("edges_num", edges.size());
        pTag.put("edges", edgesTag);
    }
}
