package gurumirum.gemthing.contents;

import gurumirum.gemthing.contents.item.LuxBatteryItem;
import gurumirum.gemthing.contents.item.WandItem;
import gurumirum.gemthing.contents.item.WrenchWandItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

public enum ModItems implements ItemLike {
	WAND(WandItem::new),
	LUX_BATTERY(LuxBatteryItem::new),

	WRENCH_WAND(WrenchWandItem::new),

	SILVER_INGOT,
	SILVER_NUGGET,
	RAW_SILVER;

	private final DeferredItem<Item> item;

	ModItems() {
		this(null, null);
	}
	ModItems(@Nullable Function<Item.Properties, Item> itemFactory) {
		this(itemFactory, null);
	}
	ModItems(@Nullable Consumer<Item.Properties> properties) {
		this(null, properties);
	}
	ModItems(@Nullable Function<Item.Properties, Item> itemFactory, @Nullable Consumer<Item.Properties> properties) {
		this.item = Contents.ITEMS.register(name().toLowerCase(Locale.ROOT), () -> {
			Item.Properties p = new Item.Properties();
			if (properties != null) properties.accept(p);
			return itemFactory == null ? new Item(p) : itemFactory.apply(p);
		});
	}

	public @NotNull ResourceLocation id() {
		return this.item.getId();
	}

	@Override
	public @NotNull Item asItem() {
		return this.item.asItem();
	}

	public static void init() {}
}
