package gurumirum.gemthing.contents;

import gurumirum.gemthing.capability.Gems;
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
	ANCIENT_LIGHT(ItemProfile.customItem(AncientLightWandItem::new, Shape.WAND), null),
	CONFIGURATION_WAND(ItemProfile.customItem(ConfigurationWandItem::new, Shape.WAND), null),
	AMBER_TORCH(ItemProfile.customItem(AmberTorchWandItem::new, Shape.WAND),
			LuxContainerStat.withSourceStat(AmberTorchWandItem.COST_PER_LIGHT_SOURCE * 250, Gems.AMBER)),
	// citrine wand
	RECALL_STAFF(ItemProfile.customItem(RecallStaffWandItem::new, Shape.STAFF),
			LuxContainerStat.withSourceStat(RecallStaffWandItem.COST_PER_RECALL * 3, Gems.AQUAMARINE));

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

	public enum Shape implements Consumer<Item.Properties> {
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
