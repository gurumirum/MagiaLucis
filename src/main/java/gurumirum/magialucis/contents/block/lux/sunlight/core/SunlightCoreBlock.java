package gurumirum.magialucis.contents.block.lux.sunlight.core;

import gurumirum.magialucis.capability.LuxStat;
import gurumirum.magialucis.contents.Gem;
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

public class SunlightCoreBlock extends BaseSunlightCoreBlock {
	public static final LuxStat STAT = LuxStat.simple(
			0, // don't make cores just ignore foci
			Gem.CITRINE.rMaxTransfer(),
			Gem.CITRINE.gMaxTransfer(),
			0); // regular sunlight cores cannot receive blue light

	public SunlightCoreBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new SunlightCoreBlockEntity(pos, state);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag tooltipFlag) {
		tooltip.add(Component.translatable("block.magialucis.sunlight_core.tooltip.0"));
		tooltip.add(Component.translatable("item.magialucis.tooltip.interference_threshold",
				NumberFormats.dec(Fields.SUNLIGHT_CORE.interferenceThreshold(), ChatFormatting.YELLOW)));

		LuxStatTooltip.formatStat(STAT, tooltip, LuxStatTooltip.Type.SOURCE);
	}
}
