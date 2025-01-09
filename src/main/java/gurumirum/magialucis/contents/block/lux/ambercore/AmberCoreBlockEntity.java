package gurumirum.magialucis.contents.block.lux.ambercore;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.capability.LuxNetComponent;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.capability.ModCapabilities;
import gurumirum.magialucis.contents.ModBlockEntities;
import gurumirum.magialucis.contents.ModBlocks;
import gurumirum.magialucis.contents.block.lux.LuxNodeBlockEntity;
import gurumirum.magialucis.impl.luxnet.InWorldLinkState;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxSourceNodeInterface;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector3d;

public class AmberCoreBlockEntity extends LuxNodeBlockEntity implements LuxSourceNodeInterface {
	private @Nullable BlockPos.MutableBlockPos mpos;

	public AmberCoreBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.AMBER_CORE.get(), pos, blockState);
	}

	@Override
	public @Nullable LuxStat calculateNodeStat(LuxNet luxNet) {
		return GemStats.AMBER;
	}

	@Override
	public void updateLink(LuxNet luxNet, LuxNet.LinkCollector linkCollector) {
		if (this.level == null) return;
		if (this.mpos == null) this.mpos = new BlockPos.MutableBlockPos();

		BlockPos pos = getBlockPos();
		int i = 0;

		for (Direction dir : Direction.values()) {
			this.mpos.set(pos).move(dir);
			BlockState state = this.level.getBlockState(this.mpos);
			if (state.is(ModBlocks.RELAY.block()) && state.getValue(BlockStateProperties.FACING) == dir) {
				LuxNetComponent luxNetComponent = this.level.getCapability(ModCapabilities.LUX_NET_COMPONENT, this.mpos, null);
				if (luxNetComponent != null) {
					linkCollector.inWorldLink(i++, luxNetComponent.luxNodeId(), pos, Vec3.atCenterOf(this.mpos));
				}
			}
		}
	}

	@Override
	public void syncLinkStatus(@NotNull @UnmodifiableView Int2ObjectMap<InWorldLinkState> linkIndexToState) {}

	@Override
	public void generateLux(Vector3d dest) {
		dest.set(10, 5, 0);
	}
}
