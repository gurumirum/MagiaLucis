package gurumirum.magialucis.utils;

import gurumirum.magialucis.api.augment.Augment;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;

public interface AugmentProvider {
	@NotNull Holder<Augment> augment();
}
