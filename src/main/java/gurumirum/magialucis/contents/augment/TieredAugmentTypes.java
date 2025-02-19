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

	public static final TieredAugmentType LUX_CAPACITY = TieredAugmentType.create("lux_capacity", 3,
			(p, i) -> p.descriptions(key -> luxCapacityDescription(i)));

	public static final TieredAugmentType SPEED = TieredAugmentType.create("speed", 3);
	public static final TieredAugmentType CASTING_SPEED = TieredAugmentType.create("casting_speed", 3);
	public static final TieredAugmentType STORAGE = TieredAugmentType.create("storage", 3);

	private static @NotNull List<@NotNull Component> luxCapacityDescription(int index) {
		return List.of(
				AugmentLogic.augmentDesc(
						Component.translatable("magialucis.augment.magialucis.lux_capacity.description",
								NumberFormats.pct(switch (index) {
									case 2 -> ItemStackLuxAcceptor.LUX_CAPACITY_3_MULTIPLIER;
									case 1 -> ItemStackLuxAcceptor.LUX_CAPACITY_2_MULTIPLIER;
									default -> ItemStackLuxAcceptor.LUX_CAPACITY_1_MULTIPLIER;
								} - 1, ChatFormatting.YELLOW)
						)
				)
		);
	}

	public static void init() {}
}
