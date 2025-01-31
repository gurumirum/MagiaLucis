package gurumirum.magialucis.contents.block.fieldmonitor;

import gurumirum.magialucis.contents.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
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
	public @NotNull ItemStack getCloneItemStack(@NotNull BlockState state, @NotNull HitResult target,
	                                            @NotNull LevelReader level, @NotNull BlockPos pos,
	                                            @NotNull Player player) {
		ItemStack stack = new ItemStack(this);
		if (level.getBlockEntity(pos) instanceof FieldMonitorBlockEntity fieldMonitor) {
			if (fieldMonitor.fieldId() != null) {
				stack.set(ModDataComponents.FIELD_ID, fieldMonitor.fieldId());
			}
		}
		return stack;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
	                            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
		ResourceLocation fieldId = stack.get(ModDataComponents.FIELD_ID);
		if (fieldId != null) {
			tooltip.add(Component.literal(fieldId + "").withStyle(ChatFormatting.GOLD));
		}
	}
}
