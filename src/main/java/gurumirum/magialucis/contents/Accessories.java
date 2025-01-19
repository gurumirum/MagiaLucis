package gurumirum.magialucis.contents;

import gurumirum.magialucis.capability.GemStats;
import gurumirum.magialucis.capability.LuxContainerStat;
import gurumirum.magialucis.contents.item.accessory.FireImmuneRubyBracelet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum Accessories implements ItemLike {
	FIRE_IMMUNE_BRACELET(ItemProfile.customItem(FireImmuneRubyBracelet::new), ModCurioSlots.BRACELET,
			LuxContainerStat.withBaseStat(FireImmuneRubyBracelet.COST_PER_FIRE_RESISTANCE * 20 * 8, GemStats.RUBY));

	private final DeferredItem<Item> item;
	private final String curioSlot;
	@Nullable
	private final LuxContainerStat luxContainerStat;

	Accessories(@NotNull ItemProfile<Item> itemProfile,@NotNull String curioSlot, @Nullable LuxContainerStat luxContainerStat) {
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

	public @NotNull String curioSlot() {return this.curioSlot;}

	public static void init() {}
}
