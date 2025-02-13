package gurumirum.magialucis.contents.block.lux.ambercore;

import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.block.Ticker;
import gurumirum.magialucis.impl.LuxStatTooltip;
import gurumirum.magialucis.impl.field.Fields;
import gurumirum.magialucis.utils.NumberFormats;
import net.minecraft.ChatFormatting;
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
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static gurumirum.magialucis.contents.block.ModBlockStates.OVERSATURATED;
import static gurumirum.magialucis.contents.block.ModBlockStates.SKYLIGHT_INTERFERENCE;

public class AmberCoreBlock extends Block implements EntityBlock {
	public static final LuxStat STAT = LuxStat.simple(
			0,
			10, 5, 0);

	public AmberCoreBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
				.setValue(SKYLIGHT_INTERFERENCE, false)
				.setValue(OVERSATURATED, false));
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new AmberCoreBlockEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(SKYLIGHT_INTERFERENCE, OVERSATURATED);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level,
	                                                                        @NotNull BlockState state,
	                                                                        @NotNull BlockEntityType<T> blockEntityType) {
		return Ticker.server(level);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag tooltipFlag) {
		tooltip.add(Component.translatable("block.magialucis.tooltip.connects_to_attached"));
		tooltip.add(Component.translatable("block.magialucis.amber_core.tooltip.0"));
		tooltip.add(Component.translatable("block.magialucis.amber_core.tooltip.1"));
		tooltip.add(Component.translatable("item.magialucis.tooltip.interference_threshold",
				NumberFormats.dec(Fields.AMBER_CORE.interferenceThreshold(), ChatFormatting.YELLOW)));

		LuxStatTooltip.formatStat(STAT, tooltip, LuxStatTooltip.Type.SOURCE);
	}

	public static int getLightValue(@NotNull BlockState state) {
		return state.getValue(SKYLIGHT_INTERFERENCE) ? 0 : state.getValue(OVERSATURATED) ? 6 : 9;
	}
}
