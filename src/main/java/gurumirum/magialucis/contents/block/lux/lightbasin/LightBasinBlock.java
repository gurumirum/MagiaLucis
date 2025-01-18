package gurumirum.magialucis.contents.block.lux.lightbasin;

import gurumirum.magialucis.contents.block.Ticker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LightBasinBlock extends Block implements EntityBlock {
	private static final VoxelShape SHAPE = box(0, 0, 0, 16, 12, 16);

	public LightBasinBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new LightBasinBlockEntity(pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level,
	                                                                        @NotNull BlockState state,
	                                                                        @NotNull BlockEntityType<T> blockEntityType) {
		return Ticker.server(level);
	}

	@Override
	protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
	                                       @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level,
	                                                    @NotNull BlockPos pos, @NotNull Player player,
	                                                    @NotNull BlockHitResult hitResult) {
		if (level.getBlockEntity(pos) instanceof LightBasinBlockEntity lightBasin) {
			if (player.isSecondaryUseActive()) {
				lightBasin.dropAllContents();
				return InteractionResult.SUCCESS;
			} else {
				return lightBasin.dropLastContent() ? InteractionResult.SUCCESS : InteractionResult.PASS;
			}
		}
		return InteractionResult.FAIL;
	}

	@Override
	protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state,
	                                                   @NotNull Level level, @NotNull BlockPos pos,
	                                                   @NotNull Player player, @NotNull InteractionHand hand,
	                                                   @NotNull BlockHitResult hitResult) {
		if (hand == InteractionHand.OFF_HAND || player.isSecondaryUseActive() || stack.isEmpty()) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}

		if (level.getBlockEntity(pos) instanceof LightBasinBlockEntity lightBasin) {
			IItemHandlerModifiable inventory = lightBasin.inventory();
			for (int i = 0; i < inventory.getSlots(); i++) {
				stack = inventory.insertItem(i, stack.getCount() == 1 ? stack : stack.copyWithCount(1), level.isClientSide);
				if (stack.isEmpty()) {
					if (!level.isClientSide) stack.shrink(1);
					return ItemInteractionResult.SUCCESS;
				}
			}
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	protected void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
	                        @NotNull BlockState newState, boolean movedByPiston) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof LightBasinBlockEntity lightBasin) {
				lightBasin.dropAllContents();
			}
		}

		super.onRemove(state, level, pos, newState, movedByPiston);
	}
}
