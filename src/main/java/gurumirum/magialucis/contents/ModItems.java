package gurumirum.magialucis.contents;

import gurumirum.magialucis.contents.item.wandbelt.WandBeltItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum ModItems implements ItemLike {
	WAND_BELT(ItemProfile.customItem(WandBeltItem::new)),

	ANCIENT_CORE,

	COPPER_NUGGET(CreativeTabType.RESOURCES),

	SILVER_INGOT(CreativeTabType.RESOURCES),
	SILVER_NUGGET(CreativeTabType.RESOURCES),
	RAW_SILVER(CreativeTabType.RESOURCES),

	ELECTRUM_INGOT(CreativeTabType.RESOURCES),
	ELECTRUM_NUGGET(CreativeTabType.RESOURCES),
	ROSE_GOLD_INGOT(CreativeTabType.RESOURCES),
	ROSE_GOLD_NUGGET(CreativeTabType.RESOURCES),
	STERLING_SILVER_INGOT(CreativeTabType.RESOURCES),
	STERLING_SILVER_NUGGET(CreativeTabType.RESOURCES);

	private final DeferredItem<Item> item;
	private final CreativeTabType tab;

	ModItems() {
		this(ItemProfile.item(), CreativeTabType.MAIN);
	}
	ModItems(@NotNull ItemProfile<Item> itemProfile) {
		this(itemProfile, CreativeTabType.MAIN);
	}

	ModItems(@NotNull CreativeTabType tab) {
		this(ItemProfile.item(), tab);
	}
	ModItems(@NotNull ItemProfile<Item> itemProfile, @NotNull CreativeTabType tab) {
		this.item = itemProfile.create(name().toLowerCase(Locale.ROOT));
		this.tab = tab;
	}

	public @NotNull ResourceLocation id() {
		return this.item.getId();
	}

	@Override
	public @NotNull Item asItem() {
		return this.item.asItem();
	}

	public CreativeTabType getCreativeTab() {
		return tab;
	}

	public static void init() {}
}
