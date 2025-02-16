package gurumirum.magialucis.contents;

import gurumirum.magialucis.api.capability.LuxContainerStat;
import gurumirum.magialucis.contents.item.accessory.*;
import gurumirum.magialucis.contents.item.wandbelt.WandBeltItem;
import gurumirum.magialucis.contents.profile.ItemProfile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum Accessories implements ItemLike {
	WAND_BELT(ItemProfile.customItem(WandBeltItem::new), ModCurioSlots.WAND_BELT),

	DRUID_WREATH(ItemProfile.customItem(p -> new AmberWreathItem(p, true)), ModCurioSlots.HEADWEAR,
			LuxContainerStat.withBaseStat(AmberWreathItem.COST_PER_UPDATE * 1500, Gem.AMBER)),

	DRYAD_WREATH(ItemProfile.customItem(p -> new AmberWreathItem(p, false)), ModCurioSlots.HEADWEAR,
			LuxContainerStat.withBaseStat(AmberWreathItem.COST_PER_UPDATE * 1500, Gem.AMBER)),

	FIRE_ARROW_RING(ItemProfile.customItem(FireArrowRingItem::new), ModCurioSlots.RING),

	SOUL_CROWN(ItemProfile.customItem(SoulCrownItem::new), ModCurioSlots.HEADWEAR),

	SPEED_RING(ItemProfile.customItem(p -> new SpeedBoostCurioItem(p,
			0.1,
			60,
			1)), ModCurioSlots.RING,
			LuxContainerStat.withBaseStat(1000, Gem.CITRINE)),

	OBSIDIAN_BRACELET(ItemProfile.customItem(ObsidianBraceletItem::new), ModCurioSlots.BRACELET,
			LuxContainerStat.withBaseStat(ObsidianBraceletItem.COST_PER_FIRE_RESISTANCE * 20 * 30, Gem.OBSIDIAN)),

	SHIELD_NECKLACE(ItemProfile.customItem(ShieldCurioItem::new), ModCurioSlots.NECKLACE,
			LuxContainerStat.withBaseStat(ShieldCurioItem.COST_PER_IMPACT * 10, Gem.POLISHED_LAPIS_LAZULI)),
	;

	private final DeferredItem<Item> item;
	private final String curioSlot;
	@Nullable
	private final LuxContainerStat luxContainerStat;

	Accessories(@NotNull ItemProfile<Item> itemProfile, @NotNull String curioSlot) {
		this(itemProfile, curioSlot, null);
	}
	Accessories(@NotNull ItemProfile<Item> itemProfile, @NotNull String curioSlot, @Nullable LuxContainerStat luxContainerStat) {
		this.item = itemProfile.create(name().toLowerCase(Locale.ROOT));
		this.curioSlot = curioSlot;
		this.luxContainerStat = luxContainerStat;
	}

	public @NotNull ResourceLocation id() {
		return this.item.getId();
	}

	@Override
	public @NotNull Item asItem() {
		return this.item.asItem();
	}

	public @Nullable LuxContainerStat luxContainerStat() {
		return luxContainerStat;
	}

	public @NotNull String curioSlot() {
		return this.curioSlot;
	}

	public static void init() {}
}
