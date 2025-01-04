package gurumirum.gemthing.contents.block.lux.remotecharger;

import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.contents.block.Ticker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RemoteChargerBlock extends Block implements EntityBlock {
	public RemoteChargerBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new RemoteChargerBlockEntity(pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
	                                                                        @NotNull BlockEntityType<T> blockEntityType) {
		return blockEntityType != Contents.REMOTE_CHARGER.get() ? null :
				Ticker.getTicker(level, false, true);
	}
}
