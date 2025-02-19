package gurumirum.magialucis.contents.augment;

import gurumirum.magialucis.capability.ItemStackLuxAcceptor;
import gurumirum.magialucis.contents.data.AugmentLogic;
import gurumirum.magialucis.utils.NumberFormats;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class TieredAugmentTypes {
	private TieredAugmentTypes() {}

	public static final TieredAugmentType OVERCHARGE = TieredAugmentType.create("overcharge", 3,
			(p, i) -> p.descriptions(key -> luxCapacityDescription(i)));

	public static final TieredAugmentType ACCELERATION = TieredAugmentType.create("acceleration", 3);
	public static final TieredAugmentType QUICK_CAST = TieredAugmentType.create("quick_cast", 3);
	public static final TieredAugmentType EXPANSION = TieredAugmentType.create("expansion", 3);

	private static @NotNull List<@NotNull Component> luxCapacityDescription(int index) {
		return List.of(
				AugmentLogic.augmentDesc(
						Component.translatable("magialucis.augment.magialucis.overcharge.description",
								NumberFormats.pct(switch (index) {
									case 2 -> ItemStackLuxAcceptor.OVERCHARGE_3_MULTIPLIER;
									case 1 -> ItemStackLuxAcceptor.OVERCHARGE_2_MULTIPLIER;
									default -> ItemStackLuxAcceptor.OVERCHARGE_1_MULTIPLIER;
								} - 1, ChatFormatting.YELLOW)
						)
				)
		);
	}

	public static void init() {}
}
