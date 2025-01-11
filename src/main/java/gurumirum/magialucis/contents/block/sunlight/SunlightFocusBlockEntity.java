package gurumirum.magialucis.contents.block.sunlight;

import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.block.lux.BasicRelayBlockEntity;
import gurumirum.magialucis.impl.RGB332;
import gurumirum.magialucis.impl.luxnet.LinkDestinationSelector;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxSourceNodeInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class SunlightFocusBlockEntity extends BasicRelayBlockEntity implements LuxSourceNodeInterface {
	public static final LuxStat STAT = LuxStat.simple(RGB332.WHITE,
			0,
			// use max throughput of foci for the stat
			SunlightLogic.DEFAULT_BASE_INTENSITY,
			SunlightLogic.DEFAULT_BASE_INTENSITY,
			SunlightLogic.DEFAULT_BASE_INTENSITY);

	public static final LinkDestinationSelector SELECTOR = (l, src, hit) ->
			l.getBlockEntity(hit.getBlockPos()) instanceof SunlightCoreBlockEntity sunlightCore ?
					sunlightCore.luxNodeId() : NO_ID;

	public static final double LINK_DISTANCE = 10;

	public SunlightFocusBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.SUNLIGHT_FOCUS.get(), pos, blockState);
	}

	@Override
	public @Nullable LuxStat calculateNodeStat(LuxNet luxNet) {
		return STAT;
	}

	@Override
	public int maxLinks() {
		return 1;
	}

	@Override
	public double linkDistance() {
		return LINK_DISTANCE;
	}

	@Override
	public void generateLux(Vector3d dest) {
		SunlightLogic.getColor(this.level, getBlockPos(), SunlightLogic.DEFAULT_BASE_INTENSITY, dest);
	}

	@Override
	protected @Nullable LinkDestinationSelector linkDestinationSelector() {
		return SELECTOR;
	}

	@Override
	public int getLinkDestinationId(int sourceId, @Nullable BlockHitResult hitResult) {
		return NO_ID; // cannot connect
	}
}
