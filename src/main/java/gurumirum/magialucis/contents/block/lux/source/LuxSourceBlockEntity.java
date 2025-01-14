package gurumirum.magialucis.contents.block.lux.source;

import gurumirum.magialucis.capability.LuxNetLinkDestination;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.capability.ModCapabilities;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.block.lux.BasicRelayBlockEntity;
import gurumirum.magialucis.impl.luxnet.LinkContext;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxSourceNodeInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class LuxSourceBlockEntity extends BasicRelayBlockEntity implements LuxSourceNodeInterface {
	private @Nullable BlockPos.MutableBlockPos mpos;

	public LuxSourceBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.LUX_SOURCE.get(), pos, blockState);
	}

	@Override
	public @Nullable LuxStat calculateNodeStat(LuxNet luxNet) {
		if (getBlockState().getBlock() instanceof LuxSourceBlock luxSourceBlock) {
			return luxSourceBlock.stat();
		}
		return null;
	}

	@Override
	public void generateLux(Vector3d dest) {
		if (getBlockState().getBlock() instanceof LuxSourceBlock luxSourceBlock) {
			dest.set(luxSourceBlock.luxGeneration());
		}
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector) {
		super.updateLink(luxNet, linkCollector);
		if (this.level == null) return;
		if (this.mpos == null) this.mpos = new BlockPos.MutableBlockPos();

		BlockPos pos = getBlockPos();
		int i = maxLinks();

		for (Direction dir : Direction.values()) {
			this.mpos.set(pos).move(dir);
			BlockState state = this.level.getBlockState(this.mpos);
			if (state.is(ModBlocks.RELAY.block()) && state.getValue(BlockStateProperties.FACING) == dir) {
				LuxNetLinkDestination dest = this.level.getCapability(ModCapabilities.LUX_NET_LINK_DESTINATION, this.mpos, null);
				if (dest != null) {
					linkCollector.inWorldLink(i++,
							dest.linkWithSource(new LinkContext(this.level, getLuxNet(), luxNodeId(), null)).nodeId(),
							pos, this.mpos.immutable(), Vec3.atCenterOf(this.mpos));
				}
			}
		}
	}
}
