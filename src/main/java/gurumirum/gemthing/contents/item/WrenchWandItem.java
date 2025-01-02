package gurumirum.gemthing.contents.item;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.contents.block.RelayBlock;
import gurumirum.gemthing.contents.block.RelayBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class WrenchWandItem extends Item {

    public WrenchWandItem(Properties properties) {
        super(properties.stacksTo(1).component(Contents.BLOCK_POS_DATA, new BlockPos(0, 0, 0)));
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if(pContext.getLevel().isClientSide()) return InteractionResult.SUCCESS;
        ItemStack stack = pContext.getItemInHand();
        BlockPos pos = pContext.getClickedPos();
        BlockPos relayPos = stack.get(Contents.BLOCK_POS_DATA.get());
        GemthingMod.LOGGER.info("Wrench wand item used on " + pos);
        if(!(pContext.getLevel().getBlockState(pos).getBlock() instanceof RelayBlock)) return InteractionResult.PASS;
        if(relayPos.equals(BlockPos.ZERO)) {
            stack.set(Contents.BLOCK_POS_DATA.get(), pos);
        } else {
            RelayBlockEntity relayBlockEntity = (RelayBlockEntity) pContext.getLevel().getBlockEntity(pos);
            if(relayPos == pos) return InteractionResult.PASS;
            if(relayBlockEntity == null) return InteractionResult.PASS;
            if(!relayBlockEntity.linkRelay(relayPos)) return InteractionResult.PASS;
            stack.set(Contents.BLOCK_POS_DATA.get(), BlockPos.ZERO);
        }
        GemthingMod.LOGGER.info("Holding " + stack.get(Contents.BLOCK_POS_DATA.get()));
        return InteractionResult.SUCCESS;
    }
}
