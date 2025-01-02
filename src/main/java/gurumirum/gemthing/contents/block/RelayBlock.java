package gurumirum.gemthing.contents.block;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.impl.RelaySingleton;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class RelayBlock extends Block implements EntityBlock {
    public RelayBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RelayBlockEntity(pPos, pState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if(pLevel.isClientSide()) return InteractionResult.SUCCESS;
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if(!(blockEntity instanceof RelayBlockEntity)) return InteractionResult.PASS;

        RelaySingleton.getInstance().getNearRelays(pPos).forEach(relay -> {
            GemthingMod.LOGGER.info("Connected with {} {} {}", relay.getX(), relay.getY(), relay.getZ());
        });
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        RelaySingleton.getInstance().removeRelay(pos);
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }
}
