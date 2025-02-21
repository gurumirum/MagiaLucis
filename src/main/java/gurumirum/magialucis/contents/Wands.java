package gurumirum.magialucis.contents;

import gurumirum.magialucis.api.capability.LuxContainerStat;
import gurumirum.magialucis.contents.item.wand.*;
import gurumirum.magialucis.contents.profile.ItemProfile;
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
			LuxContainerStat.withBaseStat(AmberTorchWandItem.COST_PER_LIGHT_SOURCE * 300, Gem.AMBER)),

	LESSER_ICE_STAFF(ItemProfile.customItem(LesserIceStaffItem::new, StandardWandShape.STAFF), null),

	// TODO citrine, iolite wand

	RECALL_STAFF(ItemProfile.customItem(RecallStaffWandItem::new, StandardWandShape.STAFF),
			LuxContainerStat.withBaseStat(RecallStaffWandItem.COST * 3, Gem.AQUAMARINE)),

	HEAL_WAND(ItemProfile.customItem(HealWandItem::new, StandardWandShape.WAND),
			LuxContainerStat.withBaseStat(HealWandItem.COST_PER_CAST * 5, Gem.PEARL)),

	// TODO prismarine wand

	ENDER_WAND(ItemProfile.customItem(EnderChestPortalWandItem::new, StandardWandShape.WAND),
			LuxContainerStat.withBaseStat(EnderChestPortalWandItem.BASE_TICK_COST * 20 * 300, Gem.ENDER_PEARL)),

	// TODO redstone wand

	LAPIS_SHIELD(ItemProfile.customItem(LapisShieldItem::new, StandardWandShape.WAND),
			LuxContainerStat.withBaseStat(LapisShieldItem.COST_PER_SHIELDING_TICK * 20 * 120, Gem.POLISHED_LAPIS_LAZULI)),

	DIAMOND_MACE(ItemProfile.customItem(DiamondMaceItem::new),
			LuxContainerStat.withBaseStat(DiamondMaceItem.COST_PER_ATTACK * 150, Gem.DIAMOND)),

	// TODO ruby, emerald, sapphire
	//      also the rest of the gems lol
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
				case WAND -> properties.stacksTo(1).attributes(WandAttributes.wand());
				case STAFF -> properties.stacksTo(1).attributes(WandAttributes.staff());
			}
		}
	}
}
