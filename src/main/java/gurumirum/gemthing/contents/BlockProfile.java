package gurumirum.gemthing.contents;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

import static gurumirum.gemthing.contents.ProfileInternals.defaultBlockFactory;

@FunctionalInterface
public interface BlockProfile<B extends Block, I extends BlockItem> {
	static BlockProfile<Block, BlockItem> block(@NotNull Properties properties) {
		return block(() -> properties);
	}

	static BlockProfile<Block, BlockItem> block(@NotNull Supplier<Properties> properties) {
		return customBlock(defaultBlockFactory, properties);
	}

	static <B extends Block> BlockProfile<B, BlockItem> customBlock(
			@NotNull Function<Properties, B> blockFactory,
			@NotNull Properties properties
	) {
		return customBlock(blockFactory, () -> properties);
	}

	static <B extends Block> BlockProfile<B, BlockItem> customBlock(
			@NotNull Function<Properties, B> blockFactory,
			@NotNull Supplier<Properties> properties
	) {
		return customBlockWithItem(blockFactory, properties, ItemProfile::blockItem);
	}

	static <B extends Block, I extends BlockItem> BlockProfile<B, I> customBlockWithItem(
			@NotNull Function<Properties, B> blockFactory,
			@NotNull Supplier<Properties> properties,
			@Nullable Function<DeferredBlock<? extends B>, ItemProfile<I>> itemProfileProvider
	) {
		return customBlockWithoutItem(blockFactory, properties).withItem(itemProfileProvider);
	}

	static <B extends Block> BlockProfile<B, BlockItem> customBlockWithoutItem(
			@NotNull Function<Properties, B> blockFactory,
			@NotNull Supplier<Properties> properties
	) {
		return id -> Contents.BLOCKS.register(id, () -> blockFactory.apply(properties.get()));
	}

	@NotNull DeferredBlock<B> create(@NotNull String id);

	default @Nullable DeferredItem<I> createItem(@NotNull DeferredBlock<? extends B> deferredBlock) {
		return null;
	}

	@SuppressWarnings("unchecked")
	default <I2 extends BlockItem> BlockProfile<B, I2> withItem(
			@Nullable Function<DeferredBlock<? extends B>, ItemProfile<I2>> itemProfileProvider
	) {
		if (itemProfileProvider == null) return (BlockProfile<B, I2>)this;
		return new BlockProfile<>() {
			@Override
			public @NotNull DeferredBlock<B> create(@NotNull String id) {
				return BlockProfile.this.create(id);
			}

			@Override
			public @NotNull DeferredItem<I2> createItem(@NotNull DeferredBlock<? extends B> deferredBlock) {
				return itemProfileProvider.apply(deferredBlock).create(deferredBlock.getId().getPath());
			}

			@Override
			public <I21 extends BlockItem> BlockProfile<B, I21> withItem(
					@Nullable Function<DeferredBlock<? extends B>, ItemProfile<I21>> itemProfileProvider
			) {
				return BlockProfile.this.withItem(itemProfileProvider);
			}
		};
	}
}
