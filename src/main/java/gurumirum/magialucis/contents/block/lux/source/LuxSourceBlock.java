package gurumirum.magialucis.contents.block.lux.source;

import gurumirum.magialucis.api.capability.LuxStat;
import gurumirum.magialucis.impl.LuxStatTooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LuxSourceBlock extends Block implements EntityBlock {
	private final LuxStat stat;
	private final double luxGeneration;

	public LuxSourceBlock(Properties properties, double luxGeneration) {
		this(properties, LuxStat.simple(0, luxGeneration, luxGeneration, luxGeneration), luxGeneration);
	}

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

	@Override
	public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
	                            @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
		LuxStatTooltip.formatStat(this.stat, tooltipComponents, LuxStatTooltip.Type.SOURCE);
	}
}
