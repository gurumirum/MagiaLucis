package gurumirum.gemthing.contents.item.wand;

import gurumirum.gemthing.GemthingMod;
import gurumirum.gemthing.contents.Contents;
import gurumirum.gemthing.contents.block.RelayBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class ConfigurationWandItem extends Item {
	public ConfigurationWandItem(Properties properties) {
		super(properties.stacksTo(1));
	}

	@Override
	public @NotNull InteractionResult useOn(UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		BlockPos pos = context.getClickedPos();
		BlockPos relayPos = stack.get(Contents.BLOCK_POS_DATA.get());

		GemthingMod.LOGGER.info("Wrench wand item used on {}", pos);

		if (relayPos == null) {
			stack.set(Contents.BLOCK_POS_DATA.get(), pos);
		} else if (context.getLevel().getBlockEntity(pos) instanceof RelayBlockEntity relay) {
			if (relayPos == pos) return InteractionResult.PASS;
			if (!relay.linkRelay(relayPos)) return InteractionResult.PASS;
			stack.remove(Contents.BLOCK_POS_DATA.get());
		} else return InteractionResult.PASS;

		GemthingMod.LOGGER.info("Holding {}", stack.get(Contents.BLOCK_POS_DATA.get()));
		return InteractionResult.SUCCESS;
	}
}
