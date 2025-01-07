package gurumirum.gemthing.contents.block.lux.relay;

import gurumirum.gemthing.capability.LuxStat;
import gurumirum.gemthing.capability.ModCapabilities;
import gurumirum.gemthing.contents.block.lux.LuxNodeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RelayBlock extends LuxNodeBlock {
	private static final VoxelShape SHAPE = box(
			2, 0, 2,
			14, 12, 14
	);

	public RelayBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new RelayBlockEntity(pos, state);
	}

	@Override
	protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
	                                       @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state,
	                                                   @NotNull Level level, @NotNull BlockPos pos,
	                                                   @NotNull Player player, @NotNull InteractionHand hand,
	                                                   @NotNull BlockHitResult hitResult) {
		LuxStat gemStat = stack.getCapability(ModCapabilities.GEM_STAT);

		if (level.getBlockEntity(pos) instanceof RelayBlockEntity relay) {
			if (gemStat == null) {
				if (relay.stack().isEmpty()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
				if (!level.isClientSide) {
					ItemStack relayItem = relay.stack();
					Containers.dropItemStack(level, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, relayItem);
					relay.setStack(ItemStack.EMPTY);
				}
				return ItemInteractionResult.SUCCESS;
			} else {
				if (ItemStack.isSameItem(relay.stack(), stack)) {
					return ItemInteractionResult.CONSUME;
				}
				if (!level.isClientSide) {
					ItemStack split = stack.split(1);
					ItemStack relayItem = relay.stack();
					Containers.dropItemStack(level, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, relayItem);
					relay.setStack(split);
				}
				return ItemInteractionResult.SUCCESS;
			}
		}
		return ItemInteractionResult.FAIL;
	}
}
