package gurumirum.gemthing.contents;

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
							for (Items i : Items.values()) {
								o.accept(i);
							}
						})
						.build());
			});
		});

		Items.init();
	}

	public enum Items implements ItemLike {
		WAND(WandItem::new),

		GEM;

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
