package gurumirum.gemthing.contents;

import gurumirum.gemthing.capability.Gems;
import gurumirum.gemthing.capability.LuxContainerStat;
import gurumirum.gemthing.contents.item.wand.AmberTorchWandItem;
import gurumirum.gemthing.contents.item.wand.AncientLightWandItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum Wands implements ItemLike {
	ANCIENT_LIGHT(ItemProfile.customItem(AncientLightWandItem::new), null),
	AMBER_TORCH(ItemProfile.customItem(AmberTorchWandItem::new), LuxContainerStat.withGemStat(150, Gems.AMBER));

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
}
