package gurumirum.gemthing.impl;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public final class RelaySingleton {
    private static final MutableGraph<BlockPos> graph = GraphBuilder.directed().allowsSelfLoops(false).build();


    private static RelaySingleton instance;
    private RelaySingleton() {}
    public static RelaySingleton getInstance() {
        if (instance == null) {
            instance = new RelaySingleton();
        }
        return instance;
    }

    public boolean addRelay(BlockPos relay) {
        return graph.addNode(relay);
    }

    public boolean addEdge(BlockPos targetRelay, BlockPos newRelay) {
        return graph.putEdge(targetRelay, newRelay);
    }

    public boolean removeRelay(BlockPos relay) {
        return graph.removeNode(relay);
    }

    public boolean removeEdge (BlockPos targetRelay, BlockPos newRelay) {
        return graph.removeEdge(newRelay, targetRelay);
    }

    public Set<BlockPos> getNearRelays(BlockPos relay) {
        if(!graph.nodes().contains(relay)) return new HashSet<>();
        return graph.adjacentNodes(relay);
    }

    public Set<BlockPos> getInboundNodes(BlockPos relay) {
        if(!graph.nodes().contains(relay)) return new HashSet<>();
        return graph.predecessors(relay);
    }

    public Set<BlockPos> getOutboundNodes(BlockPos relay) {
        if(!graph.nodes().contains(relay)) return new HashSet<>();
        return graph.successors(relay);
    }
}
