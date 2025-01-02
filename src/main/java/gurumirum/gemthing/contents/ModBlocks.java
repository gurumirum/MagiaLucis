package gurumirum.gemthing.contents;

import gurumirum.gemthing.contents.block.RelayBlock;
import gurumirum.gemthing.contents.block.RemoteChargerBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public enum ModBlocks implements ItemLike {
	SILVER(() -> Properties.ofFullCopy(Blocks.IRON_BLOCK).instrument(NoteBlockInstrument.BELL)),
	RAW_SILVER_BLOCK(() -> Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK)),

	REMOTE_CHARGER(RemoteChargerBlock::new, () -> Properties.of()),
	RELAY(RelayBlock::new, () -> Properties.of())
	;

	private final DeferredBlock<Block> block;
	@Nullable
	private final DeferredItem<BlockItem> item;

	ModBlocks(@Nullable Supplier<Properties> properties) {
		this(null, properties);
	}
	ModBlocks(@Nullable Function<Properties, Block> blockFactory,
	          @Nullable Supplier<Properties> properties) {
		this(blockFactory, properties, Contents.defaultItemFactory, null);
	}
	ModBlocks(@Nullable Function<Properties, Block> blockFactory,
	          @Nullable Supplier<Properties> properties,
	          @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory,
	          @Nullable Consumer<Item.Properties> itemProperties) {
		String id = name().toLowerCase(Locale.ROOT);
		this.block = Contents.BLOCKS.register(id, () -> {
			Properties p = properties != null ? properties.get() : Properties.of();
			return blockFactory == null ? new Block(p) : blockFactory.apply(p);
		});
		this.item = itemFactory == null ? null : Contents.ITEMS.register(id, () -> {
			Item.Properties p = new Item.Properties();
			if (itemProperties != null) itemProperties.accept(p);
			return itemFactory.apply(this.block.get(), p);
		});
	}

	public @NotNull ResourceLocation id() {
		return this.block.getId();
	}

	public @NotNull Block block() {
		return this.block.get();
	}

	@Override
	public @NotNull Item asItem() {
		return this.item != null ? this.item.get() : net.minecraft.world.item.Items.AIR;
	}

	public @Nullable BlockItem blockItem() {
		return this.item != null ? this.item.get() : null;
	}

	public static void init() {}
}
