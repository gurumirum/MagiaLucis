package gurumirum.gemthing.contents;

import gurumirum.gemthing.capability.GemStats;
import gurumirum.gemthing.capability.LuxContainerStat;
import gurumirum.gemthing.contents.item.wand.*;
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
	AMBER_TORCH(ItemProfile.customItem(AmberTorchWandItem::new, StandardWandShape.WAND),
			LuxContainerStat.withSourceStat(AmberTorchWandItem.COST_PER_LIGHT_SOURCE * 250, GemStats.AMBER)),
	// citrine wand
	RECALL_STAFF(ItemProfile.customItem(RecallStaffWandItem::new, StandardWandShape.STAFF),
			LuxContainerStat.withSourceStat(RecallStaffWandItem.COST_PER_RECALL * 3, GemStats.AQUAMARINE)),

	HEAL_WAND(ItemProfile.customItem(HealWandItem::new, StandardWandShape.WAND),
			LuxContainerStat.withSourceStat(HealWandItem.COST_PER_CAST * 5, GemStats.PEARL))
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
