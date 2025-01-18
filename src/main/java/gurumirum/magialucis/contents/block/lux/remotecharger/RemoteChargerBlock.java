package gurumirum.magialucis.contents.block.lux.remotecharger;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.impl.LuxStatTooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class RemoteChargerBlock extends Block implements EntityBlock {
	public static final LuxStat BASIC_STAT = GemStats.BRIGHTSTONE;
	public static final LuxStat ADVANCED_STAT = GemStats.PURIFIED_QUARTZ;

	private final LuxStat stat;

	public RemoteChargerBlock(Properties properties, LuxStat stat) {
		super(properties);
		this.stat = stat;
	}

	@Override
	public abstract @Nullable RemoteChargerBlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state);

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
	                                                                        @NotNull BlockEntityType<T> blockEntityType) {
		return Ticker.server(level);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		LuxStatTooltip.formatStat(this.stat, tooltip);
	}

	public static class Basic extends RemoteChargerBlock {
		public Basic(Properties properties) {
			super(properties, BASIC_STAT);
		}

		@Override
		public @Nullable RemoteChargerBlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
			return RemoteChargerBlockEntity.basic(pos, state);
		}
	}

	public static class Advanced extends RemoteChargerBlock {
		public Advanced(Properties properties) {
			super(properties, ADVANCED_STAT);
		}

		@Override
		public @Nullable RemoteChargerBlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
			return RemoteChargerBlockEntity.advanced(pos, state);
		}
	}
}
