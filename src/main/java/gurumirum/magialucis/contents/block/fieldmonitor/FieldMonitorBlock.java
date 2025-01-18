package gurumirum.magialucis.contents.block.fieldmonitor;

import gurumirum.magialucis.contents.ModDataComponents;
import gurumirum.magialucis.contents.block.Ticker;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

public class FieldMonitorBlock extends Block implements EntityBlock {
	public FieldMonitorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new FieldMonitorBlockEntity(pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level,
	                                                                        @NotNull BlockState state,
	                                                                        @NotNull BlockEntityType<T> blockEntityType) {
		return Ticker.server(level);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		ResourceLocation fieldId = stack.get(ModDataComponents.FIELD_ID.get());
		if (fieldId != null) tooltip.add(Component.literal(fieldId + ""));
	}
}
