package gurumirum.magialucis.contents;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.capability.LuxContainerStat;
import gurumirum.magialucis.contents.item.accessory.AccessoryEventListener;
import gurumirum.magialucis.contents.item.accessory.AttributeModifyRingItem;
import gurumirum.magialucis.contents.item.accessory.DamageAbsorbNecklaceItem;
import gurumirum.magialucis.contents.item.accessory.LuxContainerCurioItem;
import gurumirum.magialucis.contents.item.wandbelt.WandBeltItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum Accessories implements ItemLike {
	WAND_BELT(ItemProfile.customItem(WandBeltItem::new), ModCurioSlots.WAND_BELT),

	FIRE_IMMUNE_BRACELET(ItemProfile.customItem(LuxContainerCurioItem::new), ModCurioSlots.BRACELET,
			LuxContainerStat.withBaseStat(AccessoryEventListener.COST_PER_FIRE_RESISTANCE * 20 * 8, GemStats.RUBY)),

	DAMAGE_ABSORB_NECKLACE(ItemProfile.customItem(DamageAbsorbNecklaceItem::new), ModCurioSlots.NECKLACE,
			LuxContainerStat.withBaseStat(DamageAbsorbNecklaceItem.COST_PER_IMPACT * 10, GemStats.POLISHED_LAPIS_LAZULI)),

	MOVEMENT_SPEED_RING(ItemProfile.customItem(p -> new AttributeModifyRingItem(p, 1).addAttribute(Attributes.MOVEMENT_SPEED,
			new AttributeModifier(ResourceLocation.withDefaultNamespace("effect.speed"), 0.02, AttributeModifier.Operation.ADD_VALUE))), ModCurioSlots.RING,
			LuxContainerStat.withBaseStat(1000, GemStats.CITRINE));

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
