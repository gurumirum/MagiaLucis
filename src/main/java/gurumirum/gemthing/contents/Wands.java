package gurumirum.gemthing.contents;

import gurumirum.gemthing.contents.item.wand.AncientLightWandItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum Wands implements ItemLike {
	ANCIENT_LIGHT(ItemProfile.customItem(AncientLightWandItem::new))
	;

	private final DeferredItem<Item> item;

	Wands() {
		this(ItemProfile.item());
	}
	Wands(@NotNull ItemProfile<Item> itemProfile) {
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
