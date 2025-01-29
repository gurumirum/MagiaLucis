package gurumirum.magialucis.contents.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class AmberLampBlock extends BaseLampBlock.Stateless {
	public AmberLampBlock(Properties properties) {
		super(properties);
	}

	@Override protected boolean isRandomlyTicking(@NotNull BlockState state) {
		return true;
	}

	@Override protected void randomTick(@NotNull BlockState state, @NotNull ServerLevel level,
	                                    @NotNull BlockPos pos, @NotNull RandomSource random) {
		// TODO
	}
}
