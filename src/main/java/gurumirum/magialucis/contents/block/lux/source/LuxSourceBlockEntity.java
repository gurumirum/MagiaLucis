package gurumirum.magialucis.contents.block.lux.source;

import gurumirum.magialucis.api.luxnet.LuxNet;
import gurumirum.magialucis.api.luxnet.LuxNetLinkCollector;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.block.lux.BasicRelayBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LuxSourceBlockEntity extends BasicRelayBlockEntity<LuxSourceBehavior> {
	private @Nullable BlockPos.MutableBlockPos mpos;

	public LuxSourceBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.LUX_SOURCE.get(), pos, blockState);
	}

	@Override
	protected @NotNull LuxSourceBehavior createNodeBehavior() {
		if (getBlockState().getBlock() instanceof LuxSourceBlock block) {
			return new LuxSourceBehavior(block.stat(), block.luxGeneration());
		} else {
			return new LuxSourceBehavior();
		}
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNetLinkCollector linkCollector) {
		super.updateLink(luxNet, linkCollector);
		if (this.level == null) return;
		if (this.mpos == null) this.mpos = new BlockPos.MutableBlockPos();

		BlockPos pos = getBlockPos();
		int i = maxLinks();

		for (Direction dir : Direction.values()) {
			this.mpos.set(pos).move(dir);
			if (linkCollector.directLinkToInWorldNode(this, this.mpos, dir.getOpposite(),
					i, null, 1, false)) {
				i++;
			}
		}
	}
}
