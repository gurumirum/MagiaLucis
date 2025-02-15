package gurumirum.magialucis.contents.block.lux.charger;

import gurumirum.magialucis.contents.block.BaseLanternBlock;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.impl.LuxStatTooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RemoteChargerBlock extends BaseLanternBlock implements EntityBlock {
	private final ChargerTier chargerTier;

	public RemoteChargerBlock(Properties properties, ChargerTier chargerTier) {
		super(properties);
		this.chargerTier = chargerTier;
	}

	@Override
	public @Nullable RemoteChargerBlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new RemoteChargerBlockEntity(this.chargerTier, pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
	                                                                        @NotNull BlockEntityType<T> blockEntityType) {
		return Ticker.server(level);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		tooltip.add(Component.translatable("item.magialucis.tooltip.remote_charger"));
		LuxStatTooltip.formatStat(this.chargerTier.stat(), tooltip, LuxStatTooltip.Type.CONSUMER);
	}
}
