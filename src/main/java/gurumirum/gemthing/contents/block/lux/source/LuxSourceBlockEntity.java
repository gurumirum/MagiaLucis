package gurumirum.gemthing.contents.block.lux.source;

import gurumirum.gemthing.capability.GemStats;
import gurumirum.gemthing.contents.ModBlockEntities;
import gurumirum.gemthing.contents.block.lux.BasicRelayBlockEntity;
import gurumirum.gemthing.impl.LuxNet;
import gurumirum.gemthing.impl.LuxNode;
import gurumirum.gemthing.impl.LuxSourceNodeInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class LuxSourceBlockEntity extends BasicRelayBlockEntity implements LuxSourceNodeInterface {
	public LuxSourceBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.LUX_SOURCE.get(), pos, blockState);
	}

	@Override
	public void updateProperties(@NotNull LuxNet luxNet, @NotNull LuxNode node) {
		node.setStats(GemStats.BRIGHTSTONE);
	}

	@Override
	public void generateLux(Vector3d dest) {
		dest.set(10, 10, 10);
	}
}
