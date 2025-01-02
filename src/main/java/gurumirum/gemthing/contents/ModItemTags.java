package gurumirum.gemthing.contents;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static gurumirum.gemthing.GemthingMod.id;

public final class ModItemTags {
	private ModItemTags() {}

	public static final TagKey<Item> WANDS = ItemTags.create(id("wands"));
}
