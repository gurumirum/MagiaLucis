package gurumirum.magialucis.contents.data;

import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Augment(
		@NotNull Component name,
		@NotNull List<Component> description,
		@NotNull HolderSet<Augment> precursor,
		@NotNull HolderSet<Augment> incompatible
) {}
