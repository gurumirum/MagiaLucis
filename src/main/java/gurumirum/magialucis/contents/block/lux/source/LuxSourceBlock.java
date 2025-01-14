package gurumirum.magialucis.contents.block.lux.source;

import gurumirum.magialucis.capability.LuxStat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LuxSourceBlock extends Block implements EntityBlock {
	private final LuxStat stat;
	private final double luxGeneration;

	public LuxSourceBlock(Properties properties, LuxStat stat, double luxGeneration) {
		super(properties);
		this.stat = stat;
		this.luxGeneration = luxGeneration;
	}

	public LuxStat stat() {
		return this.stat;
	}

	public double luxGeneration() {
		return this.luxGeneration;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new LuxSourceBlockEntity(pos, state);
	}
}
