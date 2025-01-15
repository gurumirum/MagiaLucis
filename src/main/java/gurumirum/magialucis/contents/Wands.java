package gurumirum.magialucis.contents;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.capability.LuxContainerStat;
import gurumirum.magialucis.contents.item.wand.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Consumer;

public enum Wands implements ItemLike {
	ANCIENT_LIGHT(ItemProfile.customItem(AncientLightWandItem::new, StandardWandShape.WAND), null),
	CONFIGURATION_WAND(ItemProfile.customItem(ConfigurationWandItem::new, StandardWandShape.WAND), null),
	RED_CONFIGURATION_WAND(ItemProfile.customItem(ConfigurationWandItem::new, StandardWandShape.WAND), null),
	ICY_CONFIGURATION_WAND(ItemProfile.customItem(ConfigurationWandItem::new, StandardWandShape.WAND), null),

	AMBER_TORCH(ItemProfile.customItem(AmberTorchWandItem::new, StandardWandShape.WAND),
			LuxContainerStat.withBaseStat(AmberTorchWandItem.COST_PER_LIGHT_SOURCE * 250, GemStats.AMBER)),
	LESSER_ICE_STAFF(ItemProfile.customItem(LesserIceStaffWandItem::new, StandardWandShape.STAFF),
			LuxContainerStat.withBaseStat(LesserIceStaffWandItem.COST_PER_ATTACK * 250, GemStats.BRIGHTSTONE)),
	// citrine wand
	RECALL_STAFF(ItemProfile.customItem(RecallStaffWandItem::new, StandardWandShape.STAFF),
			LuxContainerStat.withBaseStat(RecallStaffWandItem.COST_PER_RECALL * 3, GemStats.AQUAMARINE)),

	HEAL_WAND(ItemProfile.customItem(HealWandItem::new, StandardWandShape.WAND),
			LuxContainerStat.withBaseStat(HealWandItem.COST_PER_CAST * 5, GemStats.PEARL)),

	SHIELD_WAND(ItemProfile.customItem(ShieldWandItem::new, StandardWandShape.WAND),
			LuxContainerStat.withBaseStat(ShieldWandItem.COST_PER_SHIELD * 100, GemStats.POLISHED_LAPIS_LAZULI)),

	DIAMOND_STAFF(ItemProfile.customItem(DiamondStaffItem::new, StandardWandShape.STAFF),
			LuxContainerStat.withBaseStat(DiamondStaffItem.COST_PER_DEBUFF * 10, GemStats.DIAMOND))
	;

	private final DeferredItem<Item> item;
	@Nullable
	private final LuxContainerStat luxContainerStat;

	Wands(@NotNull ItemProfile<Item> itemProfile, @Nullable LuxContainerStat luxContainerStat) {
		this.item = itemProfile.create(name().toLowerCase(Locale.ROOT));
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

	public static void init() {}

	public enum StandardWandShape implements Consumer<Item.Properties> {
		WAND,
		STAFF;

		@Override
		public void accept(Item.Properties properties) {
			switch (this) {
				case WAND -> properties.attributes(WandAttributes.wandAttributes());
				case STAFF -> properties.attributes(WandAttributes.staffAttributes());
			}
		}
	}
}
