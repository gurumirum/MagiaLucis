package gurumirum.gemthing.contents;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.BiFunction;
import java.util.function.Function;

final class ProfileInternals {
	private ProfileInternals() {}

	public static final Function<Item.Properties, Item> defaultItemFactory = Item::new;
	public static final BiFunction<Block, Item.Properties, BlockItem> defaultBlockItemFactory = BlockItem::new;
	public static final Function<BlockBehaviour.Properties, Block> defaultBlockFactory = Block::new;
}
