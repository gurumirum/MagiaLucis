package gurumirum.gemthing.contents;

import gurumirum.gemthing.capability.GemStat;
import gurumirum.gemthing.contents.item.WandItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

import static gurumirum.gemthing.GemthingMod.MODID;
import static gurumirum.gemthing.GemthingMod.id;

public final class Contents {
	private Contents() {}

	private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
	private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(MODID);
	private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

	public static void init(IEventBus eventBus) {
		ITEMS.register(eventBus);
		DATA_COMPONENTS.register(eventBus);
		BLOCKS.register(eventBus);

		eventBus.addListener((RegisterEvent event) -> {
			event.register(Registries.CREATIVE_MODE_TAB, h -> {
				h.register(id("main"), CreativeModeTab.builder()
						.icon(() -> new ItemStack(Items.WAND))
						.displayItems((p, o) -> {
							for (var i : Items.values()) o.accept(i);

						})
						.build());
			});
			event.register(Registries.CREATIVE_MODE_TAB, h -> {
				h.register(id("gems"), CreativeModeTab.builder()
						.icon(() -> new ItemStack(Gems.BRIGHTSTONE))
						.displayItems((p, o) -> {
							for (var g : GemStat.values()) o.accept(g.item());
						})
						.build());
			});
		});

		Gems.init();
		Items.init();
	}

	public enum Gems implements ItemLike {
		BRIGHTSTONE(GemStat.BRIGHTSTONE),

		AMBER(GemStat.AMBER),
		CITRINE(GemStat.CITRINE),
		AQUAMARINE(GemStat.AQUAMARINE),
		PEARL(GemStat.PEARL),

		PURIFIED_QUARTZ(GemStat.PURIFIED_QUARTZ),
		CRYSTALLIZED_REDSTONE(GemStat.CRYSTALLIZED_REDSTONE),
		POLISHED_LAPIS_LAZULI(GemStat.POLISHED_LAPIS_LAZULI),
		OBSIDIAN(GemStat.OBSIDIAN),

		// diamond
		RUBY(GemStat.RUBY),
		// emerald
		SAPPHIRE(GemStat.SAPPHIRE),

		// amethyst
		TOPAZ(GemStat.TOPAZ),
		MOONSTONE(GemStat.MOONSTONE),
		JET(GemStat.JET),

		BRILLIANT_DIAMOND(GemStat.BRILLIANT_DIAMOND),
		RUBY2(GemStat.RUBY2),
		EMERALD2(GemStat.EMERALD2),
		SAPPHIRE2(GemStat.SAPPHIRE2),

		DAIMONIUM(GemStat.DAIMONIUM);

		public final GemStat stat;
		private final DeferredItem<Item> item;

		Gems(GemStat stat) {
			this(stat, null, null);
		}
		Gems(GemStat stat, @Nullable Function<Item.Properties, Item> itemFactory) {
			this(stat, itemFactory, null);
		}
		Gems(GemStat stat, @Nullable Consumer<Item.Properties> properties) {
			this(stat, null, properties);
		}
		Gems(GemStat stat, @Nullable Function<Item.Properties, Item> itemFactory, @Nullable Consumer<Item.Properties> properties) {
			this.stat = stat;
			this.item = ITEMS.register(name().toLowerCase(Locale.ROOT), () -> {
				Item.Properties p = new Item.Properties();
				if (properties != null) properties.accept(p);
				return itemFactory == null ? new Item(p) : itemFactory.apply(p);
			});
		}

		@NotNull
		public ResourceLocation id() {
			return this.item.getId();
		}

		@Override
		@NotNull
		public Item asItem() {
			return this.item.asItem();
		}

		public static void init() {}
	}

	public enum Items implements ItemLike {
		WAND(WandItem::new);

		private final DeferredItem<Item> item;

		Items() {
			this(null, null);
		}
		Items(@Nullable Function<Item.Properties, Item> itemFactory) {
			this(itemFactory, null);
		}
		Items(@Nullable Consumer<Item.Properties> properties) {
			this(null, properties);
		}
		Items(@Nullable Function<Item.Properties, Item> itemFactory, @Nullable Consumer<Item.Properties> properties) {
			this.item = ITEMS.register(name().toLowerCase(Locale.ROOT), () -> {
				Item.Properties p = new Item.Properties();
				if (properties != null) properties.accept(p);
				return itemFactory == null ? new Item(p) : itemFactory.apply(p);
			});
		}

		@NotNull
		public ResourceLocation id() {
			return this.item.getId();
		}

		@Override
		@NotNull
		public Item asItem() {
			return this.item.asItem();
		}

		public static void init() {}
	}
}
