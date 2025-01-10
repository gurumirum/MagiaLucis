package gurumirum.magialucis.contents;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static gurumirum.magialucis.MagiaLucisMod.id;

public final class ModItemTags {
	private ModItemTags() {}

	public static final TagKey<Item> WANDS = ItemTags.create(id("wands"));
	public static final TagKey<Item> BRIGHTSTONES = ItemTags.create(id("brightstones"));
	// well uh, this is the correct pluralization probably
	public static final TagKey<Item> LAPIDES_MANALIS = ItemTags.create(id("lapides_manalis"));
}
