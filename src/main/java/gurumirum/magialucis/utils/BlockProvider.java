package gurumirum.magialucis.utils;

import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public interface BlockProvider {
	@NotNull Block block();
}
