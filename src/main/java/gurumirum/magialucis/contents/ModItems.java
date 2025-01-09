package gurumirum.magialucis.contents;

import gurumirum.magialucis.contents.item.LuxBatteryItem;
import gurumirum.magialucis.contents.item.wandbelt.WandBeltItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum ModItems implements ItemLike {
	LUX_BATTERY(ItemProfile.customItem(LuxBatteryItem::new)),
	WAND_BELT(ItemProfile.customItem(WandBeltItem::new)),

	ANCIENT_CORE,

	SILVER_INGOT,
	SILVER_NUGGET,
	RAW_SILVER;

	private final DeferredItem<Item> item;

	ModItems() {
		this(ItemProfile.item());
	}
	ModItems(@NotNull ItemProfile<Item> itemProfile) {
		this.item = itemProfile.create(name().toLowerCase(Locale.ROOT));
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
