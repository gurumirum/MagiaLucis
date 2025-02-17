package gurumirum.magialucis.contents.profile;

import gurumirum.magialucis.contents.Contents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface ItemProfile<I extends Item> {
	ItemProfile<Item> DEFAULT = item(null);

	static ItemProfile<Item> item() {
		return DEFAULT;
	}

	static ItemProfile<Item> item(@Nullable Consumer<Item.Properties> properties) {
		return customItem(ProfileInternals.defaultItemFactory, properties);
	}

	static <I extends Item> ItemProfile<I> customItem(@NotNull Function<Item.Properties, I> itemFactory) {
		return customItem(itemFactory, null);
	}

	static <I extends Item> ItemProfile<I> customItem(@NotNull Function<Item.Properties, I> itemFactory,
	                                                  @Nullable Consumer<Item.Properties> properties) {
		return id -> Contents.ITEMS.register(id, () -> {
			Item.Properties p = new Item.Properties();
			if (properties != null) properties.accept(p);
			return itemFactory.apply(p);
		});
	}

	static <B extends Block> ItemProfile<BlockItem> blockItem(@NotNull DeferredBlock<B> deferredBlock) {
		return blockItem(deferredBlock, null);
	}

	static <B extends Block> ItemProfile<BlockItem> blockItem(
			@NotNull DeferredBlock<B> deferredBlock,
			@Nullable Consumer<Item.Properties> properties
	) {
		return customBlockItem(deferredBlock, ProfileInternals.defaultBlockItemFactory, properties);
	}

	static <B extends Block, I extends BlockItem> ItemProfile<I> customBlockItem(
			@NotNull DeferredBlock<B> deferredBlock,
			@NotNull BiFunction<B, Item.Properties, I> itemFactory
	) {
		return customBlockItem(deferredBlock, itemFactory, null);
	}

	static <B extends Block, I extends BlockItem> ItemProfile<I> customBlockItem(
			@NotNull DeferredBlock<B> deferredBlock,
			@NotNull BiFunction<? super B, Item.Properties, I> itemFactory,
			@Nullable Consumer<Item.Properties> properties
	) {
		return id -> Contents.ITEMS.register(id, () -> {
			Item.Properties p = new Item.Properties();
			if (properties != null) properties.accept(p);
			return itemFactory.apply(deferredBlock.get(), p);
		});
	}

	@NotNull DeferredItem<I> create(@NotNull String id);
}
