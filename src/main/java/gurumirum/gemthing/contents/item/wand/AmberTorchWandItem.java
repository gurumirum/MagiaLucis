package gurumirum.gemthing.contents.item.wand;

import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.contents.ModBlocks;
import gurumirum.gemthing.contents.item.LuxBatteryItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AmberTorchWandItem extends LuxBatteryItem {
	public static final int COST_PER_LIGHT_SOURCE = 2;

	public AmberTorchWandItem(Properties properties) {
		super(properties.stacksTo(1));
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		// TODO
		return InteractionResultHolder.success(stack);
	}

	@Override
	public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		long charge = stack.getOrDefault(Contents.LUX_CHARGE, 0L);
		if (charge < COST_PER_LIGHT_SOURCE) return InteractionResult.FAIL;

		BlockPos pos = context.getClickedPos();
		BlockState state = context.getLevel().getBlockState(pos);

		if (state.is(ModBlocks.AMBER_LIGHT.block())) return InteractionResult.FAIL;
		if (!state.canBeReplaced()) {
			pos = pos.relative(context.getClickedFace());
			state = context.getLevel().getBlockState(pos);

			if (state.is(ModBlocks.AMBER_LIGHT.block())) return InteractionResult.FAIL;
			if (!state.canBeReplaced()) return InteractionResult.PASS;
		}

		context.getLevel().setBlockAndUpdate(pos, ModBlocks.AMBER_LIGHT.block().defaultBlockState());

		stack.set(Contents.LUX_CHARGE, charge - COST_PER_LIGHT_SOURCE);
		applyCooldown(context.getPlayer());

		return InteractionResult.SUCCESS;
	}

	private void applyCooldown(@Nullable Player player) {
		if (player == null) return;
		player.getCooldowns().addCooldown(this, 10);
	}
}
