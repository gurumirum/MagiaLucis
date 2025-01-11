package gurumirum.magialucis.contents.block.sunlight;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.impl.luxnet.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector3d;

public class SunlightCoreBlockEntity extends LuxNodeBlockEntity implements LuxSourceNodeInterface {
	public static final LuxStat STAT = LuxStat.simple(
			GemStats.CITRINE.color(),
			0, // don't make cores just ignore foci
			GemStats.CITRINE.rMaxTransfer(),
			GemStats.CITRINE.gMaxTransfer(),
			0); // regular sunlight cores cannot receive blue light

	public static final double LINK_DISTANCE = 7;

	public SunlightCoreBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.SUNLIGHT_CORE.get(), pos, blockState);
	}

	@Override
	public void generateLux(Vector3d dest) {
		SunlightLogic.getColor(this.level, getBlockPos(), SunlightLogic.DEFAULT_BASE_INTENSITY, dest);
	}

	@Override
	public @Nullable LuxStat calculateNodeStat(LuxNet luxNet) {
		return STAT;
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector) {
		LuxUtils.linkToInWorldNode(this, linkCollector, (float)(Math.PI / 2), 0, LINK_DISTANCE,
				luxNodeId(), 0, null);
	}

	@Override
	public void syncLinkStatus(@NotNull @UnmodifiableView Int2ObjectMap<InWorldLinkState> linkIndexToState) {}

	@Override
	public int getLinkDestinationId(int sourceId, @Nullable BlockHitResult hitResult) {
		LuxNet luxNet = getLuxNet();
		if (luxNet == null) return NO_ID;
		LuxNode src = luxNet.get(sourceId);
		if (src == null) return NO_ID;
		return src.iface() instanceof SunlightFocusBlockEntity ? luxNodeId() : NO_ID;
	}
}
