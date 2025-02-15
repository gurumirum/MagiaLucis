package gurumirum.magialucis.contents.data;

import gurumirum.magialucis.contents.Accessories;
import gurumirum.magialucis.contents.Augments;
import gurumirum.magialucis.contents.Wands;
import gurumirum.magialucis.utils.AugmentProvider;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

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

	private static void addWandAugment(List<AugmentProvider> augments, Wands wand) {
		if (wand.luxContainerStat() != null) {
			augments.add(Augments.LUX_CAPACITY_1);
			augments.add(Augments.LUX_CAPACITY_2);
			augments.add(Augments.LUX_CAPACITY_3);
		}

		switch (wand) {
			case ENDER_WAND -> augments.add(Augments.IDK);
		}
	}

	private static void addAccessoryAugment(List<AugmentProvider> augments, Accessories acc) {
		if (acc.luxContainerStat() != null) {
			augments.add(Augments.LUX_CAPACITY_1);
			augments.add(Augments.LUX_CAPACITY_2);
			augments.add(Augments.LUX_CAPACITY_3);
		}
	}

	private static <T> void collect(T t,
	                                BiConsumer<List<AugmentProvider>, T> builder,
	                                BiConsumer<Set<Holder<Augment>>, T> register) {
		List<AugmentProvider> list = new ArrayList<>();
		builder.accept(list, t);
		if (list.isEmpty()) return;
		register.accept(list.stream()
				.map(AugmentProvider::augment)
				.collect(Collectors.toSet()), t);
	}
}
