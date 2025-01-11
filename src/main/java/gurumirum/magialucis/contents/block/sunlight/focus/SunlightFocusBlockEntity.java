package gurumirum.magialucis.contents.block.sunlight.focus;

import gurumirum.magialucis.capability.LuxNetLinkDestination;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.block.lux.BasicRelayBlockEntity;
import gurumirum.magialucis.contents.block.sunlight.SunlightLogic;
import gurumirum.magialucis.contents.block.sunlight.core.SunlightCoreBlockEntity;
import gurumirum.magialucis.impl.RGB332;
import gurumirum.magialucis.impl.luxnet.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class SunlightFocusBlockEntity extends BasicRelayBlockEntity implements LuxSourceNodeInterface, LinkDestinationSelector {
	public static final LuxStat STAT = LuxStat.simple(RGB332.WHITE,
			0,
			// use max throughput of foci for the stat
			SunlightLogic.DEFAULT_BASE_INTENSITY,
			SunlightLogic.DEFAULT_BASE_INTENSITY,
			SunlightLogic.DEFAULT_BASE_INTENSITY);

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
	public @Nullable LinkDestinationSelector linkDestinationSelector() {
		return this;
	}

	@Override
	public @NotNull LuxNetLinkDestination.LinkTestResult linkWithSource(@NotNull LinkContext context) {
		return LinkTestResult.reject(); // cannot connect
	}

	@Override
	public @Nullable LuxNetLinkDestination chooseLinkDestination(@NotNull Level level,
	                                                             @Nullable ServerSideLinkContext context,
	                                                             @NotNull BlockHitResult hitResult) {
		if (hitResult.getBlockPos().getY() < this.getBlockPos().getY()) return null;
		return level.getBlockEntity(hitResult.getBlockPos()) instanceof SunlightCoreBlockEntity sunlightCore ?
				sunlightCore : null;
	}
}
