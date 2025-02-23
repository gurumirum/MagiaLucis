package gurumirum.magialucis.contents.data;

import gurumirum.magialucis.api.augment.Augment;
import gurumirum.magialucis.contents.Accessories;
import gurumirum.magialucis.contents.Wands;
import gurumirum.magialucis.utils.AugmentProvider;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static gurumirum.magialucis.contents.Augments.*;

public final class AugmentSpecGen {
	private AugmentSpecGen() {}

	public static void add(DataMapProvider.Builder<Set<Holder<Augment>>, Item> b) {
		for (Wands wand : Wands.values()) {
			collect(wand, AugmentSpecGen::addWandAugment,
					(set, w) -> b.add(w.id(), set, false));
		}

		for (Accessories accessory : Accessories.values()) {
			collect(accessory, AugmentSpecGen::addAccessoryAugment,
					(set, a) -> b.add(a.id(), set, false));
		}
	}

	private static void addWandAugment(Collector c, Wands wand) {
		if (wand.luxContainerStat() != null) c.add(OVERCHARGE_1, OVERCHARGE_2, OVERCHARGE_3);

		switch (wand) {
			case ANCIENT_LIGHT -> c.add(ACCELERATION_1);
			case CONFIGURATION_WAND, RED_CONFIGURATION_WAND, ICY_CONFIGURATION_WAND ->
					c.add(CONFIGURATION_WAND_DEBUG_VIEW);
			case AMBER_TORCH -> c.add(AMBER_TORCH_CONCEALED_FLAME);
			case LESSER_ICE_STAFF -> c.add(QUICK_CAST_1);
			case RECALL_STAFF -> c.add(QUICK_CAST_1, QUICK_CAST_2, QUICK_CAST_3);
			case HEAL_WAND -> c.add(QUICK_CAST_1, QUICK_CAST_2, QUICK_CAST_3);
			case ENDER_WAND -> c
					.add(EXPANSION_1, EXPANSION_2, EXPANSION_3)
					.add(ENDER_WAND_COLLECTOR);
		}
	}

	private static void addAccessoryAugment(Collector c, Accessories acc) {
		if (acc.luxContainerStat() != null) c.add(OVERCHARGE_1, OVERCHARGE_2, OVERCHARGE_3);

		switch (acc) {
			case SHIELD_NECKLACE -> c.add(SHIELD_NECKLACE_EXPLOSION);
		}
	}

	private static <T> void collect(T t,
	                                BiConsumer<Collector, T> builder,
	                                BiConsumer<Set<Holder<Augment>>, T> register) {
		Collector collector = new Collector(new ArrayList<>());
		builder.accept(collector, t);
		if (collector.augments.isEmpty()) return;

		ObjectLinkedOpenHashSet<Holder<Augment>> set = new ObjectLinkedOpenHashSet<>();
		for (AugmentProvider p : collector.augments) set.add(p.augment());

		register.accept(set, t);
	}

	private record Collector(List<AugmentProvider> augments) {
		public Collector add(AugmentProvider... augments) {
			this.augments.addAll(List.of(augments));
			return this;
		}
	}
}
