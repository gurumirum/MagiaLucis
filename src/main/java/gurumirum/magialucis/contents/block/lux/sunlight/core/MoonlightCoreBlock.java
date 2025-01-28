package gurumirum.magialucis.contents.block.lux.sunlight.core;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.impl.LuxStatTooltip;
import gurumirum.magialucis.impl.field.Fields;
import gurumirum.magialucis.utils.NumberFormats;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MoonlightCoreBlock extends BaseSunlightCoreBlock {
	public static final LuxStat STAT = LuxStat.simple(
			GemStats.IOLITE.color(),
			0, // don't make cores just ignore foci
			0,
			0,
			GemStats.IOLITE.bMaxTransfer());

	public MoonlightCoreBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new MoonlightCoreBlockEntity(pos, state);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag tooltipFlag) {
		tooltip.add(Component.translatable("block.magialucis.moonlight_core.tooltip.0"));
		tooltip.add(Component.translatable("block.magialucis.moonlight_core.tooltip.1"));
		tooltip.add(Component.translatable("item.magialucis.tooltip.interference_threshold",
				NumberFormats.dec(Fields.MOONLIGHT_CORE.interferenceThreshold(), ChatFormatting.YELLOW)));

		LuxStatTooltip.formatStat(STAT, tooltip, LuxStatTooltip.Type.SOURCE);
	}
}
