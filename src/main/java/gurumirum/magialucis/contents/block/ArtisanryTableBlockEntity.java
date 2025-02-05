package gurumirum.magialucis.contents.block;

import gurumirum.magialucis.contents.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ArtisanryTableBlockEntity extends BlockEntityBase implements Ticker.Server {
	public ArtisanryTableBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.ARTISANRY_TABLE.get(), pos, blockState);
	}

	@Override public void updateServer(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {

	}
}
